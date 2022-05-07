package com.example.students_calendar.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.students_calendar.holders.CalendarViewHolder
import com.example.students_calendar.R
import java.util.*

class CalendarAdapter : RecyclerView.Adapter<CalendarViewHolder> {

    private val daysOfMoth : ArrayList<String>
    private val listener : OnItemListener

    constructor(daysOfMonth: ArrayList<String>, onItemListener: OnItemListener)
    {
        this.daysOfMoth = daysOfMonth
        this.listener = onItemListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        var view = inflater.inflate(R.layout.calendar_cell,parent,false)
        var layoutParams = view.layoutParams
        layoutParams.height = (parent.height * 0.166666666).toInt()
        return  CalendarViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.dayOfMonthText.setText(daysOfMoth.get(position))
    }

    override fun getItemCount(): Int {
        return daysOfMoth.size
    }

    public interface OnItemListener
    {
        fun onItemClick(position: Int, dayText:String)
    }
}