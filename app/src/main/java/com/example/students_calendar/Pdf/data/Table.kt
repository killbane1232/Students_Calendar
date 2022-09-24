package com.example.students_calendar.Pdf.data

class Table(private val columnsCount: Int) {

    var rows: MutableList<TableRow> = ArrayList()

    override fun toString(): String {
        val retVal = StringBuilder()
        for (row: TableRow in rows) {
            if (retVal.length > 0) {
                retVal.append("\n")
            }
            var cellIdx = 0 //pointer of row.cells
            var columnIdx = 0 //pointer of columns
            while (columnIdx < columnsCount) {
                if (cellIdx < row.cells.size) {
                    val cell: TableCell = row.cells.get(cellIdx)
                    if (cell.idx === columnIdx) {

                        if (cell.idx !== 0) {
                                retVal.append(";")
                            }
                            retVal.append(cell.content)
                        cellIdx++
                        columnIdx++
                    } else if (columnIdx < cellIdx) {
                        if (columnIdx != 0) {
                            retVal.append(";")
                        }
                        columnIdx++
                    } else {
                        throw RuntimeException("Invalid state")
                    }
                } else {
                    if (columnIdx != 0) {
                        retVal.append(";")
                    }
                    columnIdx++
                }
            }
        }
        return retVal.toString()
    }
}
