package com.example.students_calendar.file_workers

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.students_calendar.Pdf.PDFTableExtractor
import com.example.students_calendar.Pdf.data.Table
import com.example.students_calendar.R
import com.example.students_calendar.data.Note
import com.example.students_calendar.data.NoteState
import com.example.students_calendar.dialogs.NumeratorDialog
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.*
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class NotesFile {
    val context:AppCompatActivity
    private var FILE_NAME:String

    constructor(_context: AppCompatActivity)
    {
        this.context = _context
        FILE_NAME =context.resources.getString(R.string.notes_file)
    }

    public fun WriteNotes(notesList: List<Note>)
    {
        var fos: FileOutputStream? = null
        val text = Gson().toJson(notesList)
        try {
            fos = context.openFileOutput(FILE_NAME, AppCompatActivity.MODE_PRIVATE)

            fos.write(text.toByteArray())
        } catch (ex: IOException) {
            Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show()
        } finally {
            try {
                fos?.close()
            } catch (ex: IOException) {
                Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    public fun ReadNotes() : List<Note>
    {
        var NotesList = listOf<Note>()
        var fis: FileInputStream? = null
        try {
            fis = context.openFileInput(FILE_NAME)
            val listType: Type = object : TypeToken<ArrayList<Note>>() {}.type
            NotesList = GsonBuilder()
                .setLenient()
                .create()
                .fromJson(fis.reader(), listType)
        } catch (ex: Exception) {
            if(!File(FILE_NAME).exists())
                WriteNotes(NotesList)
            Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show()
        } finally {
            try {
                fis?.close()
            } catch (ex: IOException) {
                Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show()
            }
        }
        return NotesList
    }
    public fun ReadNotes(file: File) : List<Note> {
        var NotesList = listOf<Note>()
        var reader:InputStreamReader?=null
        try {
            reader = file.reader()
            val listType: Type = object : TypeToken<List<Note>>() {}.type
            NotesList = GsonBuilder()
                .setLenient()
                .create()
                .fromJson(reader, listType)
        } catch (ex: Exception) {
            Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show()
        } finally {
            try {
                reader?.close()
            } catch (ex: IOException) {
                Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show()
            }
        }
        return NotesList
    }
    fun ReadNotesFromPDF(file: File, date: LocalDate, page:Int) : List<Note> {
        var a = PDFTableExtractor()
        a.setSource(file)
        a.exceptLine(intArrayOf(0,1,2,3,4,5,6,-1,-2))
        a.exceptColumn(intArrayOf(0))
        var table = a.extract(page)

        var pair = 0
        var day = 0
        var i = 0

        while(i<table.rows.size)
        {
            table.rows[i].index=day
            if(table.rows[i].cells[0].content.length<4)
            {
                i+=3
            }
            else
                i+=2
            if(pair>=5){
                pair = -1
                day++
            }
            pair++
        }

        var NotesList:MutableList<Note> = parseTableToNotes(table, date);

        return NotesList
    }

    private fun parseTableToNotes(table: Table, date: LocalDate): MutableList<Note> {
        var result = mutableListOf<Note>()
        var i =0

        var rows = table.rows

        while(i < rows.size-2)
        {
            var note1:Note = Note("",NoteState.New,true)
            var note2:Note = Note("",NoteState.New,true)
            var rowNames = rows[i].cells

            var noteTimeStart = i

            if(rowNames.size<2)
            {
                if(rowNames[0].content.contains(' '))
                    i+=2
                else
                    i+=3
                continue
            }

            var timeStartLocal = LocalTime.parse("9:00", DateTimeFormatter.ofPattern("H:m"))
            while(noteTimeStart<rows.size-2){
                try{
                    var timeStart = rows[noteTimeStart].cells[0].content
                    if(timeStart.contains(' '))
                        timeStart = timeStart.split(' ')[1]
                    timeStartLocal = LocalTime.parse(timeStart, DateTimeFormatter.ofPattern("H:m"))
                    break
                }
                catch(ex:Exception) {
                    noteTimeStart++
                }
            }

            var noteTimeEnd = noteTimeStart+1
            var timeEndLocal = LocalTime.parse("9:00", DateTimeFormatter.ofPattern("H:m"))
            while(noteTimeEnd<rows.size-2){
                try{
                    if(rows[noteTimeEnd].cells.size<2)
                    {
                        noteTimeEnd++
                        continue
                    }
                    var timeEnd = rows[noteTimeEnd].cells[0].content
                    if(timeEnd.contains(' '))
                        timeEnd = timeEnd.split(' ')[1]
                    timeEndLocal = LocalTime.parse(timeEnd, DateTimeFormatter.ofPattern("H:m"))
                    break
                }
                catch(ex:Exception) {
                    noteTimeEnd++
                }
            }

            val dateToWork = date.plusDays(table.rows[i].index.toLong())

            var rowDescriptions = rows[noteTimeEnd].cells

            note1.name= rowNames[1].content
            note1.description = rowDescriptions[1].content
            if(rowNames.size>2){
                note2.name= rowNames[2].content
                note2.description = rowDescriptions[2].content
            }

            note1.startTime = timeStartLocal
            note1.endTime = timeEndLocal
            note2.startTime = timeStartLocal
            note2.endTime = timeEndLocal

            note1.startDate = dateToWork
            note1.endDate = note1.startDate
            note2.startDate = dateToWork.plusDays(7)
            note2.endDate = note2.startDate

            note1.isPeriodic = true
            note2.isPeriodic = true
            note1.periodDays = 14
            note2.periodDays = 14

            note1.isSchedule = true
            note2.isSchedule = true

            if(note1.name.length>1)
                result.add(note1)
            if(note2.name.length>1)
                result.add(note2)
            i = noteTimeEnd+1
        }
        return result
    }
}