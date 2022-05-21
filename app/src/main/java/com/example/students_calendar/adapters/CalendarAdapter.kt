package com.example.students_calendar.adapters

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.example.students_calendar.R
import com.example.students_calendar.holders.CalendarViewHolder
import org.xmlpull.v1.XmlPullParser
import java.util.*

class CalendarAdapter : RecyclerView.Adapter<CalendarViewHolder> {

    private val daysOfMoth : ArrayList<String>
    private val listener : OnItemListener
    private val _context:Context

    constructor(daysOfMonth: ArrayList<String>, onItemListener: OnItemListener, context:Context)
    {
        this.daysOfMoth = daysOfMonth
        this.listener = onItemListener
        this._context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        var view = inflater.inflate(R.layout.calendar_cell,parent,false)
        var layoutParams = view.layoutParams
        layoutParams.height = (parent.height * 0.166666666).toInt()
        return  CalendarViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        var date = daysOfMoth.get(position)
        if(date.length>0 && date[0]=='%')
        {
            date=date.removePrefix("%")
            var value = TypedValue()
            val theme = _context.getTheme()

            theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, value, true)
            holder.ItemView.setBackgroundColor(value.data)
        }
        holder.dayOfMonthText.setText(date)
    }

    override fun getItemCount(): Int {
        return daysOfMoth.size
    }

    public interface OnItemListener
    {
        fun onItemClick(position: Int, dayText:String)
    }
}