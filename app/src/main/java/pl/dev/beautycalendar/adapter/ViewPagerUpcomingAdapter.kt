package pl.dev.beautycalendar.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.dev.beautycalendar.R
import pl.dev.beautycalendar.classes.DateOfVisits
import pl.dev.beautycalendar.data.CustomerInfo

class ViewPagerUpcomingAdapter(private val visitsList: ArrayList<CustomerInfo>): RecyclerView.Adapter<ViewPagerUpcomingAdapter.Pager2ViewHandler>() {

    private val dateOfVisits = DateOfVisits()

    inner class Pager2ViewHandler(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nameTextView: TextView = itemView.findViewById(R.id.eventPageNameTextView)
        val telephoneTextView: TextView = itemView.findViewById(R.id.eventPageTelephoneTextView)
        val serviceTextView: TextView = itemView.findViewById(R.id.eventPageServiceTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Pager2ViewHandler {
        return Pager2ViewHandler(
            LayoutInflater.from(parent.context).inflate(R.layout.visit_info_page, parent, false)
        )
    }

    override fun onBindViewHolder(holder: Pager2ViewHandler, position: Int) {
        sortVisitsList()
        setVisitsDetails(holder, position)
    }

    private fun sortVisitsList() {
        visitsList.sortBy {
            it.dateOf[it.dateOf.size - 1].date
        }
    }

    override fun getItemCount(): Int {
        return visitsList.size
    }

    @SuppressLint("SetTextI18n")
    private fun setVisitsDetails(holder: Pager2ViewHandler, position: Int) {
        holder.nameTextView.text = visitsList[position].name + " " + visitsList[position].surname
        holder.telephoneTextView.text = visitsList[position].telephone
        holder.serviceTextView.text =
            dateOfVisits.getStringTime(visitsList[position].dateOf[visitsList[position].dateOf.size - 1].date) + " - " + visitsList[position].dateOf[visitsList[position].dateOf.size - 1].service
    }

}