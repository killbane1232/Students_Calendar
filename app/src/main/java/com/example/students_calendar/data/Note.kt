package com.example.students_calendar.data

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class Note(name: String, state: NoteState, periodic: Boolean) {
    var Name:String = name
    var State:NoteState = state
    var Description:String=""
    var StartTime: LocalTime? = null
    var EndTime:LocalTime? = null
    var StartDate: LocalDate? = null
    var EndDate: LocalDate? = null
    var IsPeriodic:Boolean = periodic
    var PeriodYears: Int? = null
    var PeriodMonths: Int? = null
    var PeriodDays: Int? = null
    var PeriodMinutes: Int? = null
}