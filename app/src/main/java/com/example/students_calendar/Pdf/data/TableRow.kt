package com.example.students_calendar.Pdf.data



class TableRow(val idx: Int) {
    val cells: MutableList<TableCell> = ArrayList()
    var index:Int = 0

    override fun toString(): String {
        val retVal = StringBuilder()
        var lastCellIdx = 0
        for (cell in cells) {
            for (idx2 in lastCellIdx until cell.idx - 1) {
                retVal.append(";")
            }
            if (cell.idx > 0) {
                retVal.append(";")
            }
            retVal.append(cell.content)
            lastCellIdx = cell.idx
        }
        //return
        return retVal.toString()
    }
}