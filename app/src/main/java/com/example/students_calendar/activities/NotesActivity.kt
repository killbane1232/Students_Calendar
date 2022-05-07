package com.example.students_calendar.activities

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.students_calendar.R
import java.time.LocalDate

class NotesActivity : AppCompatActivity() {

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

        initWidgets()
    }

    private fun initWidgets() {


    }

    fun calendarAction(view: View) {
        this.startActivity(MainActivity.getInstance(this))}
    fun notesAction(view: View) {}
    fun menuAction(view: View) {
        this.startActivity(MenuActivity.getInstance(this))}
    fun previousMonthAction(view: View) {}
    fun nextMonthAction(view: View) {}
}