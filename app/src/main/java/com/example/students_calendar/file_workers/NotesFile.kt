package com.example.students_calendar.file_workers

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.students_calendar.R
import com.example.students_calendar.data.Note
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdfparser.PDFParser
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import java.io.*
import java.lang.reflect.Type


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
    public fun ReadNotesFromPDF(file: File) : List<Note>
    {
        PDFBoxResourceLoader.init(context.applicationContext);
        var document = PDDocument.load(file)

        val pdfStripper = PDFTextStripper()
        pdfStripper.startPage=0
        pdfStripper.endPage=1

        var str = pdfStripper.getText(document)

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