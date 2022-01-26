package pl.dev.beautycalendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pl.dev.beautycalendar.MainActivity.Companion.userName
import pl.dev.beautycalendar.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
    }


    private fun setListeners(){
        binding.monikaButton.setOnClickListener{
            userName = "monika"
            this.finish()
        }
        binding.agataButton.setOnClickListener{
            userName = "agata"
            this.finish()
        }
    }

}