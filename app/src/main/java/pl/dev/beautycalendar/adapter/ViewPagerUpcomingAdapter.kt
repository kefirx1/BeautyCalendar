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

    inner class Pager2ViewHandler(itemView: View): RecyclerView.ViewHolder(itemView) {

        val nameTextView: TextView = itemView.findViewById(R.id.eventPageNameTextView)
        val telephoneTextView: TextView = itemView.findViewById(R.id.eventPageTelephoneTextView)
        val serviceTextView: TextView = itemView.findViewById(R.id.eventPageServiceTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Pager2ViewHandler {
        return Pager2ViewHandler(LayoutInflater.from(parent.context).inflate(R.layout.visit_info_page, parent, false))
    }

    override fun onBindViewHolder(holder: Pager2ViewHandler, position: Int) {
        sortVisitsList()
        setExamDetails(holder, position)
    }

    private fun sortVisitsList(){
        visitsList.sortBy {
            it.dateOf[it.dateOf.size-1].date
        }
    }

    override fun getItemCount(): Int {
        return visitsList.size
    }

    @SuppressLint("SetTextI18n")
    private fun setExamDetails(holder: Pager2ViewHandler, position: Int){
        holder.nameTextView.text = visitsList[position].name + " " + visitsList[position].surname
        holder.telephoneTextView.text = visitsList[position].telephone
        holder.serviceTextView.text =  getTimeString(visitsList[position].dateOf[visitsList[position].dateOf.size-1].date) + " - " + visitsList[position].dateOf[visitsList[position].dateOf.size-1].service
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