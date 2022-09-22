package com.example.students_calendar.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.students_calendar.R
import com.example.students_calendar.adapters.NoteAdapter
import com.example.students_calendar.data.Note
import com.example.students_calendar.data.NoteState
import com.example.students_calendar.dialogs.RedactNoteDialog
import com.example.students_calendar.file_workers.NotesFile
import com.google.android.material.textfield.TextInputEditText
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import java.time.LocalDate
import java.time.LocalTime
import java.util.*


class NotesActivity : AppCompatActivity(),NoteAdapter.OnItemListener {
    lateinit var notesList:RecyclerView
    lateinit var NotesList:MutableList<Note>
    lateinit var Adapter:NoteAdapter

    private lateinit var FILE_NAME:String

    companion object {
        fun getInstance(
            context: Context,
        ): Intent {
            return Intent(context, NotesActivity::class.java)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)
        NotesList = mutableListOf()

        initWidgets()
    }

    private fun initWidgets() {


        notesList = findViewById(R.id.notesListView)
        val layoutManger = LinearLayoutManager(applicationContext)
        notesList.layoutManager = layoutManger

        val fileReader = NotesFile(this)

        var notes = fileReader.ReadNotes()

        NotesList.addAll(notes)

        val adapter = NoteAdapter(NotesList,this)
        notesList.adapter = adapter
        Adapter = adapter
    }

