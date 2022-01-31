package pl.dev.beautycalendar

import android.R
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import pl.dev.beautycalendar.adapter.InfoListAdapter
import pl.dev.beautycalendar.data.CustomerInfo
import pl.dev.beautycalendar.data.VisitsDate
import pl.dev.beautycalendar.databinding.ActivityCustomersListBinding

class CustomersListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomersListBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    val customersList: ArrayList<CustomerInfo> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCustomersListBinding.inflate(layoutInflater)

        setContentView(binding.root)

        checkVisits()
    }

    private fun getCustomerId(customerName: String): String {

        val indexOfFirstSpace = customerName.indexOf(" ")

        return customerName.substring(0, indexOfFirstSpace)
    }

    @SuppressLint("SetTextI18n")
    private fun setView() {
        val customerName = binding.customersListAutoComplete.text.toString()
        val customerId = getCustomerId(customerName)
        val customer = getCustomer(customerId)


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

    private fun getCustomer(customerId: String): CustomerInfo {

        val emptyList: ArrayList<VisitsDate> = ArrayList()
        var customerNewVisit = CustomerInfo(1, emptyList, "", "", "")

        customersList.forEach {
            if (it.telephone == customerId) {
                customerNewVisit = it
            }
        }

        return customerNewVisit

    }

    private fun setAutoCompletedInfo() {
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

        customersList.forEach {
            customersNameList.add("${it.telephone} ${it.name} ${it.surname}")
        }

        return customersNameList
    }

    private fun checkVisits() {
        database = FirebaseDatabase.getInstance()
        reference = database.getReference(MainActivity.userName)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                Log.e("TAG", "Check")

                snapshot.children.forEach { customer ->

                    val listOfItems: ArrayList<Any> = ArrayList()

                    customer.children.forEach {
                        listOfItems.add(it.value!!)
                    }

                    val singleVisitsDate: ArrayList<VisitsDate> = ArrayList()

                    val visitsDateRow = listOfItems[1] as ArrayList<*>

                    visitsDateRow.forEach {
                        val visitsDateRowMap = it as HashMap<*, *>
                        val date = visitsDateRowMap["date"] as Long
                        val service = visitsDateRowMap["service"] as String
                        val visitsDate = VisitsDate(date, service)
                        singleVisitsDate.add(visitsDate)
                    }

                    val singleCustomer = CustomerInfo(
                        listOfItems[0].toString().toInt(),
                        singleVisitsDate,
                        listOfItems[2] as String,
                        listOfItems[3] as String,
                        listOfItems[4] as String,
                    )

                    customersList.add(singleCustomer)
                }
                setAutoCompletedInfo()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }

}