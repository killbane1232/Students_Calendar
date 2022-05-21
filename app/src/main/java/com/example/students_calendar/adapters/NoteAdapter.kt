package com.example.students_calendar.adapters

import android.content.Context
import android.util.JsonReader
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import androidx.recyclerview.widget.RecyclerView
import com.example.students_calendar.R
import com.example.students_calendar.data.Note
import com.example.students_calendar.holders.CalendarViewHolder
import com.example.students_calendar.holders.NoteHolder

class NoteAdapter : RecyclerView.Adapter<NoteHolder> {
    var NotesList:MutableList<Note>
    private val listener : NoteAdapter.OnItemListener

    public constructor(noteList:MutableList<Note>, onItemListener: NoteAdapter.OnItemListener)
    {
        this.NotesList = noteList
        this.listener = onItemListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        var inflater = LayoutInflater.from(parent.context)
        var view = inflater.inflate(R.layout.note_line,parent,false)

        return  NoteHolder(view, listener)
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        holder.noteNameText.setText(NotesList.get(position).Name)
        holder.noteDescriptionText.setText(NotesList.get(position).Description)
    }

    override fun getItemCount(): Int {
        return NotesList.size
    }

    fun Add(note: Note) {
        NotesList.add(note)
        this.notifyItemInserted(NotesList.size-1)
    }

    public interface OnItemListener
    {
        fun onItemClick(position: Int, dayText:String)
    }
}