package com.example.students_calendar.activities

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
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
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

        FILE_NAME=resources.getString(R.string.notes_file)

        notesList = findViewById(R.id.notesListView)
        val layoutManger = LinearLayoutManager(applicationContext)
        notesList.layoutManager = layoutManger
        val adapter = NoteAdapter(NotesList,this)
        notesList.adapter = adapter
        Adapter = adapter

        var fis: FileInputStream? = null
        try {
            fis = openFileInput(FILE_NAME)
            val listType: Type = object : TypeToken<ArrayList<Note>>() {}.type
            NotesList.addAll(Gson().fromJson(fis.reader(), listType))
            Adapter.notifyDataSetChanged()
        } catch (ex: Exception) {
            Toast.makeText(this, ex.message, Toast.LENGTH_SHORT).show()
        } finally {
            try {
                fis?.close()
            } catch (ex: IOException) {
                Toast.makeText(this, ex.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun addNoteAction(view: View) {
        val customView = layoutInflater.inflate(R.layout.dialog_create_note, null)
        val beginDate = customView.findViewById<TextInputEditText>(R.id.beginDateText)
        val endDate = customView.findViewById<TextInputEditText>(R.id.endDateText)
        val beginTime = customView.findViewById<TextInputEditText>(R.id.beginTimeText)
        val endTime = customView.findViewById<TextInputEditText>(R.id.endTimeText)
        val periodDate = customView.findViewById<TextInputEditText>(R.id.periodDateText)
        val periodTime = customView.findViewById<TextInputEditText>(R.id.periodTimeText)


        val dates = arrayOf(beginDate,endDate,periodDate)
        val times = arrayOf(beginTime,endTime,periodTime)
        for (i in 0..1)
        {
            val slotsDate = UnderscoreDigitSlotsParser().parseSlots("____-__-__")
            val formatWatcherDate: FormatWatcher = MaskFormatWatcher(
                MaskImpl.createTerminated(slotsDate)
            )
            formatWatcherDate.installOn(dates[i])

            val slotsTime = UnderscoreDigitSlotsParser().parseSlots("__:__")
            val formatWatcherTime: FormatWatcher = MaskFormatWatcher(
                MaskImpl.createTerminated(slotsTime))
            formatWatcherTime.installOn(times[i])
        }

        beginDate.visibility = View.GONE
        endDate.visibility = View.GONE
        beginTime.visibility = View.GONE
        endTime.visibility = View.GONE
        periodDate.visibility = View.GONE
        periodTime.visibility = View.GONE

        val checkBoxDate = customView.findViewById<CheckBox>(R.id.checkBoxDate)
        val checkBoxTime = customView.findViewById<CheckBox>(R.id.checkBoxTime)
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
        AlertDialog.Builder(this)
            .setTitle(R.string.node_create)
            .setView(customView)
            .setCancelable(false)
            .setNegativeButton(android.R.string.cancel) { _, _ ->

            }
            .setPositiveButton(android.R.string.ok) { _, _ ->
                var view = customView.findViewById<TextInputEditText>(R.id.NoteNameInput)
                val name = view.text
                val newNote = Note(name.toString(), NoteState.New, false)
                view = customView.findViewById(R.id.NoteDescriptionInput)
                val description = view.text
                newNote.Description = description.toString()
                NotesList.add(newNote)
                Adapter.notifyItemInserted(NotesList.size-1)
                if(checkBoxDate.isChecked)
                {
                    var dateText = beginDate.text.toString()
                    newNote.StartDate = LocalDate.parse(dateText, DateTimeFormatter.ISO_DATE)
                    dateText = endDate.text.toString()
                    newNote.EndDate = LocalDate.parse(dateText, DateTimeFormatter.ISO_DATE)
                }
                if(checkBoxTime.isChecked)
                {
                    var dateText = beginTime.text.toString()
                    newNote.StartTime = LocalTime.parse(dateText, DateTimeFormatter.ISO_TIME)
                    dateText = endTime.text.toString()
                    newNote.EndTime = LocalTime.parse(dateText, DateTimeFormatter.ISO_TIME)
                }
                if(checkBoxPeriodic.isChecked)
                {

                }
                var fos: FileOutputStream? = null
                val text = Gson().toJson(NotesList)
                try {
                    fos = openFileOutput(FILE_NAME, MODE_PRIVATE)
                    fos.write(text.toByteArray())
                } catch (ex: IOException) {
                    Toast.makeText(this, ex.message, Toast.LENGTH_SHORT).show()
                } finally {
                    try {
                        fos?.close()
                    } catch (ex: IOException) {
                        Toast.makeText(this, ex.message, Toast.LENGTH_SHORT).show()
                    }
                }
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

    }

}