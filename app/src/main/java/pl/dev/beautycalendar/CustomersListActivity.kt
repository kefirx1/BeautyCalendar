package pl.dev.beautycalendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pl.dev.beautycalendar.databinding.ActivityCustomersListBinding

class CustomersListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomersListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCustomersListBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}