package com.example.students_calendar.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.example.students_calendar.R
import com.example.students_calendar.adapters.CalendarAdapter
import com.example.students_calendar.adapters.NoteAdapter
import com.example.students_calendar.data.Note
import com.example.students_calendar.dialogs.NoteListDialog
import com.example.students_calendar.file_workers.NotesFile
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity(), CalendarAdapter.OnItemListener, NoteAdapter.OnItemListener {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initWidgets()

        selectedDate = LocalDate.now()

        setMonthView()
    }

    private fun setMonthView() {
        monthText.text = monthFromDate(selectedDate)
        val pair = daysInMonthArray(selectedDate)
        val array = pair.first

        val adapter = CalendarAdapter(array,this, this)
        val layoutManger = GridLayoutManager(applicationContext,7)

        calendarView.layoutManager = layoutManger
        calendarView.adapter = adapter
    }

    private fun daysInMonthArray(selectedDate: LocalDate): Pair<ArrayList<String>,Int> {
        val days = ArrayList<String>()
        val yearMonth = YearMonth.from(selectedDate)
        val daysInMoth = yearMonth.lengthOfMonth()
        val firstOfMonth = selectedDate.withDayOfMonth(1)
        val dayOfWeek = firstOfMonth.dayOfWeek.value-1
        var dateToday = -1
        val localDateNow = LocalDate.now()

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

    private fun monthFromDate(date: LocalDate):String
    {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return date.format(formatter)
    }

    private fun initWidgets() {
        calendarView = findViewById(R.id.notesListView)
        monthText = findViewById(R.id.monthTV)
    }

    override fun onItemClick(position: Int, dayText: String) {
        if(dayText.isBlank())
            return

        val notesFile = NotesFile(this)

        val notesList = notesFile.readNotes()

        val dayClicked:LocalDate
        val dayOfMonth = dayText.toInt()
        var daysDif = (selectedDate.dayOfMonth-dayOfMonth).toLong()

        dayClicked = if(selectedDate.dayOfMonth>dayOfMonth)
            selectedDate.minusDays(daysDif)
        else
            selectedDate.plusDays(-daysDif)

        val epoch = dayClicked.toEpochDay()
        val listToUse = mutableListOf<Note>()
        notesList.forEach {
            val x = it
            if(x.startDate==null || x.endDate==null)
                return@forEach

            var daysPeriod = false
            if(x.isPeriodic)
            {
                val difference = epoch - x.startDate!!.toEpochDay()-((epoch - x.startDate!!.toEpochDay()) % x.periodDays!!)
                val start = x.startDate!!.toEpochDay()+difference
                val end = x.endDate!!.toEpochDay()+difference
                daysPeriod = epoch in start..end ||
                        start-x.periodDays!!<=epoch && end-x.periodDays!!>=epoch ||
                        start+x.periodDays!!<=epoch && end+x.periodDays!!>=epoch
            }

            if((x.startDate!!.toEpochDay()<=epoch && x.endDate!!.toEpochDay()>=epoch)||
                daysPeriod)
                listToUse.add(x)
        }

        NoteListDialog(this).createDialog("Заметки на "+dayClicked.toString(), listToUse, this)
    }

    fun previousMonthAction(view: View) {
        selectedDate = selectedDate.minusMonths(1)
        setMonthView()
    }

    fun nextMonthAction(view: View) {
        selectedDate = selectedDate.plusMonths(1)
        setMonthView()
    }

    fun calendarAction(view: View) {}
    fun notesAction(view: View) {
        this.startActivity(NotesActivity.getInstance(this))}
    fun menuAction(view: View) {
        this.startActivity(MenuActivity.getInstance(this))}

    override fun onItemClick(position: Int) {
    }
}