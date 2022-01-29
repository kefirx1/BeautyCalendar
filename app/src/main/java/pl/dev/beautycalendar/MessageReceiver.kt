package pl.dev.beautycalendar

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.widget.Toast

class MessageReceiver: BroadcastReceiver() {

    companion object {
        const val MESSAGE_EXTRA = "messageExtra"
        const val PHONE_EXTRA = "phoneExtra"
    }

    override fun onReceive(context: Context, intent: Intent) {

        val phoneNumber = intent.getStringExtra(PHONE_EXTRA)!!
        val textMessage = intent.getStringExtra(MESSAGE_EXTRA)!!

        sendMessage(phoneNumber, textMessage)

    }

    private fun sendMessage(phoneNumber: String, textMessage: String) {
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phoneNumber, null, textMessage, null, null)
    }
    
}