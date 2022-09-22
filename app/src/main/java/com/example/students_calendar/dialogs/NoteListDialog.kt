package com.example.students_calendar.dialogs

import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.students_calendar.R
import com.example.students_calendar.adapters.NoteAdapter
import com.example.students_calendar.data.Note

class NoteListDialog {
    val parent:AppCompatActivity

    constructor(parent:AppCompatActivity)
    {
        this.parent = parent
    }

    fun createDialog(title:String, noteList:MutableList<Note>, listener:NoteAdapter.OnItemListener):AlertDialog
    {
        val customView = parent.layoutInflater.inflate(R.layout.dialog_calendar_notes, null)
        val notesList = customView.findViewById<RecyclerView>(R.id.notesListView)
        val layoutManger = LinearLayoutManager(parent)
        notesList.layoutManager = layoutManger
        val listToUse = mutableListOf<Note>()
        listToUse.addAll(noteList)
        val adapter = NoteAdapter(listToUse,listener)
        notesList.adapter = adapter
        return (AlertDialog.Builder(parent)
            .setTitle(title)
            .setView(customView)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                Toast.makeText(parent,"УРА", Toast.LENGTH_LONG)
            }).show()
    }
}