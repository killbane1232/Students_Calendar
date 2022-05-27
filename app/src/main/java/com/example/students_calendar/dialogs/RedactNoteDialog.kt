package com.example.students_calendar.dialogs

import android.view.View
import android.widget.Adapter
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.students_calendar.R
import com.example.students_calendar.data.Note
import com.example.students_calendar.data.NoteState
import com.google.android.material.textfield.TextInputEditText
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import androidx.lifecycle.Observer
import androidx.lifecycle.LiveData
import com.example.students_calendar.adapters.NoteAdapter
import com.example.students_calendar.file_workers.NotesFile
import java.text.FieldPosition

class RedactNoteDialog {
    val parent: AppCompatActivity
    var active: Boolean
    constructor(activity: AppCompatActivity) {
        this.parent = activity
        this.active = false
    }

    fun createDialog(adapter: NoteAdapter, position: Int) {
        val customView = parent.layoutInflater.inflate(R.layout.dialog_create_note, null)
        val beginDate = customView.findViewById<TextInputEditText>(R.id.beginDateText)
        val endDate = customView.findViewById<TextInputEditText>(R.id.endDateText)
        val beginTime = customView.findViewById<TextInputEditText>(R.id.beginTimeText)
        val endTime = customView.findViewById<TextInputEditText>(R.id.endTimeText)
        val periodDate = customView.findViewById<TextInputEditText>(R.id.periodDateText)
        val periodTime = customView.findViewById<TextInputEditText>(R.id.periodTimeText)


        val dates = arrayOf(beginDate, endDate, periodDate)
        val times = arrayOf(beginTime, endTime, periodTime)
        for (i in 0..1) {
            val slotsDate = UnderscoreDigitSlotsParser().parseSlots("____-__-__")
            val formatWatcherDate: FormatWatcher = MaskFormatWatcher(
                MaskImpl.createTerminated(slotsDate)
            )
            formatWatcherDate.installOn(dates[i])

            val slotsTime = UnderscoreDigitSlotsParser().parseSlots("__:__")
            val formatWatcherTime: FormatWatcher = MaskFormatWatcher(
                MaskImpl.createTerminated(slotsTime)
            )
            formatWatcherTime.installOn(times[i])
        }

        beginDate.visibility = View.GONE
        endDate.visibility = View.GONE
        beginTime.visibility = View.GONE
        endTime.visibility = View.GONE
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
            beginDate.visibility = View.VISIBLE
            beginDate.setText(note.StartDate?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            endDate.visibility = View.VISIBLE
            endDate.setText(note.EndDate?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        }
        val checkBoxTime = customView.findViewById<CheckBox>(R.id.checkBoxTime)
        if(note.StartTime!=null){
            checkBoxTime.isChecked = true
            beginTime.visibility = View.VISIBLE
            beginTime.setText(note.StartTime?.format(DateTimeFormatter.ofPattern("HH:mm")))
            endTime.visibility = View.VISIBLE
            endTime.setText(note.EndTime?.format(DateTimeFormatter.ofPattern("HH:mm")))
        }
        val checkBoxPeriodic = customView.findViewById<CheckBox>(R.id.checkBoxPeriodic)
        checkBoxDate.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
            if (b) {
                beginDate.visibility = View.VISIBLE
                endDate.visibility = View.VISIBLE
            } else {
                beginDate.visibility = View.GONE
                endDate.visibility = View.GONE
            }
        }
        checkBoxTime.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
            if (b) {
                beginTime.visibility = View.VISIBLE
                endTime.visibility = View.VISIBLE
            } else {
                beginTime.visibility = View.GONE
                endTime.visibility = View.GONE
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

        val dialogBuilder = AlertDialog.Builder(parent)
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
                    var dateText = beginDate.text.toString()
                    note.StartDate = LocalDate.parse(dateText, DateTimeFormatter.ISO_DATE)
                    dateText = endDate.text.toString()
                    note.EndDate = LocalDate.parse(dateText, DateTimeFormatter.ISO_DATE)
                }
                if (checkBoxTime.isChecked) {
                    var dateText = beginTime.text.toString()
                    note.StartTime = LocalTime.parse(dateText, DateTimeFormatter.ISO_TIME)
                    dateText = endTime.text.toString()
                    note.EndTime = LocalTime.parse(dateText, DateTimeFormatter.ISO_TIME)
                }
                if (checkBoxPeriodic.isChecked) {
                }
                adapter.notifyItemChanged(position)
                NotesFile(parent).WriteNotes(adapter.NotesList)
            }.show()
    }
}