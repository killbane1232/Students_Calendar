package com.example.students_calendar;

import android.view.View
import android.widget.TextView

import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView

public class CalendarViewHolder : RecyclerView.ViewHolder, View.OnClickListener {

    var dayOfMonthText:TextView;
    val onItemListener:CalendarAdapter.OnItemListener

    constructor(@NonNull itemView: View, onItemListener: CalendarAdapter.OnItemListener) : super(itemView) {
        dayOfMonthText = itemView.findViewById(R.id.cellDayTV);
        this.onItemListener = onItemListener
        itemView.setOnClickListener(this)
    }

    @Override
    override fun onClick(view: View)
    {
        onItemListener.onItemClick(adapterPosition, dayOfMonthText.text.toString())
    }
}
