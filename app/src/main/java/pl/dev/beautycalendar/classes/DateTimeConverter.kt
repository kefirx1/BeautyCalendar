package pl.dev.beautycalendar.classes

import android.widget.DatePicker
import android.widget.TimePicker
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

object DateTimeConverter {

    fun getDayBeforeMillis(dateTimeOfVisitMill: Long): Long {
        val dateTimeOfVisit = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(dateTimeOfVisitMill),
            ZoneId.systemDefault()
        )

        val calendar = Calendar.getInstance()

        calendar.set(
            dateTimeOfVisit.year,
            dateTimeOfVisit.monthValue - 1,
            dateTimeOfVisit.dayOfMonth - 1,
            15,
            0,
            0
        )
        return calendar.timeInMillis
    }


    fun getDateOfVisitMillis(customerDatePicker: DatePicker, customerTimePicker: TimePicker): Long {

        val year = customerDatePicker.year
        val month = customerDatePicker.month
        val day = customerDatePicker.dayOfMonth
        val hour = customerTimePicker.hour
        val minute = customerTimePicker.minute

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute)

        return calendar.timeInMillis
    }

    fun getStringTime(dateTimeMillis: Long): String {
        val dateTimeOfVisit = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTimeMillis), ZoneId.systemDefault())
        val hour = dateTimeOfVisit.hour
        var minute = dateTimeOfVisit.minute.toString()

        if(dateTimeOfVisit.minute<10){
            minute = "0$minute"
        }

        return "$hour:$minute"
    }

    fun getStringDateTime(dateTimeOfVisitMillis: Long): String {
        val dateTimeOfVisit = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(dateTimeOfVisitMillis),
            ZoneId.systemDefault()
        )
        val year = dateTimeOfVisit.year
        var month = dateTimeOfVisit.monthValue.toString()
        var day = dateTimeOfVisit.dayOfMonth.toString()
        val hour = dateTimeOfVisit.hour
        var minute = dateTimeOfVisit.minute.toString()

        if(month.length==1){
            month = "0$month"
        }
        if(day.length==1){
            day = "0$day"
        }
        if (dateTimeOfVisit.minute < 10) {
            minute = "0$minute"
        }

        return "$day.$month.$year, godzina $hour:$minute"

    }

    fun getStringTimeDate(dateTimeMillis: Long): String {
        val dateTimeOfVisit =
            LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTimeMillis), ZoneId.systemDefault())
        val year = dateTimeOfVisit.year
        val month = dateTimeOfVisit.monthValue
        val day = dateTimeOfVisit.dayOfMonth
        val hour = dateTimeOfVisit.hour
        var minute = dateTimeOfVisit.minute.toString()

        if (dateTimeOfVisit.minute < 10) {
            minute = "0$minute"
        }

        return "$hour:$minute - $day.$month.$year"
    }

}