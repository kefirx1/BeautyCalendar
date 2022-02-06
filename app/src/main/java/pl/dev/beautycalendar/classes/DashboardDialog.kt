package pl.dev.beautycalendar.classes

import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.Toast
import pl.dev.beautycalendar.MainActivity
import pl.dev.beautycalendar.R
import pl.dev.beautycalendar.adapter.EventsAdapter
import pl.dev.beautycalendar.data.CustomerInfo
import pl.dev.beautycalendar.receiver.MessageReceiver

class DashboardDialog(private val visitsList: ArrayList<CustomerInfo>, private val applicationContext: Context, private val instance: MainActivity) {

    private val makeMessage = MakeMessage()

    fun showDialog(holder: EventsAdapter.ViewHandler, position: Int) {

        val dialog = Dialog(instance)
        dialog.requestWindowFeature(Window.FEATURE_OPTIONS_PANEL)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.events_long_dialog)

        val callButton: Button = dialog.findViewById(R.id.callEventButton)
        val messageButton: Button = dialog.findViewById(R.id.messageEventButton)
        val cancelButton: Button = dialog.findViewById(R.id.cancelEventButton)

        callButton.setOnClickListener {
            Customer.callToCustomer(visitsList[position], instance)
            dialog.dismiss()
            holder.modal.visibility = View.GONE
        }
        messageButton.setOnClickListener {
            Customer.messageToCustomer(visitsList[position], instance)
            dialog.dismiss()
            holder.modal.visibility = View.GONE
        }
        cancelButton.setOnClickListener {
            cancelVisit(holder, position)
            dialog.dismiss()
            holder.modal.visibility = View.GONE
        }

        dialog.show()
    }


    private fun cancelVisit(holder: EventsAdapter.ViewHandler, position: Int) {
        Toast.makeText(applicationContext, "Wizyta odwołana", Toast.LENGTH_SHORT).show()

        val messageId = makeMessage.getMessageId(visitsList[position])

        val intent = Intent(instance, MessageReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            messageId,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = instance.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.cancel(pendingIntent)

        val customer = visitsList[position]

        customer.active = 0

        MainActivity.viewModel.cancelVisit(customer)

        holder.modal.visibility = View.GONE
    }


}