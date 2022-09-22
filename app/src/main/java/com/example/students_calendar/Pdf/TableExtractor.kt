package com.example.students_calendar.Pdf

import com.example.students_calendar.Pdf.data.Table
import com.example.students_calendar.Pdf.data.TableCell
import com.example.students_calendar.Pdf.data.TableRow
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.HashMultimap
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.LinkedListMultimap
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.Multimap
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.Range
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import com.tom_roush.pdfbox.text.TextPosition
import java.io.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue


class PDFTableExtractor() {
    private val extractedPages: MutableList<Int> = ArrayList()
    private val exceptedPages: MutableList<Int> = ArrayList()

    private val pageNExceptedLinesMap: Multimap<Int, Int> = HashMultimap.create()
    private var inputStream: InputStream? = null
    private var document: PDDocument? = null
    private var password: String? = null

    fun setSource(file: File?): PDFTableExtractor {
        try {
            this.inputStream = FileInputStream(file)
            return this
        } catch (ex: FileNotFoundException) {
            throw RuntimeException("Invalid pdf file", ex)
        }
    }

    fun extract(indx:Int): Table {
        val retVal: MutableList<Table> = ArrayList()
        val pageIdNLineRangesMap: Multimap<Int, Range<Int>> = LinkedListMultimap.create()
        val pageIdNTextsMap: Multimap<Int, TextPosition> = LinkedListMultimap.create()
        val pageId = indx
        try {
            document =
                if (password != null) PDDocument.load(inputStream, password) else PDDocument.load(
                    inputStream
                )
                val b =
                    !exceptedPages.contains(pageId) && (extractedPages.isEmpty() || extractedPages.contains(
                        pageId
                    ))
                if (b) {
                    val texts = extractTextPositions(pageId) //sorted by .getY() ASC
                    //extract line ranges
                    val lineRanges: List<Range<Int>> = getLineRanges(pageId, texts)
                    //extract column ranges
                    val textsByLineRanges = getTextsByLineRanges(lineRanges, texts)
                    pageIdNLineRangesMap.putAll(pageId, lineRanges)
                    pageIdNTextsMap.putAll(pageId, textsByLineRanges)
                }
            //Calculate columnRanges
            val columnRanges: List<Range<Int>> = getColumnRanges(pageIdNTextsMap.values())
            for (pageId: Int in pageIdNTextsMap.keySet()) {
                val table: Table = buildTable(
                    pageId,
                    pageIdNTextsMap.get(pageId).toList(),
                    pageIdNLineRangesMap.get(pageId).toList(),
                    columnRanges
                )
                retVal.add(table)
            }
        } catch (ex: IOException) {
            throw RuntimeException("Parse pdf file fail", ex)
        } finally {
            if (document != null) {
                try {
                    document!!.close()
                } catch (ex: IOException) {
                }
            }
        }
        return retVal.first()
    }

    private fun buildTable(
        pageIdx: Int, tableContent: List<TextPosition>,
        rowTrapRanges: List<Range<Int>>, columnTrapRanges: List<Range<Int>>
    ): Table {
        val retVal = Table(pageIdx, columnTrapRanges.size)
        var idx = 0
        var rowIdx = 0
        val rowContent: MutableList<TextPosition> = ArrayList()
        while (idx < tableContent.size) {
            val textPosition = tableContent[idx]
            val rowTrapRange: Range<Int> = rowTrapRanges[rowIdx]
            val textRange: Range<Int> =
                Range.closed(textPosition.y.toInt(), (textPosition.y + textPosition.height).toInt())
            if (rowTrapRange.encloses(textRange)) {
                rowContent.add(textPosition)
                idx++
            } else {
                val row: TableRow = buildRow(rowIdx, rowContent, columnTrapRanges)
                retVal.rows.add(row)
                //next row: clear rowContent
                rowContent.clear()
                rowIdx++
            }
        }
        //last row
        if (!rowContent.isEmpty() && rowIdx < rowTrapRanges.size) {
            val row: TableRow = buildRow(rowIdx, rowContent, columnTrapRanges)
            retVal.rows.add(row)

        }
        //return
        return retVal
    }

    private fun buildRow(
        rowIdx: Int,
        rowContent: List<TextPosition>,
        columnTrapRanges: List<Range<Int>>
    ): TableRow {
        val retVal = TableRow(rowIdx)
        //Sort rowContent
        Collections.sort(rowContent
        ) { a, b ->
            when {
                (a!!.x < b!!.x) -> -1
                (a.x > b.x) -> 1
                else -> 0
            }
        }
        var idx = 0
        var columnIdx = 0
        val cellContent: MutableList<TextPosition> = ArrayList()
        while (idx < rowContent.size) {
            val textPosition = rowContent[idx]
            val columnTrapRange: Range<Int> = columnTrapRanges[columnIdx]
            val textRange: Range<Int> =
                Range.closed(textPosition.x.toInt(), (textPosition.x + textPosition.width).toInt())
            if (columnTrapRange.encloses(textRange)) {
                cellContent.add(textPosition)
                idx++
            } else {
                val cell: TableCell = buildCell(columnIdx, cellContent)
                retVal.cells.add(cell)
                //next column: clear cell content
                cellContent.clear()
                columnIdx++
            }
        }
        if (!cellContent.isEmpty() && columnIdx < columnTrapRanges.size) {
            val cell: TableCell = buildCell(columnIdx, cellContent)
            retVal.cells.add(cell)
        }
        return retVal
    }

