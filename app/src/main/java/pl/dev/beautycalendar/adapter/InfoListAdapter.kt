package pl.dev.beautycalendar.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import pl.dev.beautycalendar.R
import pl.dev.beautycalendar.data.VisitsDate
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class InfoListAdapter(private val visitsList: ArrayList<VisitsDate>): RecyclerView.Adapter<InfoListAdapter.ViewHandler>() {

    inner class ViewHandler(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val dateTextView: TextView = itemView.findViewById(R.id.customersDateTimeTextView)
        val serviceTextView: TextView = itemView.findViewById(R.id.customersServiceTextView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHandler {
        return ViewHandler(
            LayoutInflater.from(parent.context).inflate(R.layout.customers_info_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHandler, position: Int) {

        setVisitsDetails(holder, position)

    }


    override fun getItemCount(): Int {
        return visitsList.size
    }


    @SuppressLint("SetTextI18n")
    private fun setVisitsDetails(holder: ViewHandler, position: Int) {
        holder.dateTextView.text =
            getDateTimeString(visitsList[position].date)
        holder.serviceTextView.text =
            visitsList[position].service
    }

    private fun getDateTimeString(dateTimeMillis: Long): String {
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