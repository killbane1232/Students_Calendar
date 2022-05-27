package com.example.students_calendar.annotations;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        LaunchActivityResult.noData,
        LaunchActivityResult.permissions,
})
@Retention(RetentionPolicy.SOURCE)
public @interface LaunchActivityResult {
    int noData = 0;
    int permissions = 1;

}