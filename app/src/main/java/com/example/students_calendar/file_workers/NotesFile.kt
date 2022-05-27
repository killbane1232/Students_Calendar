package com.example.students_calendar.file_workers

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.students_calendar.R
import com.example.students_calendar.data.Note
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.*
import java.lang.reflect.Type
import java.util.ArrayList

class NotesFile {
    val context:Context
    private var FILE_NAME:String

    constructor(_context: Context)
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
    public fun ReadNotes(file: File) : List<Note>
    {
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
}