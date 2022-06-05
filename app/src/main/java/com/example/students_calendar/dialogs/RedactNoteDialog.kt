package com.example.students_calendar.dialogs

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.graphics.pdf.PdfDocument
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
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*


class RedactNoteDialog {
    val parent: AppCompatActivity
    var active: Boolean
    constructor(activity: AppCompatActivity) {
        this.parent = activity
        this.active = false
    }

    fun createDialog(adapter: NoteAdapter, position: Int) {
        val customView = parent.layoutInflater.inflate(R.layout.dialog_create_note, null)
        val beginDateButton = customView.findViewById<Button>(R.id.beginDateText)
        val endDateButton = customView.findViewById<Button>(R.id.endDateText)
        val beginTimeButton = customView.findViewById<Button>(R.id.beginTimeText)
        val endTimeButton = customView.findViewById<Button>(R.id.endTimeText)
        val periodDate = customView.findViewById<TextInputEditText>(R.id.periodDateText)
        val periodTime = customView.findViewById<TextInputEditText>(R.id.periodTimeText)

        var beginDate:LocalDate?=null
        var endDate:LocalDate?=null
        var beginTime:LocalTime?=null
        var endTime:LocalTime?=null

        beginDateButton.setOnClickListener{
            val dateSetter = Calendar.getInstance()
            if(beginDate!=null)
                dateSetter.set(beginDate!!.year,beginDate!!.monthValue-1,beginDate!!.dayOfMonth)
            val d =
                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    dateSetter.set(Calendar.YEAR, year)
                    dateSetter.set(Calendar.MONTH, monthOfYear)
                    dateSetter.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    beginDate = LocalDate.of(year,monthOfYear+1,dayOfMonth)
                    beginDateButton.text = beginDate.toString()
                    if(endDate==null||beginDate!!.isAfter(endDate))
                    {
                        endDate = LocalDate.of(year,monthOfYear+1,dayOfMonth)
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
        endDateButton.setOnClickListener{
            val dateSetter = Calendar.getInstance()
            if(endDate!=null)
                dateSetter.set(endDate!!.year,endDate!!.monthValue-1,endDate!!.dayOfMonth)
            val d =
                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    dateSetter.set(Calendar.YEAR, year)
                    dateSetter.set(Calendar.MONTH, monthOfYear)
                    dateSetter.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    endDate = LocalDate.of(year,monthOfYear+1,dayOfMonth)
                    endDateButton.text = endDate.toString()
                    if(beginDate==null||endDate!!.isBefore(beginDate))
                    {
                        beginDate = LocalDate.of(year,monthOfYear+1,dayOfMonth)
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
        beginTimeButton.setOnClickListener{
            val timeSetter = Calendar.getInstance()
            var t =
                OnTimeSetListener { view, hourOfDay, minute ->
                    timeSetter.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    timeSetter.set(Calendar.MINUTE, minute)
                    beginTime = LocalTime.of(hourOfDay,minute)
                    beginTimeButton.text = beginTime.toString()
                    if(endTime==null||beginTime!!.isAfter(endTime)) {
                        endTime = LocalTime.of(hourOfDay,minute)
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
        endTimeButton.setOnClickListener{
            val timeSetter = Calendar.getInstance()
            var t =
                OnTimeSetListener { view, hourOfDay, minute ->
                    timeSetter.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    timeSetter.set(Calendar.MINUTE, minute)
                    endTime = LocalTime.of(hourOfDay,minute)
                    endTimeButton.text = endTime.toString()
                    if(beginTime==null||endTime!!.isBefore(beginTime)) {
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

        val slotsDate = UnderscoreDigitSlotsParser().parseSlots("____-__-__")
        val formatWatcherDate: FormatWatcher = MaskFormatWatcher(
            MaskImpl.createTerminated(slotsDate)
        )
        formatWatcherDate.installOn(periodDate)

        val slotsTime = UnderscoreDigitSlotsParser().parseSlots("__:__")
        val formatWatcherTime: FormatWatcher = MaskFormatWatcher(
            MaskImpl.createTerminated(slotsTime)
        )
        formatWatcherTime.installOn(periodTime)

        beginDateButton.visibility = View.GONE
        endDateButton.visibility = View.GONE
        beginTimeButton.visibility = View.GONE
        endTimeButton.visibility = View.GONE
        periodDate.visibility = View.GONE
        periodTime.visibility = View.GONE

        val note = adapter.NotesList.get(position)

        val nameView = customView.findViewById<TextInputEditText>(R.id.NoteNameInput)
        nameView.setText(note.Name)
        val stateView = customView.findViewById<CheckBox>(R.id.checkBoxState)
        stateView.isChecked = (note.State == NoteState.Complete)
        val descriptionView = customView.findViewById<TextInputEditText>(R.id.NoteDescriptionInput)
        descriptionView.setText(note.Description)

        val checkBoxDate = customView.findViewById<CheckBox>(R.id.checkBoxDate)
        if(note.StartDate!=null && note.EndDate != null){
            checkBoxDate.isChecked = true
            beginDateButton.visibility = View.VISIBLE
            beginDateButton.setText(note.StartDate?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            endDateButton.visibility = View.VISIBLE
            endDateButton.setText(note.EndDate?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        }
        val checkBoxTime = customView.findViewById<CheckBox>(R.id.checkBoxTime)
        if(note.StartTime!=null){
            checkBoxTime.isChecked = true
            beginTimeButton.visibility = View.VISIBLE
            beginTimeButton.setText(note.StartTime?.format(DateTimeFormatter.ofPattern("HH:mm")))
            endTimeButton.visibility = View.VISIBLE
            endTimeButton.setText(note.EndTime?.format(DateTimeFormatter.ofPattern("HH:mm")))
        }
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
                periodTime.visibility = View.VISIBLE
            } else {
                periodDate.visibility = View.GONE
                periodTime.visibility = View.GONE
            }
        }

        AlertDialog.Builder(parent)
            .setTitle(R.string.node_create)
            .setView(customView)
            .setCancelable(false)
            .setNegativeButton(android.R.string.cancel) { _, _ ->
            }
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val name = nameView.text
                note.Name = name.toString()
                if (stateView.isChecked)
                    note.State = NoteState.Complete
                else
                    note.State = NoteState.New

                note.Description = descriptionView.text.toString()
                if (checkBoxDate.isChecked) {
                    note.StartDate = beginDate
                    note.EndDate = endDate
                }
                if (checkBoxTime.isChecked) {
                    note.StartTime = beginTime
                    note.EndTime = endTime
                }
                if (checkBoxPeriodic.isChecked) {
                }
                adapter.notifyItemChanged(position)
                NotesFile(parent).WriteNotes(adapter.NotesList)
            }.show()
    }
}