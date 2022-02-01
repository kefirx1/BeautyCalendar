package pl.dev.beautycalendar.classes

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import pl.dev.beautycalendar.receiver.MessageReceiver

class ScheduleMessage {

    private val dateOfVisits = DateOfVisits()

    fun setScheduleMessage(
        applicationContext: Context,
        instance: Activity,
        textMessage: String,
        phoneNumber: String,
        messageId: Int,
        dateTimeOfVisitMill: Long
    ) {
        val intent = Intent(instance, MessageReceiver::class.java).apply {
            putExtra(MessageReceiver.MESSAGE_EXTRA, textMessage)
            putExtra(MessageReceiver.PHONE_EXTRA, phoneNumber)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            messageId,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = instance.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val dayBeforeVisitMillis = dateOfVisits.getDayBeforeMillis(dateTimeOfVisitMill)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            dayBeforeVisitMillis,
            pendingIntent
        )
    }

}