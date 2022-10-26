package com.example.students_calendar.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.students_calendar.R
import com.example.students_calendar.annotations.LaunchActivityResult
import com.example.students_calendar.data.Note
import com.example.students_calendar.dialogs.FileChooserDialog
import com.example.students_calendar.file_workers.NotesFile
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import java.io.File
import java.io.FileInputStream
import java.io.IOException


class MenuActivity : AppCompatActivity() {

    lateinit var fileName:String

    companion object {
        fun getInstance(
            context: Context,
        ): Intent {
            return Intent(context, MenuActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        initWidgets()
    }

    private fun initWidgets() {
        fileName=resources.getString(R.string.notes_file)
    }

    fun ExportNotes(view: View) {
        var fis: FileInputStream? = null
        val fileName = resources.getString(R.string.notes_file)
        try {
            fis = openFileInput(fileName)
            val file = File(getExternalFilesDir(Environment.DIRECTORY_DCIM),fileName)
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
            FileChooserDialog(this).showDialog("json")
        }
    }
    fun ImportNotesPDF(view: View) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                LaunchActivityResult.permissions
            )
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()){
            requestPermissions(this)
        } else {
            PDFBoxResourceLoader.init(applicationContext)
            FileChooserDialog(this).showDialog("pdf")
        }
    }
    fun requestPermissions(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(String.format("package:%s", activity.packageName))
                activity.startActivityForResult(intent, 101)
            } catch (e: java.lang.Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                activity.startActivityForResult(intent, 101)
            }
        } else {
            ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                101
            )
        }
    }
    fun WipeShedule(view: View) {
        try{
            val file = NotesFile(this)
            var notes = mutableListOf<Note>()
            notes.addAll(file.readNotes())
            notes.removeAll { it.isSchedule }
            file.writeNotes(notes)
        }
        catch (ex: IOException) {
            Toast.makeText(this, ex.message, Toast.LENGTH_SHORT).show()
        }
    }
    fun WipeNotes(view: View) {
        try{
            deleteFile(fileName)
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