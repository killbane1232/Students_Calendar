package com.example.students_calendar.holders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.students_calendar.R
import com.example.students_calendar.adapters.NoteAdapter

class NoteHolder(
    itemView: View,
    private val onItemListener: NoteAdapter.OnItemListener
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    var noteNameText: TextView
    var noteDescriptionText: TextView
    var noteStartTimeText: TextView
    var noteEndTimeText: TextView

    init {
        noteNameText = itemView.findViewById(R.id.NoteName)
        noteDescriptionText = itemView.findViewById(R.id.NoteDescription)
        noteStartTimeText = itemView.findViewById(R.id.timeStart)
        noteEndTimeText = itemView.findViewById(R.id.timeEnd)
        itemView.setOnClickListener(this)
    }

    @Override
    override fun onClick(view: View)
    {
        onItemListener.onItemClick(adapterPosition)
    }
}