package com.example.students_calendar.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.students_calendar.R
import com.example.students_calendar.data.Note
import com.example.students_calendar.holders.NoteHolder

class NoteAdapter : RecyclerView.Adapter<NoteHolder> {
    var NotesList:MutableList<Note>
    var listener : NoteAdapter.OnItemListener

    constructor(noteList:MutableList<Note>, onItemListener: NoteAdapter.OnItemListener)
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
        val note = NotesList.get(position)
        holder.noteNameText.text = note.name
        holder.noteDescriptionText.text = note.description
        holder.noteStartTimeText.visibility = View.GONE
        holder.noteEndTimeText.visibility = View.GONE
        if(note.startTime!=null)
        {
            holder.noteStartTimeText.text = note.startTime.toString()
            holder.noteStartTimeText.visibility = View.VISIBLE
        }
        if(note.endTime!=null)
        {
            holder.noteEndTimeText.text = note.endTime.toString()
            holder.noteEndTimeText.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return NotesList.size
    }

    interface OnItemListener
    {
        fun onItemClick(position: Int)
    }
}