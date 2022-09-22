package com.example.students_calendar.data

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class Note(name: String, state: NoteState, periodic: Boolean) {
    var name:String = name
    var state:NoteState = state
    var description:String=""
    var startTime: LocalTime? = null
    var endTime:LocalTime? = null
    var startDate: LocalDate? = null
    var endDate: LocalDate? = null
    var isPeriodic:Boolean = periodic
    var periodDays: Int? = null
    var isSchedule:Boolean = false
}