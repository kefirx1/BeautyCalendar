package pl.dev.beautycalendar.adapter

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import info.androidhive.fontawesome.FontTextView
import pl.dev.beautycalendar.MainActivity
import pl.dev.beautycalendar.R
import pl.dev.beautycalendar.data.CustomerInfo
import pl.dev.beautycalendar.receiver.MessageReceiver
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class EventsAdapter(private val visitsList: ArrayList<CustomerInfo>, private val applicationContext: Context, private val instance: MainActivity): RecyclerView.Adapter<EventsAdapter.ViewHandler>() {

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    inner class ViewHandler(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val rowLayout: ConstraintLayout = itemView.findViewById(R.id.rowLayout)
        val dateTextView: TextView = itemView.findViewById(R.id.rowTimeEventTextView)
        val nameTextView: TextView = itemView.findViewById(R.id.rowInfoEventTextView)

        val modal: LinearLayout = instance.findViewById(R.id.eventVisitsModal)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHandler {
        return ViewHandler(
            LayoutInflater.from(parent.context).inflate(R.layout.visit_info_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHandler, position: Int) {

        sortVisitsList()

        setExamDetails(holder, position)
        holder.rowLayout.setOnLongClickListener{


            showDialog(holder, position)


            true
        }

    }

    private fun sortVisitsList(){
        visitsList.sortBy {
            it.dateOf[it.dateOf.size-1].date
        }
    }

    private fun showDialog(holder: ViewHandler, position: Int){

        val dialog = Dialog(instance)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.events_long_dialog)

        val backButton: FontTextView = dialog.findViewById(R.id.eventVisitsDialogBackButton)
        val callButton: Button = dialog.findViewById(R.id.callEventButton)
        val messageButton: Button = dialog.findViewById(R.id.messageEventButton)
        val cancelButton: Button = dialog.findViewById(R.id.cancelEventButton)

        backButton.setOnClickListener{
            dialog.dismiss()
        }
        callButton.setOnClickListener{
            callToCustomer(position)
            dialog.dismiss()
            holder.modal.visibility = View.GONE
        }
        messageButton.setOnClickListener{
            messageToCustomer(position)
            dialog.dismiss()
            holder.modal.visibility = View.GONE
        }
        cancelButton.setOnClickListener{
            cancelVisit(holder, position)
            dialog.dismiss()
            holder.modal.visibility = View.GONE
        }

        dialog.show()
    }

    private fun messageToCustomer(position: Int) {
        val messageIntent = Intent(Intent.ACTION_VIEW)
        messageIntent.type = "vnd.android-dir/mms-sms"
        messageIntent.putExtra("address", visitsList[position].telephone)
        instance.startActivity(messageIntent)
    }

    private fun callToCustomer(position: Int) {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:" + visitsList[position].telephone)
        instance.startActivity(dialIntent)
    }

    override fun getItemCount(): Int {
        return visitsList.size
    }


    private fun cancelVisit(holder: ViewHandler, position: Int) {
        Toast.makeText(applicationContext, "Wizyta odwołana", Toast.LENGTH_SHORT).show()

        val messageId = (visitsList[position].dateOf[visitsList[position].dateOf.size-1].date / 1000 / 60).toInt() + visitsList[position].telephone.toInt()

        val intent = Intent(instance, MessageReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            messageId,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = instance.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.cancel(pendingIntent)

        database = FirebaseDatabase.getInstance()
        reference = database.getReference(MainActivity.userName)

        val customer = visitsList[position]

        customer.active = 0

        reference.child(customer.telephone).setValue(customer)
        holder.modal.visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    private fun setExamDetails(holder: ViewHandler, position: Int) {
        holder.dateTextView.text =
            getTimeString(visitsList[position].dateOf[visitsList[position].dateOf.size - 1].date) + " - " + visitsList[position].dateOf[visitsList[position].dateOf.size - 1].service
        holder.nameTextView.text =
            visitsList[position].name + " " + visitsList[position].surname
    }

    private fun getTimeString(dateTimeMillis: Long): String {
        val dateTimeOfVisit =
            LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTimeMillis), ZoneId.systemDefault())
        val hour = dateTimeOfVisit.hour
        var minute = dateTimeOfVisit.minute.toString()

        if (dateTimeOfVisit.minute < 10) {
            minute = "0$minute"
        }

        return "$hour:$minute"
    }
}