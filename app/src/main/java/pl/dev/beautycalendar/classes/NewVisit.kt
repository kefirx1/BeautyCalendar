package pl.dev.beautycalendar.classes

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import pl.dev.beautycalendar.MainActivity
import pl.dev.beautycalendar.data.CustomerInfo

class NewVisit {

    private val scheduleMessage = ScheduleMessage()
    private val makeMessage = MakeMessage()

    fun addNewVisit(customer: CustomerInfo, instance: Activity, applicationContext: Context) {

        MainActivity.viewModel.insertNewVisit(customer)

        Toast.makeText(applicationContext, "Dodano wizytę", Toast.LENGTH_SHORT).show()

        val phoneNumber = "+48" + customer.telephone
        val textMessage = makeMessage.getMessageString(customer)
        val messageId = makeMessage.getMessageId(customer)
        val dateTimeOfVisitMill = customer.dateOf[customer.dateOf.size - 1].date

        if (ContextCompat.checkSelfPermission(
                instance,
                android.Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            scheduleMessage.setScheduleMessage(
                applicationContext,
                instance,
                textMessage,
                phoneNumber,
                messageId,
                dateTimeOfVisitMill
            )

        } else {
            ActivityCompat.requestPermissions(
                instance,
                arrayOf(android.Manifest.permission.SEND_SMS),
                100
            )
        }

        instance.finish()
    }


}