    fun addNoteAction(view: View) {
        val customView = layoutInflater.inflate(R.layout.dialog_create_note, null)
        val beginDateButton = customView.findViewById<Button>(R.id.numeratorDate)
        val endDateButton = customView.findViewById<Button>(R.id.endDateText)
        val beginTimeButton = customView.findViewById<Button>(R.id.beginTimeText)
        val endTimeButton = customView.findViewById<Button>(R.id.endTimeText)
        val periodDate = customView.findViewById<TextInputEditText>(R.id.periodDaysText)

        var beginDate:LocalDate?=null
        var endDate:LocalDate?=null
        var beginTime:LocalTime?=null
        var endTime:LocalTime?=null

        val slotsDate = UnderscoreDigitSlotsParser().parseSlots("____-__-__")
        val formatWatcherDate: FormatWatcher = MaskFormatWatcher(
            MaskImpl.createTerminated(slotsDate)
        )
        formatWatcherDate.installOn(periodDate)

        beginDateButton.visibility = View.GONE
        endDateButton.visibility = View.GONE
        beginTimeButton.visibility = View.GONE
        endTimeButton.visibility = View.GONE
        periodDate.visibility = View.GONE

        val checkBoxDate = customView.findViewById<CheckBox>(R.id.checkBoxDate)
        val checkBoxTime = customView.findViewById<CheckBox>(R.id.checkBoxTime)
        val checkBoxPeriodic = customView.findViewById<CheckBox>(R.id.checkBoxPeriodic)
        checkBoxDate.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
            if (b) {
                beginDateButton.visibility = View.VISIBLE
                endDateButton.visibility = View.VISIBLE
            } else {
                beginDateButton.visibility = View.GONE
                endDateButton.visibility = View.GONE
            }
        }
        checkBoxTime.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
            if (b) {
                beginTimeButton.visibility = View.VISIBLE
                endTimeButton.visibility = View.VISIBLE
            } else {
                beginTimeButton.visibility = View.GONE
                endTimeButton.visibility = View.GONE
            }
        }
        checkBoxPeriodic.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
            if (b) {
                periodDate.visibility = View.VISIBLE
            } else {
                periodDate.visibility = View.GONE
            }
        }


        beginDateButton.setOnClickListener{
            val dateSetter = Calendar.getInstance()
            if(beginDate!=null)
                dateSetter.set(beginDate!!.year,beginDate!!.monthValue-1,beginDate!!.dayOfMonth)
            val d =
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    dateSetter.set(Calendar.YEAR, year)
                    dateSetter.set(Calendar.MONTH, monthOfYear)
                    dateSetter.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    beginDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                    beginDateButton.text = beginDate.toString()
                    if (endDate == null || beginDate!!.isAfter(endDate)) {
                        endDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                        endDateButton.text = endDate.toString()
                    }
                }
            DatePickerDialog(
                this, d,
                dateSetter.get(Calendar.YEAR),
                dateSetter.get(Calendar.MONTH),
                dateSetter.get(Calendar.DAY_OF_MONTH)
            )
                .show()
        }
        endDateButton.setOnClickListener{
            val dateSetter = Calendar.getInstance()
            if(endDate!=null)
                dateSetter.set(endDate!!.year,endDate!!.monthValue-1,endDate!!.dayOfMonth)
            val d =
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    dateSetter.set(Calendar.YEAR, year)
                    dateSetter.set(Calendar.MONTH, monthOfYear)
                    dateSetter.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    endDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                    endDateButton.text = endDate.toString()
                    if (beginDate == null || endDate!!.isBefore(beginDate)) {
                        beginDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                        beginDateButton.text = beginDate.toString()
                    }
                }
            DatePickerDialog(
                this, d,
                dateSetter.get(Calendar.YEAR),
                dateSetter.get(Calendar.MONTH),
                dateSetter.get(Calendar.DAY_OF_MONTH)
            )
                .show()
        }
        beginTimeButton.setOnClickListener{
            val timeSetter = Calendar.getInstance()
            var t =
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    timeSetter.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    timeSetter.set(Calendar.MINUTE, minute)
                    beginTime = LocalTime.of(hourOfDay, minute)
                    beginTimeButton.text = beginTime.toString()
                    if (endTime == null || beginTime!!.isAfter(endTime)) {
                        endTime = LocalTime.of(hourOfDay, minute)
                        endTimeButton.text = endTime.toString()
                    }
                }
            TimePickerDialog(
                this, t,
                timeSetter.get(Calendar.HOUR_OF_DAY),
                timeSetter.get(Calendar.MINUTE), true
            )
                .show()
        }
        endTimeButton.setOnClickListener{
            val timeSetter = Calendar.getInstance()
            var t =
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    timeSetter.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    timeSetter.set(Calendar.MINUTE, minute)
                    endTime = LocalTime.of(hourOfDay, minute)
                    endTimeButton.text = endTime.toString()
                    if (beginTime == null || endTime!!.isBefore(beginTime)) {
                        beginTime = LocalTime.of(hourOfDay, minute)
                        beginTimeButton.text = beginTime.toString()
                    }
                }
            TimePickerDialog(
                this, t,
                timeSetter.get(Calendar.HOUR_OF_DAY),
                timeSetter.get(Calendar.MINUTE), true
            )
                .show()
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.node_create)
            .setView(customView)
            .setCancelable(false)
            .setNegativeButton(android.R.string.cancel) { _, _ ->

            }
            .setPositiveButton(android.R.string.ok) { _, _ ->
                var view = customView.findViewById<TextInputEditText>(R.id.NoteNameInput)
                val name = view.text
                val newNote = Note(name.toString(), NoteState.New, checkBoxPeriodic.isChecked)
                view = customView.findViewById(R.id.NoteDescriptionInput)
                val description = view.text
                newNote.description = description.toString()
                NotesList.add(newNote)
                Adapter.notifyItemInserted(NotesList.size-1)
                if(checkBoxDate.isChecked)
                {
                    newNote.startDate = beginDate
                    newNote.endDate = endDate
                }
                if(checkBoxTime.isChecked)
                {
                    newNote.startTime = beginTime
                    newNote.endTime = endTime
                }
                if(checkBoxPeriodic.isChecked)
                {
                    newNote.isPeriodic = true
                    view = customView.findViewById(R.id.periodDaysText)
                    newNote.periodDays = view.text.toString().toInt()
                }
                NotesFile(this).WriteNotes(NotesList)
            }.show()
    }
    fun calendarAction(view: View) {
        this.startActivity(MainActivity.getInstance(this))}
    fun notesAction(view: View) {}
    fun menuAction(view: View) {
        this.startActivity(MenuActivity.getInstance(this))}
    fun previousMonthAction(view: View) {}
    fun nextMonthAction(view: View) {}
    override fun onItemClick(position: Int) {
        RedactNoteDialog(this).createDialog(Adapter,position)

    }

}