package pt.isec.amov.a2018020733.trabalhopratico1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pt.isec.amov.a2018020733.trabalhopratico1.databinding.CreditsBinding

class CreditsActivity : AppCompatActivity() {

    lateinit var binding: CreditsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CreditsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}