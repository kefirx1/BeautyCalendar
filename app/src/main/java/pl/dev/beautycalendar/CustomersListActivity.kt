package pl.dev.beautycalendar

import android.R
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import pl.dev.beautycalendar.adapter.InfoListAdapter
import pl.dev.beautycalendar.classes.Customer
import pl.dev.beautycalendar.classes.FirebaseData
import pl.dev.beautycalendar.data.CustomerInfo
import pl.dev.beautycalendar.data.VisitsDate
import pl.dev.beautycalendar.databinding.ActivityCustomersListBinding

class CustomersListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomersListBinding
    private lateinit var firebaseData: FirebaseData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCustomersListBinding.inflate(layoutInflater)

        setContentView(binding.root)
        firebaseData = FirebaseData()

        firebaseData.getListOfVisits(this)

    }

    @SuppressLint("SetTextI18n")
    private fun setView() {
        val customerName = binding.customersListAutoComplete.text.toString()
        val customerId = Customer.getCustomerId(customerName)
        val customer = Customer.getCustomer(customerId)


        binding.customersListName.text = customer.name + " " + customer.surname
        binding.customersListTelephone.text = customer.telephone

        val visitsHistoryList = customer.dateOf

        val adapter = InfoListAdapter(visitsHistoryList)

        binding.customersListRecyclerView.adapter = adapter
        binding.customersListRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.callCustomerButton.setOnClickListener {
            callToCustomer(customer)
        }
        binding.messageCustomerButton.setOnClickListener {
            messageToCustomer(customer)
        }

    }

    private fun messageToCustomer(customer: CustomerInfo) {
        val messageIntent = Intent(Intent.ACTION_VIEW)
        messageIntent.type = "vnd.android-dir/mms-sms"
        messageIntent.putExtra("address", customer.telephone)
        startActivity(messageIntent)
    }

    private fun callToCustomer(customer: CustomerInfo) {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:" + customer.telephone)
        startActivity(dialIntent)
    }

    fun setAutoCompletedInfo() {
        val customersNameList = getCustomersNameList()
        val adapter =
            ArrayAdapter(applicationContext, R.layout.simple_list_item_1, customersNameList)
        binding.customersListAutoComplete.setAdapter(adapter)

        binding.customersListAutoComplete.doAfterTextChanged {
            if (it != null) {
                if (it.length > 9) {
                    binding.customersListInfoLayout.visibility = View.VISIBLE
                    setView()
                } else {
                    binding.customersListInfoLayout.visibility = View.GONE
                }
            } else {
                binding.customersListInfoLayout.visibility = View.GONE
            }
        }
    }

    private fun getCustomersNameList(): ArrayList<String> {

        val customersNameList: ArrayList<String> = ArrayList()

        MainActivity.customersList.forEach {
            customersNameList.add("${it.telephone} ${it.name} ${it.surname}")
        }

        return customersNameList
    }



}