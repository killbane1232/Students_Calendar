package com.example.students_calendar.Pdf.data

class Table(val pageIdx: Int, private val columnsCount: Int) {

    var rows: MutableList<TableRow> = ArrayList()

    fun toHtml(): String {
        return toString(true)
    }

    override fun toString(): String {
        return toString(false)
    }

    private fun toString(inHtmlFormat: Boolean): String {
        val retVal = StringBuilder()
        if (inHtmlFormat) {
            retVal.append(
                "<!DOCTYPE html>"
                        + "<html>"
                        + "<head>"
                        + "<meta charset='utf-8'>"
            )
                .append("</head>")
                .append("<body>")
            retVal.append("<table border='1'>")
        }
        for (row: TableRow in rows) {
            if (inHtmlFormat) {
                retVal.append("<tr>")
            } else if (retVal.length > 0) {
                retVal.append("\n")
            }
            var cellIdx = 0 //pointer of row.cells
            var columnIdx = 0 //pointer of columns
            while (columnIdx < columnsCount) {
                if (cellIdx < row.cells.size) {
                    val cell: TableCell = row.cells.get(cellIdx)
                    if (cell.idx === columnIdx) {
                        if (inHtmlFormat) {
                            retVal.append("<td>")
                                .append(cell.content)
                                .append("</td>")
                        } else {
                            if (cell.idx !== 0) {
                                retVal.append(";")
                            }
                            retVal.append(cell.content)
                        }
                        cellIdx++
                        columnIdx++
                    } else if (columnIdx < cellIdx) {
                        if (inHtmlFormat) {
                            retVal.append("<td>")
                                .append("</td>")
                        } else if (columnIdx != 0) {
                            retVal.append(";")
                        }
                        columnIdx++
                    } else {
                        throw RuntimeException("Invalid state")
                    }
                } else {
                    if (inHtmlFormat) {
                        retVal.append("<td>")
                            .append("</td>")
                    } else if (columnIdx != 0) {
                        retVal.append(";")
                    }
                    columnIdx++
                }
            }
            if (inHtmlFormat) {
                retVal.append("</tr>")
            }
        }
        if (inHtmlFormat) {
            retVal.append(
                "</table>"
            )
                .append("</body>")
                .append("</html>")
        }
        return retVal.toString()
    }
}
