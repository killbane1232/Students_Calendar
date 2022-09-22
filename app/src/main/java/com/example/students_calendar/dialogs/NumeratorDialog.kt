package com.example.students_calendar.dialogs

import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.view.View
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


class NumeratorDialog {
    val parent: AppCompatActivity

    constructor(parent: AppCompatActivity)
    {
        this.parent = parent
    }

    fun createDialog(file:File): AlertDialog
    {
        val customView = parent.layoutInflater.inflate(R.layout.dialog_numerator, null)
        var date:LocalDate? = null
        val dateNow = getPreferenceValue(parent)
        if(dateNow!=null)
        {
            try{
                dateNow.toLong()
                date = LocalDate.ofEpochDay(dateNow.toLong())
            }
            catch (ex:Exception)
            {
                date = null
            }
        }

        val numeratorButton = customView.findViewById<Button>(R.id.numeratorDate)
        val pageNumber = customView.findViewById<EditText>(R.id.pageNumber)

        numeratorButton.setOnClickListener{
            val dateSetter = Calendar.getInstance()
            if(date!=null)
                dateSetter.set(date!!.year,date!!.monthValue-1,date!!.dayOfMonth)
            val d =
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
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

        var dialog =(AlertDialog.Builder(parent)
            .setTitle("Выберите любой день недели, являющейся числителем")
            .setView(customView)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok) { _, _ ->
            }).show()

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        if (date!=null) {
                            var page = 0
                            if(pageNumber.text.length!=0)
                            {
                                page = pageNumber.text.toString().toInt()-1
                            }
                            date = date!!.minusDays(date!!.dayOfWeek.value.toLong()+69)
                            val notesFile = NotesFile(parent)
                            var notes: MutableList<Note>
                            try {
                                notes = notesFile.ReadNotes().toMutableList()
                            } catch (ex: IOException) {
                                notes = mutableListOf()
                            }
                            var newNotes: List<Note>
                            try {
                                notes.removeAll { it.isSchedule }
                                newNotes = notesFile.ReadNotesFromPDF(file, date!!, page)
                                /*newNotes.forEach {
                                    var new = it
                                    var note = notes.find{ (it.name == new.name) }
                                    if (note == null) {
                                        notes.add(it)
                                    } else
                                        if (it.description != note.description ||
                                            it.isPeriodic != note.isPeriodic ||
                                            it.endDate != note.endDate ||
                                            it.startDate != note.startDate ||
                                            it.endTime != note.endTime ||
                                            it.startTime != note.startTime ||
                                            it.periodDays != note.periodDays)
                                            notes.add(it)
                                }*/
                                notes.addAll(newNotes)
                                notesFile.WriteNotes(notes)
                            } catch (ex: Exception) {
                                Toast.makeText(parent, ex.message, Toast.LENGTH_LONG)
                            }
                            dialog.dismiss()
                        }
                    }
                })
        return dialog
    }
    companion object {

        val myPref = "preferenceName"
        fun getPreferenceValue(activity: AppCompatActivity): String? {
            val sp: SharedPreferences = activity.getSharedPreferences(myPref, 0)
            return sp.getString("myStore", "TheDefaultValueIfNoValueFoundOfThisKey")
        }

        fun writeToPreference(activity: AppCompatActivity, thePreference: String?) {
            val editor: SharedPreferences.Editor = activity.getSharedPreferences(myPref, 0).edit()
            editor.putString("myStore", thePreference)
            editor.commit()
        }
    }
}