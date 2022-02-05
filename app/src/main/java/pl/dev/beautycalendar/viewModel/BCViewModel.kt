package pl.dev.beautycalendar.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.database.DataSnapshot
import pl.dev.beautycalendar.MainActivity
import pl.dev.beautycalendar.data.CustomerInfo
import pl.dev.beautycalendar.database.BCRepository

class BCViewModel(application: Application): AndroidViewModel(application) {

    private var bcRepository = BCRepository()

    fun insertNewVisit(customerInfo: CustomerInfo){
        bcRepository.insertCustomer(customerInfo)
    }

    fun setCustomerList(snapshot: DataSnapshot){
        bcRepository.setCustomersList(snapshot)
    }

}