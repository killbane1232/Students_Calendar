package com.example.students_calendar.holders

import android.view.View
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.students_calendar.R
import com.example.students_calendar.adapters.CalendarAdapter
import com.example.students_calendar.adapters.NoteAdapter

class NoteHolder : RecyclerView.ViewHolder, View.OnClickListener {
    var noteNameText: TextView
    var noteDescriptionText: TextView
    var noteStartTimeText: TextView
    var noteEndTimeText: TextView
    val onItemListener: NoteAdapter.OnItemListener

    constructor(@NonNull itemView: View, onItemListener: NoteAdapter.OnItemListener) : super(itemView) {
        noteNameText = itemView.findViewById(R.id.NoteName)
        noteDescriptionText = itemView.findViewById(R.id.NoteDescription)
        noteStartTimeText = itemView.findViewById(R.id.timeStart)
        noteEndTimeText = itemView.findViewById(R.id.timeEnd)
        this.onItemListener = onItemListener
        itemView.setOnClickListener(this)
    }

    @Override
    override fun onClick(view: View)
    {

        onItemListener.onItemClick(adapterPosition)
    }
}