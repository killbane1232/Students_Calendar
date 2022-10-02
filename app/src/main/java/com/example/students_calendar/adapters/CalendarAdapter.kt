package com.example.students_calendar.adapters

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.students_calendar.R
import com.example.students_calendar.holders.CalendarViewHolder
import java.util.*

class CalendarAdapter(
    daysOfMonth: ArrayList<String>,
    onItemListener: OnItemListener,
    context: Context
) : RecyclerView.Adapter<CalendarViewHolder>() {

    private val daysOfMoth : ArrayList<String> = daysOfMonth
    private val listener : OnItemListener = onItemListener
    private val _context:Context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.calendar_cell,parent,false)
        val layoutParams = view.layoutParams
        layoutParams.height = (parent.height * 0.166666666).toInt()
        return  CalendarViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        var date = daysOfMoth[position]
        if(date.isNotEmpty() && date[0]=='%')
        {
            date=date.removePrefix("%")
            val value = TypedValue()
            val theme = _context.theme

            theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, value, true)
            holder.itemView.setBackgroundColor(value.data)
        }
        holder.dayOfMonthText.text = date
    }

    override fun getItemCount(): Int {
        return daysOfMoth.size
    }

    interface OnItemListener
    {
        fun onItemClick(position: Int, dayText:String)
    }
}