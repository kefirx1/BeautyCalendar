package pl.dev.beautycalendar.database

import android.app.Activity
import com.google.gson.Gson
import pl.dev.beautycalendar.MainActivity
import pl.dev.beautycalendar.json.GetJSONString

class BCRepository() {



    private val firebaseData = FirebaseData()


    fun setCustomersList(instance: Activity){
        if(MainActivity.isOnline){
            firebaseData.getListOfVisits(instance)
        }else{
            println(MainActivity.bcDatabaseJSON)
        }
    }

}