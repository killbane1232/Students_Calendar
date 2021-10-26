package com.example.students_calendar;

import androidx.appcompat.app.AppCompatActivity;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.widget.GridView;

public class MainActivity extends AppCompatActivity {
    Bundle savedInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedInstance = savedInstanceState;
        setContentView(R.layout.activity_main);
        
        GridView dataGrid = findViewById(R.id.dataGrid);
        dataGrid.setNumColumns(7);

        int daysInMonth;
        int month=Calendar.getInstance().get(Calendar.MONTH)+1;
        if(month>6)
            if(month%2==0)
                daysInMonth=31;
            else
                daysInMonth=30;
        else
            if(month==2)
                if(Calendar.getInstance().get(Calendar.YEAR)%4==0)
                    daysInMonth=29;
                else
                    daysInMonth=28;
            else
                if(month%2==0)
                    daysInMonth=30;
                else
                    daysInMonth=31;
        int dayToday = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)+5)%7;
        int dayOfFirstDay = (dayToday-dayOfWeek+7)%7;
        ButtonProperties[] props = new ButtonProperties[42];
        for(int i =0;i<42;i++){
            props[i] = new ButtonProperties(
                    "",
                    "#FAFAFA",
                    false
            );
        }
        for(int i = 1;i<= daysInMonth;i++){
            props[i+dayOfFirstDay-1].Name=Integer.toString(i);
            props[i+dayOfFirstDay-1].Enabled = true;

            if(i != dayToday)
                props[i+dayOfFirstDay-1].Color="#1049F1";
            else
                props[i+dayOfFirstDay-1].Color="#FF0000";
        }
        dataGrid.setVerticalSpacing(5);
        dataGrid.setHorizontalSpacing(5);
        ButtonAdapter adapter = new ButtonAdapter(this, android.R.layout.simple_list_item_1, props);
        dataGrid.setAdapter(adapter);
    }


}