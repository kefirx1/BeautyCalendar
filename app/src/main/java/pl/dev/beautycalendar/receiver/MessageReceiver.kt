package pl.dev.beautycalendar.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager

class MessageReceiver: BroadcastReceiver() {

    companion object {
        const val MESSAGE_EXTRA = "messageExtra"
        const val PHONE_EXTRA = "phoneExtra"
    }

    override fun onReceive(context: Context, intent: Intent) {

        val phoneNumber = intent.getStringExtra(PHONE_EXTRA)!!
        val textMessage = intent.getStringExtra(MESSAGE_EXTRA)!!

        sendMessage(
            phoneNumber = phoneNumber,
            textMessage = textMessage,
            context = context
        )

    }

    private fun sendMessage(phoneNumber: String, textMessage: String, context: Context) {
        val smsManager = context.getSystemService(SmsManager::class.java)
        smsManager.sendTextMessage(phoneNumber, null, textMessage, null, null)
    }

}