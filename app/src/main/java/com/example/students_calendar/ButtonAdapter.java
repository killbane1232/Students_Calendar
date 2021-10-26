package com.example.students_calendar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;

import java.util.jar.Attributes;

public class ButtonAdapter extends ArrayAdapter<ButtonProperties> {
    Context mContext;
    ButtonProperties[] mContent;
    // Конструктор
    public ButtonAdapter(Context context, int textViewResourceId, ButtonProperties[] array) {
        super(context, textViewResourceId, array);
        this.mContext = context;
        this.mContent = array;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Button label = (Button) convertView;

        if (convertView == null) {
            convertView = new Button(this.mContext);
            label = (Button) convertView;
        }

        label.setText(this.mContent[position].Name);
        label.setBackgroundColor(Color.parseColor(this.mContent[position].Color));
        label.setEnabled(this.mContent[position].Enabled);
        label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button sender = (Button)view;
                Intent intent = new Intent(mContext, OpenedTab.class);

                intent.putExtra("dayNumber", Integer.getInteger(sender.getText().toString()));
                //intent.putExtra("dayOfWeek", props)

                mContext.startActivity(intent);
            }
        });
        return (convertView);
    }

    // возвращает содержимое выделенного элемента списка
    public ButtonProperties getItem(int position) {
        return this.mContent[position];
    }

}