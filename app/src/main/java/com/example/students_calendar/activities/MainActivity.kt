package com.example.students_calendar.activities;

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build
import android.os.Bundle;
import android.view.View
import android.widget.Button
import android.widget.TextView;
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.example.students_calendar.R
import com.example.students_calendar.adapters.CalendarAdapter
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity(), CalendarAdapter.OnItemListener {
    private lateinit var monthText:TextView
    private lateinit var calendarView:RecyclerView
    private lateinit var selectedDate:LocalDate

    companion object {
        fun getInstance(
            context: Context,
        ): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initWidgets();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            selectedDate = LocalDate.now()
        }
        setMonthView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMonthView() {
        monthText.setText(monthFromDate(selectedDate));
        val pair = daysInMonthArray(selectedDate)
        val array = pair.first
        val index = pair.second


        val adapter = CalendarAdapter(array,this, this)
        val layoutManger = GridLayoutManager(applicationContext,7)
        //var decor = RecyclerView.ItemDecoration()
        //calendarView.addItemDecoration()
        calendarView.layoutManager = layoutManger
        calendarView.adapter = adapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun daysInMonthArray(selectedDate: LocalDate): Pair<ArrayList<String>,Int> {
        val days = ArrayList<String>()
        val yearMonth = YearMonth.from(selectedDate)
        val daysInMoth = yearMonth.lengthOfMonth()
        val firstOfMonth = selectedDate.withDayOfMonth(1)
        val dayOfWeek = firstOfMonth.dayOfWeek.value-1
        var dateToday = -1
        var localDateNow = LocalDate.now()

        for(i in 1..42)
        {
            if(i<= dayOfWeek || i>dayOfWeek+daysInMoth)
                days.add("")
            else
            {
                if(localDateNow.monthValue==selectedDate.monthValue && localDateNow.year==selectedDate.year&&i-dayOfWeek==localDateNow.dayOfMonth)
                    dateToday=i-1
                if(localDateNow.year == selectedDate.year&&localDateNow.month == selectedDate.month && localDateNow.dayOfMonth==i-dayOfWeek)
                    days.add("%"+(i-dayOfWeek).toString())
                else
                    days.add((i-dayOfWeek).toString())
            }
        }
        return Pair(days,dateToday)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun monthFromDate(date: LocalDate):String
    {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return date.format(formatter)
    }

    private fun initWidgets() {
        calendarView = findViewById(R.id.notesListView)
        monthText = findViewById(R.id.monthTV)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemClick(position: Int, dayText: String) {
        if(!dayText.equals(""))
        {
            val message = "Selected Date " + dayText + " " + monthFromDate(selectedDate)
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun previousMonthAction(view: View) {
        selectedDate = selectedDate.minusMonths(1)
        setMonthView()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun nextMonthAction(view: View) {
        selectedDate = selectedDate.plusMonths(1)
        setMonthView()
    }

    fun calendarAction(view: View) {}
    fun notesAction(view: View) {
        this.startActivity(NotesActivity.getInstance(this))}
    fun menuAction(view: View) {
        this.startActivity(MenuActivity.getInstance(this))}
}