    private fun buildCell(columnIdx: Int, cellContent: List<TextPosition>): TableCell {
        Collections.sort(cellContent)
        { a, b ->
            when {
                (a!!.y < b!!.y) -> -1
                (a!!.y > b!!.y) -> 1
                (a!!.x < b!!.x)&&(a!!.y == b!!.y) -> -1
                (a.x > b.x)&&(a!!.y == b!!.y) -> 1
                else -> 0
            }
        }
        //String cellContentString = Joiner.on("").join(cellContent.stream().map(e -> e.getCharacter()).iterator());
        val cellContentBuilder = StringBuilder()
        var prevY:Float = 0f
        var prevX:Float = 0f
        if(cellContent.size>0){
            prevY = cellContent[0].y
            prevX = cellContent[0].x
        }
        for (textPosition: TextPosition in cellContent) {
            if(textPosition.y- prevY>0 || (textPosition.x-prevX).absoluteValue>textPosition.width*2)
                cellContentBuilder.append(" ")
            cellContentBuilder.append(textPosition.unicode)
            prevY = textPosition.y
            prevX = textPosition.x
        }
        val cellContentString = cellContentBuilder.toString()
        return TableCell(columnIdx, cellContentString)
    }

    @Throws(IOException::class)
    private fun extractTextPositions(pageId: Int): List<TextPosition> {
        val extractor = TextPositionExtractor(document, pageId)
        return extractor.extract()
    }

    private fun isExceptedLine(pageIdx: Int, lineIdx: Int): Boolean {
        return (pageNExceptedLinesMap.containsEntry(pageIdx, lineIdx)
                || pageNExceptedLinesMap.containsEntry(-1, lineIdx))
    }

    private fun getTextsByLineRanges(
        lineRanges: List<Range<Int>>,
        textPositions: List<TextPosition>
    ): List<TextPosition> {
        val retVal: MutableList<TextPosition> = ArrayList()
        var idx = 0
        var lineIdx = 0
        while (idx < textPositions.size && lineIdx < lineRanges.size) {
            val textPosition = textPositions[idx]
            val textRange: Range<Int> =
                Range.closed(textPosition.y.toInt(), (textPosition.y + textPosition.height).toInt())
            val lineRange: Range<Int> = lineRanges[lineIdx]
            if (lineRange.encloses(textRange)) {
                retVal.add(textPosition)
                idx++
            } else if (lineRange.upperEndpoint() < textRange.lowerEndpoint()) {
                lineIdx++
            } else {
                idx++
            }
        }
        return retVal
    }

    private fun getColumnRanges(texts: Collection<TextPosition>): List<Range<Int>> {
        val rangesBuilder = TrapRangeBuilder()
        for (text: TextPosition in texts) {
            val range: Range<Int> = Range.closed(text.x.toInt()-1, (text.x + text.width).toInt()+1)
            rangesBuilder.addRange(range)
        }
        return rangesBuilder.build()
    }

    private fun getLineRanges(
        pageId: Int,
        pageContent: List<TextPosition>
    ): List<Range<Int>> {
        val lineTrapRangeBuilder = TrapRangeBuilder()
        for (textPosition: TextPosition in pageContent) {
            val lineRange: Range<Int> = Range.closed(
                textPosition.y.toInt(),
                (textPosition.y + textPosition.height).toInt()
            )
            //add to builder
            lineTrapRangeBuilder.addRange(lineRange)
        }
        val lineTrapRanges: List<Range<Int>> =
            lineTrapRangeBuilder.build()
        return removeExceptedLines(pageId, lineTrapRanges)
    }

    private fun removeExceptedLines(
        pageIdx: Int,
        lineTrapRanges: List<Range<Int>>
    ): List<Range<Int>> {
        val retVal: MutableList<Range<Int>> = ArrayList()
        for (lineIdx in lineTrapRanges.indices) {
            val isExceptedLine = (isExceptedLine(pageIdx, lineIdx)
                    || isExceptedLine(pageIdx, lineIdx - lineTrapRanges.size))
            if (!isExceptedLine) {
                retVal.add(lineTrapRanges[lineIdx])
            }
        }
        return retVal
    }

    private class TextPositionExtractor(document: PDDocument?, pageId: Int) :
        PDFTextStripper() {
        private val textPositions: MutableList<TextPosition> = ArrayList()
        private val pageId: Int
        @Throws(IOException::class)
        fun stripPage(pageId: Int) {
            startPage = pageId + 1
            endPage = pageId + 1
            OutputStreamWriter(ByteArrayOutputStream()).use { writer ->
                writeText(
                    document,
                    writer
                )
            }
        }

        @Throws(IOException::class)
        override fun writeString(string: String, textPositions: List<TextPosition>) {
            this.textPositions.addAll(textPositions)
        }

        /**
         * and order by textPosition.getY() ASC
         *
         * @return
         * @throws IOException
         */
        @Throws(IOException::class)
        fun extract(): List<TextPosition> {
            stripPage(pageId)
            //sort
            textPositions.sortWith { a, b ->
                when {
                    (a!!.y < b!!.y) -> -1
                    (a.y > b.y) -> 1
                    else -> 0
                }
            }
            textPositions.removeAll {
                it.x == 16.321081f ||
                        it.y<=146.04004||
                        it.y>=814.55993
            }
            return textPositions
        }

        init {
            super.setSortByPosition(true)
            super.document = document
            this.pageId = pageId
        }
    }
}