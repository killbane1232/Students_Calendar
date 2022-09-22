package com.example.students_calendar.dialogs

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.students_calendar.data.Note
import com.example.students_calendar.file_workers.NotesFile
import com.obsez.android.lib.filechooser.ChooserDialog
import java.io.IOException

class FileChooserDialog {
    val parent: AppCompatActivity

    constructor(parent: AppCompatActivity)
    {
        this.parent = parent
    }
    fun showDialog(suffix:String) {
        ChooserDialog(parent)
            .withFilter(false, false, suffix)
            .withChosenListener { _, file ->

                if (suffix == "pdf") {
                    NumeratorDialog(parent).createDialog(file)
                    return@withChosenListener
                }
                val notesFile = NotesFile(parent)
                var notes: MutableList<Note>
                try {
                    notes = notesFile.ReadNotes().toMutableList()
                } catch (ex: IOException) {
                    notes = mutableListOf()
                }
                var newNotes: List<Note>
                try {
                    newNotes = notesFile.ReadNotes(file)
                    newNotes.forEach {
                        var note = notes.find{ x-> (x.name == it.name) }
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