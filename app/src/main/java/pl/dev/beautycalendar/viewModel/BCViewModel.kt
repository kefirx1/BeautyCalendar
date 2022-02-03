package pl.dev.beautycalendar.viewModel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import pl.dev.beautycalendar.data.CustomerInfo
import pl.dev.beautycalendar.database.BCRepository

class BCViewModel(application: Application): AndroidViewModel(application) {

    private var bcRepository = BCRepository()

    fun insertNewVisit(customerInfo: CustomerInfo){

    }
    fun setCustomerList(instance: Activity){
        bcRepository.setCustomersList(instance)
    }

}