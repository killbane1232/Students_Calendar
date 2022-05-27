package com.example.students_calendar.activities;

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.students_calendar.R
import com.example.students_calendar.annotations.LaunchActivityResult
import com.example.students_calendar.data.Note
import com.example.students_calendar.file_workers.NotesFile
import com.obsez.android.lib.filechooser.ChooserDialog
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


class MenuActivity : AppCompatActivity() {

    lateinit var FILE_NAME:String

    companion object {
        fun getInstance(
            context: Context,
        ): Intent {
            return Intent(context, MenuActivity::class.java)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        initWidgets()
    }

    private fun initWidgets() {
        FILE_NAME=resources.getString(R.string.notes_file)
    }

    fun ExportNotes(view: View) {
        var fis: FileInputStream? = null
        val FILE_NAME = resources.getString(R.string.notes_file)
        try {
            fis = openFileInput(FILE_NAME)
            val file = File(getExternalFilesDir(Environment.DIRECTORY_DCIM),FILE_NAME)
            if(file.exists())
                file.delete()
            file.createNewFile()
            file.appendBytes(fis.readBytes())
            Toast.makeText(this,file.path+file.name , Toast.LENGTH_SHORT).show()
        } catch (ex: Exception) {
            Toast.makeText(this, ex.message, Toast.LENGTH_SHORT).show()
        }
        finally {
            try {
                fis?.close()
                // fos?.close()
            } catch (ex: IOException) {
                Toast.makeText(this, ex.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun ImportNotes(view: View) {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                LaunchActivityResult.permissions
            )
        } else {
            ChooserDialog(this)
                .withFilter(false, false, "json")
                .withChosenListener { _, file ->
                    val notesFile = NotesFile(this)
                    var notes:MutableList<Note>
                    try {
                        notes = notesFile.ReadNotes().toMutableList()
                    }
                    catch (ex:IOException)
                    {
                        notes = mutableListOf()
                    }
                    var newNotes:List<Note>
                    try {
                        newNotes = notesFile.ReadNotes(file)
                        newNotes.forEach{
                            var new = it
                            var note = notes.find ({ (it.Name == new.Name) })
                            if(note==null)
                            {
                                notes.add(it)
                            }
                            else
                                if( it.Description != new.Description ||
                                    it.IsPeriodic != new.IsPeriodic ||
                                    it.EndDate != new.EndDate ||
                                    it.StartDate != new.StartDate ||
                                    it.EndTime != new.EndTime ||
                                    it.StartTime != new.StartTime ||
                                    it.PeriodMinutes != new.PeriodMinutes ||
                                    it.PeriodDays != new.PeriodDays ||
                                    it.PeriodMonths != new.PeriodMonths||
                                    it.PeriodYears != new.PeriodYears)
                                    notes.add(it)
                        }
                        notesFile.WriteNotes(notes)
                    }
                    catch (ex:Exception)
                    {
                        Toast.makeText(this, ex.message, Toast.LENGTH_LONG)
                    }
                }
                .build()
                .show()
        }
    }
    fun WipeNotes(view: View) {
        try{
            deleteFile(FILE_NAME)
        }
        catch (ex: IOException) {
            Toast.makeText(this, ex.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun calendarAction(view: View) {
        this.startActivity(MainActivity.getInstance(this))}
    fun notesAction(view: View) {
        this.startActivity(NotesActivity.getInstance(this))}
    fun menuAction(view: View) {}
}