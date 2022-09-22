package com.example.students_calendar.dialogs

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.students_calendar.R
import com.example.students_calendar.adapters.NoteAdapter
import com.example.students_calendar.data.NoteState
import com.example.students_calendar.file_workers.NotesFile
import com.google.android.material.textfield.TextInputEditText
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*


class RedactNoteDialog {
    val parent: AppCompatActivity
    var active: Boolean

    lateinit var beginDateButton: Button
    lateinit var endDateButton: Button
    lateinit var beginTimeButton: Button
    lateinit var endTimeButton: Button
    lateinit var periodDate: TextInputEditText
    lateinit var customView: View
    lateinit var checkBoxDate: CheckBox
    lateinit var checkBoxTime: CheckBox
    lateinit var checkBoxPeriodic: CheckBox

    var beginDate: LocalDate? = null
    var endDate: LocalDate? = null
    var beginTime: LocalTime? = null
    var endTime: LocalTime? = null

    constructor(parent: AppCompatActivity) {
        this.parent = parent
        this.active = false
    }

    fun createDialog(adapter: NoteAdapter, position: Int) {
        customView = parent.layoutInflater.inflate(R.layout.dialog_create_note, null)

        initObservers()
        prepareUI()

        val note = adapter.NotesList.get(position)

        val nameView = customView.findViewById<TextInputEditText>(R.id.NoteNameInput)
        nameView.setText(note.name)
        val stateView = customView.findViewById<CheckBox>(R.id.checkBoxState)
        stateView.isChecked = (note.state == NoteState.Complete)
        val descriptionView = customView.findViewById<TextInputEditText>(R.id.NoteDescriptionInput)
        descriptionView.setText(note.description)

        if (note.startDate != null && note.endDate != null) {
            checkBoxDate.isChecked = true
            beginDate = note.startDate!!
            endDate = note.endDate!!
            beginDateButton.visibility = View.VISIBLE
            beginDateButton.setText(note.startDate?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            endDateButton.visibility = View.VISIBLE
            endDateButton.setText(note.endDate?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        }
        if (note.startTime != null) {
            checkBoxTime.isChecked = true
            beginTime = note.startTime!!
            endTime = note.endTime!!
            beginTimeButton.visibility = View.VISIBLE
            beginTimeButton.setText(note.startTime?.format(DateTimeFormatter.ofPattern("HH:mm")))
            endTimeButton.visibility = View.VISIBLE
            endTimeButton.setText(note.endTime?.format(DateTimeFormatter.ofPattern("HH:mm")))
        }

        if (note.isPeriodic && note.periodDays != null) {
            checkBoxPeriodic.isChecked = true
            periodDate.visibility = View.VISIBLE
            periodDate.setText(note.periodDays.toString())
        }

        AlertDialog.Builder(parent)
            .setTitle(R.string.node_create)
            .setView(customView)
            .setCancelable(false)
            .setNegativeButton(android.R.string.cancel) { _, _ ->
            }
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val name = nameView.text
                note.name = name.toString()
                if (stateView.isChecked)
                    note.state = NoteState.Complete
                else
                    note.state = NoteState.New

                note.description = descriptionView.text.toString()
                if (checkBoxDate.isChecked) {
                    note.startDate = beginDate
                    note.endDate = endDate
                }
                if (checkBoxTime.isChecked) {
                    note.startTime = beginTime
                    note.endTime = endTime
                }
                if (checkBoxPeriodic.isChecked) {
                    note.isPeriodic = true
                    note.periodDays = periodDate.text.toString().toInt()
                }
                adapter.notifyItemChanged(position)
                NotesFile(parent).WriteNotes(adapter.NotesList)
            }.show()
    }

    private fun prepareUI() {
        beginDateButton.visibility = View.GONE
        endDateButton.visibility = View.GONE
        beginTimeButton.visibility = View.GONE
        endTimeButton.visibility = View.GONE
        periodDate.visibility = View.GONE
    }

    fun initObservers() {
        beginDateButton = customView.findViewById<Button>(R.id.numeratorDate)
        endDateButton = customView.findViewById<Button>(R.id.endDateText)
        beginTimeButton = customView.findViewById<Button>(R.id.beginTimeText)
        endTimeButton = customView.findViewById<Button>(R.id.endTimeText)
        periodDate = customView.findViewById<TextInputEditText>(R.id.periodDaysText)

        beginDateButton.setOnClickListener{ beginDateOnClick() }
        endDateButton.setOnClickListener { endDateOnClick() }

        beginTimeButton.setOnClickListener { beginTimeOnClick() }
        endTimeButton.setOnClickListener { endTimeOnClick() }

        checkBoxDate = customView.findViewById<CheckBox>(R.id.checkBoxDate)
        checkBoxTime = customView.findViewById<CheckBox>(R.id.checkBoxTime)
        checkBoxPeriodic = customView.findViewById<CheckBox>(R.id.checkBoxPeriodic)

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
    }

    fun beginDateOnClick(){
        val dateSetter = Calendar.getInstance()
        if (beginDate != null)
            dateSetter.set(beginDate!!.year, beginDate!!.monthValue - 1, beginDate!!.dayOfMonth)
        val d =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
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
            parent, d,
            dateSetter.get(Calendar.YEAR),
            dateSetter.get(Calendar.MONTH),
            dateSetter.get(Calendar.DAY_OF_MONTH)
        )
            .show()
    }
    fun endDateOnClick(){
        val dateSetter = Calendar.getInstance()
        if (endDate != null)
            dateSetter.set(endDate!!.year, endDate!!.monthValue - 1, endDate!!.dayOfMonth)
        val d =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
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
            parent, d,
            dateSetter.get(Calendar.YEAR),
            dateSetter.get(Calendar.MONTH),
            dateSetter.get(Calendar.DAY_OF_MONTH)
        )
            .show()
    }
    fun beginTimeOnClick(){
        val timeSetter = Calendar.getInstance()
        var t =
            OnTimeSetListener { view, hourOfDay, minute ->
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
            parent, t,
            timeSetter.get(Calendar.HOUR_OF_DAY),
            timeSetter.get(Calendar.MINUTE), true
        )
            .show()
    }
    fun endTimeOnClick(){
        val timeSetter = Calendar.getInstance()
        var t =
            OnTimeSetListener { view, hourOfDay, minute ->
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
            parent, t,
            timeSetter.get(Calendar.HOUR_OF_DAY),
            timeSetter.get(Calendar.MINUTE), true
        )
            .show()
    }
}