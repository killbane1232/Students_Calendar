package com.example.students_calendar.dialogs

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.students_calendar.R
import com.example.students_calendar.adapters.NoteAdapter
import com.example.students_calendar.data.Note

class NoteListDialog {
    val activity:AppCompatActivity

    constructor(activity:AppCompatActivity)
    {
        this.activity = activity
    }

    fun createDialog(title:String, noteList:MutableList<Note>, listener:NoteAdapter.OnItemListener):AlertDialog
    {
        val customView = activity.layoutInflater.inflate(R.layout.dialog_calendar_notes, null)
        val notesList = customView.findViewById<RecyclerView>(R.id.notesListView)
        val layoutManger = LinearLayoutManager(activity)
        notesList.layoutManager = layoutManger
        val listToUse = mutableListOf<Note>()
        listToUse.addAll(noteList)
        val adapter = NoteAdapter(listToUse,listener)
        notesList.adapter = adapter
        return (AlertDialog.Builder(activity)
            .setTitle(title)
            .setView(customView)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                Toast.makeText(activity,"УРА", Toast.LENGTH_LONG)
            }).show()
    }
}