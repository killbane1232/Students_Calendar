package com.example.students_calendar.holders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.students_calendar.R
import com.example.students_calendar.adapters.CalendarAdapter

class CalendarViewHolder(
    itemView: View,
    private val onItemListener: CalendarAdapter.OnItemListener
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var dayOfMonthText:TextView

    init {
        dayOfMonthText = itemView.findViewById(R.id.cellDayTV)
        itemView.setOnClickListener(this)
    }

    @Override
    override fun onClick(view: View)
    {
        onItemListener.onItemClick(adapterPosition, dayOfMonthText.text.toString())
    }
}
