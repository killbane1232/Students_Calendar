package com.example.students_calendar.data

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class Note(var name: String, var state: NoteState, var isPeriodic: Boolean) {
    var description:String=""
    var startTime: LocalTime? = null
    var endTime:LocalTime? = null
    var startDate: LocalDate? = null
    var endDate: LocalDate? = null
    var periodDays: Int? = null
    var isSchedule:Boolean = false
}