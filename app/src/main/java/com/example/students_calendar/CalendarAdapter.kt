package com.example.students_calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
        //TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        //TODO("Not yet implemented")
        return daysOfMoth.size
    }

    public interface OnItemListener
    {
        fun onItemClick(position: Int, dayText:String)
    }
}