package pl.dev.beautycalendar.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.dev.beautycalendar.R
import pl.dev.beautycalendar.classes.DateOfVisits
import pl.dev.beautycalendar.data.VisitsDate

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
            DateOfVisits.getStringTimeDate(visitsList[position].date)
        holder.serviceTextView.text =
            visitsList[position].service
    }

}