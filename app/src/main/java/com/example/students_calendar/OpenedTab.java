package com.example.students_calendar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class OpenedTab extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opened_tab);

        TextView a = findViewById(R.id.textView);
        Bundle arguments = getIntent().getExtras();
        Object obj = arguments.get("dayNumber");
        String name = obj.toString();
        a.setText(name);
    }
}