package com.example.students_calendar.file_workers

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.students_calendar.Pdf.PDFTableExtractor
import com.example.students_calendar.Pdf.data.Table
import com.example.students_calendar.R
import com.example.students_calendar.data.Note
import com.example.students_calendar.data.NoteState
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.io.*
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class NotesFile(_context: AppCompatActivity) {
    val context: AppCompatActivity = _context
    private var fileName: String = context.resources.getString(R.string.notes_file)

    fun writeNotes(notesList: List<Note>) {
        var fos: FileOutputStream? = null
        val text = GsonBuilder()
            .setLenient()
            .registerTypeAdapter(object : TypeToken<LocalDate>() {}.type, LocalDateConverter())
            .registerTypeAdapter(object : TypeToken<LocalTime>() {}.type, LocalTimeConverter())
            .create()
            .toJson(notesList)
        try {
            fos = context.openFileOutput(fileName, AppCompatActivity.MODE_PRIVATE)

            fos.write(text.toByteArray())
        } catch (ex: IOException) {
            Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show()
        } finally {
            try {
                fos?.close()
            } catch (ex: IOException) {
                Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun readNotes(): List<Note> {
        var notesList = listOf<Note>()
        var fis: FileInputStream? = null
        try {
            fis = context.openFileInput(fileName)
            val listType: Type = object : TypeToken<ArrayList<Note>>() {}.type
            notesList = GsonBuilder()
                .setLenient()
                .registerTypeAdapter(object : TypeToken<LocalDate>() {}.type, LocalDateConverter())
                .registerTypeAdapter(object : TypeToken<LocalTime>() {}.type, LocalTimeConverter())
                .create()
                .fromJson(fis.reader(), listType)
        } catch (ex: Exception) {
            if (!File(fileName).exists())
                writeNotes(notesList)
            Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show()
        } finally {
            try {
                fis?.close()
            } catch (ex: IOException) {
                Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show()
            }
        }
        return notesList
    }

    fun readNotes(file: File): List<Note> {
        var notesList = listOf<Note>()
        var reader: InputStreamReader? = null
        try {
            reader = file.reader()
            val listType: Type = object : TypeToken<List<Note>>() {}.type
            notesList = GsonBuilder()
                .setLenient()
                .registerTypeAdapter(object : TypeToken<LocalDate>() {}.type, LocalDateConverter())
                .registerTypeAdapter(object : TypeToken<LocalTime>() {}.type, LocalTimeConverter())
                .create()
                .fromJson(reader, listType)
        } catch (ex: Exception) {
            Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show()
        } finally {
            try {
                reader?.close()
            } catch (ex: IOException) {
                Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show()
            }
        }
        return notesList
    }

    fun readNotesFromPDF(file: File, date: LocalDate, page: Int): List<Note> {
        val a = PDFTableExtractor()
        a.setSource(file)
        a.exceptLine(intArrayOf(0, 1, 2, 3, 4, 5, 6, -1, -2))
        a.exceptColumn(intArrayOf(0))
        val table = a.extract(page)

        var pair = 0
        var day = 0
        var i = 0

        while (i < table.rows.size) {
            table.rows[i].dayIndex = day
            i += if (table.rows[i].cells[0].content.length < 4) {
                3
            } else
                2
            if (pair >= 5) {
                pair = -1
                day++
            }
            pair++
        }

        return parseTableToNotes(table, date)
    }

    private fun parseTableToNotes(table: Table, date: LocalDate): MutableList<Note> {
        val result = mutableListOf<Note>()
        var i = 0

        val rows = table.rows

        while (i < rows.size - 2) {
            val note1 = Note("", NoteState.New, true)
            val note2 = Note("", NoteState.New, true)
            val rowNames = rows[i].cells

            var noteTimeStart = i

            if (rowNames.size < 2) {
                i += if (rowNames[0].content.contains(' '))
                    2
                else
                    3
                continue
            }

            var timeStartLocal = LocalTime.parse("9:00", DateTimeFormatter.ofPattern("H:m"))
            while (noteTimeStart < rows.size - 2) {
                try {
                    var timeStart = rows[noteTimeStart].cells[0].content
                    if (timeStart.contains(' '))
                        timeStart = timeStart.split(' ')[1]
                    timeStartLocal = LocalTime.parse(timeStart, DateTimeFormatter.ofPattern("H:m"))
                    break
                } catch (ex: Exception) {
                    noteTimeStart++
                }
            }

            var noteTimeEnd = noteTimeStart + 1
            var timeEndLocal = LocalTime.parse("9:00", DateTimeFormatter.ofPattern("H:m"))
            while (noteTimeEnd < rows.size - 2) {
                try {
                    if (rows[noteTimeEnd].cells.size < 2) {
                        noteTimeEnd++
                        continue
                    }
                    var timeEnd = rows[noteTimeEnd].cells[0].content
                    if (timeEnd.contains(' '))
                        timeEnd = timeEnd.split(' ')[1]
                    timeEndLocal = LocalTime.parse(timeEnd, DateTimeFormatter.ofPattern("H:m"))
                    break
                } catch (ex: Exception) {
                    noteTimeEnd++
                }
            }

            val dateToWork = date.plusDays(table.rows[i].dayIndex.toLong())

            val rowDescriptions = rows[noteTimeEnd].cells

            note1.name = rowNames[1].content
            note1.description = rowDescriptions[1].content
            if (rowNames.size > 2) {
                note2.name = rowNames[2].content
                note2.description = rowDescriptions[2].content
            }

            note1.startTime = timeStartLocal
            note1.endTime = timeEndLocal
            note2.startTime = timeStartLocal
            note2.endTime = timeEndLocal

            note1.startDate = dateToWork
            note1.endDate = note1.startDate
            note2.startDate = dateToWork.plusDays(7)
            note2.endDate = note2.startDate

            note1.isPeriodic = true
            note2.isPeriodic = true
            note1.periodDays = 14
            note2.periodDays = 14

            note1.isSchedule = true
            note2.isSchedule = true

            if (note1.name.length > 1)
                result.add(note1)
            if (note2.name.length > 1)
                result.add(note2)
            i = noteTimeEnd + 1
        }
        return result
    }

    class LocalDateConverter : JsonSerializer<LocalDate?>,
        JsonDeserializer<LocalDate?> {
        override fun serialize(
            src: LocalDate?,
            typeOfSrc: Type?,
            context: JsonSerializationContext?
        ): JsonElement {
            return JsonPrimitive(DateTimeFormatter.ISO_LOCAL_DATE.format(src))
        }

        @Throws(JsonParseException::class)
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): LocalDate {
            return DateTimeFormatter.ISO_LOCAL_DATE.parse(json.asString, LocalDate::from)
        }
    }

    class LocalTimeConverter : JsonSerializer<LocalTime?>,
        JsonDeserializer<LocalTime?> {
        override fun serialize(
            src: LocalTime?,
            typeOfSrc: Type?,
            context: JsonSerializationContext?
        ): JsonElement {
            return JsonPrimitive(DateTimeFormatter.ISO_LOCAL_TIME.format(src))
        }

        @Throws(JsonParseException::class)
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): LocalTime {
            return DateTimeFormatter.ISO_LOCAL_TIME.parse(json.asString, LocalTime::from)
        }
    }
}