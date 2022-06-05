package com.example.students_calendar.dialogs

import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.students_calendar.adapters.NoteAdapter
import com.example.students_calendar.data.Note
import com.example.students_calendar.file_workers.NotesFile
import com.obsez.android.lib.filechooser.ChooserDialog
import java.io.IOException

class FileChooserDialog {
    val parent: AppCompatActivity

    constructor(activity: AppCompatActivity)
    {
        this.parent = activity
    }
    fun showDialog(suffix:String) {
        ChooserDialog(parent)
            .withFilter(false, false, suffix)
            .withChosenListener { _, file ->
                val notesFile = NotesFile(parent)
                var notes: MutableList<Note>
                try {
                    notes = notesFile.ReadNotes().toMutableList()
                } catch (ex: IOException) {
                    notes = mutableListOf()
                }
                var newNotes: List<Note>
                try {
                    if(suffix!="pdf")
                        newNotes = notesFile.ReadNotes(file)
                    else
                        newNotes = notesFile.ReadNotesFromPDF(file)
                    newNotes.forEach {
                        var new = it
                        var note = notes.find({ (it.Name == new.Name) })
                        if (note == null) {
                            notes.add(it)
                        } else
                            if (it.Description != new.Description ||
                                it.IsPeriodic != new.IsPeriodic ||
                                it.EndDate != new.EndDate ||
                                it.StartDate != new.StartDate ||
                                it.EndTime != new.EndTime ||
                                it.StartTime != new.StartTime ||
                                it.PeriodMinutes != new.PeriodMinutes ||
                                it.PeriodDays != new.PeriodDays ||
                                it.PeriodMonths != new.PeriodMonths ||
                                it.PeriodYears != new.PeriodYears
                            )
                                notes.add(it)
                    }
                    notesFile.WriteNotes(notes)
                } catch (ex: Exception) {
                    Toast.makeText(parent, ex.message, Toast.LENGTH_LONG)
                }
            }
            .build()
            .show()
    }
}