package com.example.students_calendar.dialogs

import android.app.DatePickerDialog
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.students_calendar.R
import com.example.students_calendar.data.Note
import com.example.students_calendar.file_workers.NotesFile
import java.io.File
import java.io.IOException
import java.time.LocalDate
import java.util.*


class NumeratorDialog(val parent: AppCompatActivity) {

    fun createDialog(file:File): AlertDialog
    {
        val customView = parent.layoutInflater.inflate(R.layout.dialog_numerator, null)
        var date:LocalDate? = null

        val numeratorButton = customView.findViewById<Button>(R.id.numeratorDate)
        val pageNumber = customView.findViewById<EditText>(R.id.pageNumber)

        numeratorButton.setOnClickListener{
            val dateSetter = Calendar.getInstance()
            if(date!=null)
                dateSetter.set(date!!.year,date!!.monthValue-1,date!!.dayOfMonth)
            val d =
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    dateSetter.set(Calendar.YEAR, year)
                    dateSetter.set(Calendar.MONTH, monthOfYear)
                    dateSetter.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    date = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                    numeratorButton.text = date.toString()
                }
            DatePickerDialog(
                parent, d,
                dateSetter.get(Calendar.YEAR),
                dateSetter.get(Calendar.MONTH),
                dateSetter.get(Calendar.DAY_OF_MONTH)
            )
                .show()
        }

        val dialog =(AlertDialog.Builder(parent)
            .setTitle("Выберите любой день недели, являющейся числителем")
            .setView(customView)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok) { _, _ ->
            }).show()

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {
                    if (date != null) {
                        var page = 0
                        if (pageNumber.text.isNotEmpty()) {
                            page = pageNumber.text.toString().toInt() - 1
                        }
                        date = date!!.minusDays(date!!.dayOfWeek.value.toLong() + 69)
                        val notesFile = NotesFile(parent)
                        val notes: MutableList<Note> = try {
                            notesFile.readNotes().toMutableList()
                        } catch (ex: IOException) {
                            mutableListOf()
                        }
                        val newNotes: List<Note>
                        try {
                            notes.removeAll { it.isSchedule }
                            newNotes = notesFile.readNotesFromPDF(file, date!!, page)

                            notes.addAll(newNotes)
                            notesFile.writeNotes(notes)
                        } catch (ex: Exception) {
                            Toast.makeText(parent, ex.message, Toast.LENGTH_LONG).show()
                        }
                        dialog.dismiss()
                    }
                }
        return dialog
    }
}