package com.example.students_calendar.activities;

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build
import android.os.Bundle;
import android.view.View
import android.widget.TextView;
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.example.students_calendar.R
import com.example.students_calendar.adapters.CalendarAdapter
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

class MenuActivity : AppCompatActivity() {
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

    }

    fun calendarAction(view: View) {
        this.startActivity(MainActivity.getInstance(this))}
    fun notesAction(view: View) {
        this.startActivity(NotesActivity.getInstance(this))}
    fun menuAction(view: View) {}
}