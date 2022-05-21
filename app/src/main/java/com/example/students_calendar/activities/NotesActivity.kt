package com.example.students_calendar.activities

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Adapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.students_calendar.R
import com.example.students_calendar.adapters.CalendarAdapter
import com.example.students_calendar.adapters.NoteAdapter
import com.example.students_calendar.data.Note
import com.example.students_calendar.data.NoteState
import com.google.android.material.textfield.TextInputEditText
import java.time.LocalDate

class NotesActivity : AppCompatActivity(),NoteAdapter.OnItemListener {
    lateinit var notesList:RecyclerView
    lateinit var NotesList:MutableList<Note>
    lateinit var Adapter:NoteAdapter

    companion object {
        fun getInstance(
            context: Context,
        ): Intent {
            return Intent(context, NotesActivity::class.java)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)
        NotesList = mutableListOf()
        initWidgets()
    }

    private fun initWidgets() {
        notesList = findViewById(R.id.notesListView)

        update()
    }
    private fun update()
    {
        val layoutManger = LinearLayoutManager(applicationContext)
        notesList.layoutManager = layoutManger
        val adapter = NoteAdapter(NotesList,this)
        notesList.adapter = adapter
        Adapter = adapter
    }

    fun addNoteAction(view: View) {
        val customView = layoutInflater.inflate(R.layout.node_creation_dialog, null)
        AlertDialog.Builder(this)
            .setTitle(R.string.node_create)
            .setView(customView)
            .setCancelable(false)
            .setNegativeButton(android.R.string.cancel) { _, _ ->

            }
            .setPositiveButton(android.R.string.ok) { _, _ ->
                var view = customView.findViewById<TextInputEditText>(R.id.NoteNameInput)
                var text = view.text
                val newNode = Note(text.toString(), NoteState.New)
                view = customView.findViewById<TextInputEditText>(R.id.NoteDescriptionInput)
                text = view.text
                newNode.Description = text.toString()
                Adapter.Add(newNode)
            }.show()
    }
    fun calendarAction(view: View) {
        this.startActivity(MainActivity.getInstance(this))}
    fun notesAction(view: View) {}
    fun menuAction(view: View) {
        this.startActivity(MenuActivity.getInstance(this))}
    fun previousMonthAction(view: View) {}
    fun nextMonthAction(view: View) {}
    override fun onItemClick(position: Int, dayText: String) {

    }
}