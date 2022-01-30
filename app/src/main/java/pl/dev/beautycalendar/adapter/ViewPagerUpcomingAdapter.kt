package pl.dev.beautycalendar.adapter

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import pl.dev.beautycalendar.MainActivity
import pl.dev.beautycalendar.R
import pl.dev.beautycalendar.data.CustomerInfo
import pl.dev.beautycalendar.receiver.MessageReceiver
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class ViewPagerUpcomingAdapter(private val visitsList: ArrayList<CustomerInfo>, private val applicationContext: Context, private val instance: MainActivity): RecyclerView.Adapter<ViewPagerUpcomingAdapter.Pager2ViewHandler>() {

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    inner class Pager2ViewHandler(itemView: View): RecyclerView.ViewHolder(itemView) {

        val nameTextView: TextView = itemView.findViewById(R.id.eventPageNameTextView)
        val telephoneTextView: TextView = itemView.findViewById(R.id.eventPageTelephoneTextView)
        val serviceTextView: TextView = itemView.findViewById(R.id.eventPageServiceTextView)
        val cancelButton: Button = itemView.findViewById(R.id.cancelEventVisitButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Pager2ViewHandler {
        return Pager2ViewHandler(LayoutInflater.from(parent.context).inflate(R.layout.visit_info_page, parent, false))
    }

    override fun onBindViewHolder(holder: Pager2ViewHandler, position: Int) {
        setCancelVisitButtonListener(holder, position)
        setExamDetails(holder, position)
    }

    override fun getItemCount(): Int {
        return visitsList.size
    }


    private fun setCancelVisitButtonListener(holder: Pager2ViewHandler, position: Int) {
        holder.cancelButton.setOnClickListener {
            Toast.makeText(applicationContext, "Wizyta odwołana", Toast.LENGTH_SHORT).show()

            val messageId = (visitsList[position].date / 1000 / 60).toInt()

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
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setExamDetails(holder: Pager2ViewHandler, position: Int){
        holder.nameTextView.text = visitsList[position].name + " " + visitsList[position].surname
        holder.telephoneTextView.text = visitsList[position].telephone
        holder.serviceTextView.text =  getTimeString(visitsList[position].date) + " - " + visitsList[position].service
    }

    private fun getTimeString(dateTimeMillis: Long): String {
        val dateTimeOfVisit = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTimeMillis), ZoneId.systemDefault())
        val hour = dateTimeOfVisit.hour
        var minute = dateTimeOfVisit.minute.toString()

        if(dateTimeOfVisit.minute<10){
            minute = "0$minute"
        }

        return "$hour:$minute"
    }
}