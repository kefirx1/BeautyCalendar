package pl.dev.beautycalendar.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import pl.dev.beautycalendar.MainActivity
import pl.dev.beautycalendar.R
import pl.dev.beautycalendar.classes.DashboardDialog
import pl.dev.beautycalendar.classes.DateTimeConverter
import pl.dev.beautycalendar.data.CustomerInfo

class EventsAdapter(private val visitsList: ArrayList<CustomerInfo>, applicationContext: Context, private val instance: MainActivity): RecyclerView.Adapter<EventsAdapter.ViewHandler>() {

    private val dashboardDialog = DashboardDialog(visitsList, applicationContext, instance)

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
        holder.rowLayout.setOnLongClickListener {

            dashboardDialog.showDialog(holder, position)

            true
        }
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
    private fun setExamDetails(holder: ViewHandler, position: Int) {
        holder.dateTextView.text =
            DateTimeConverter.getStringTime(visitsList[position].dateOf[visitsList[position].dateOf.size - 1].date) + " - " + visitsList[position].dateOf[visitsList[position].dateOf.size - 1].service
        holder.nameTextView.text =
            visitsList[position].name + " " + visitsList[position].surname
    }

}