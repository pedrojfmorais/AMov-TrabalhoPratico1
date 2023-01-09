package pt.isec.amov.a2018020733.trabalhopratico1

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import pt.isec.amov.a2018020733.trabalhopratico1.databinding.LevelTransitionBinding
import pt.isec.amov.a2018020733.trabalhopratico1.models.TRANSITION_TIME_SECONDS
import java.util.*
import kotlin.concurrent.fixedRateTimer

class LevelTransitionActivity : AppCompatActivity() {

    lateinit var binding: LevelTransitionBinding
    lateinit var timer: Timer
    var flagPaused = false
    var timeLeftTransition = TRANSITION_TIME_SECONDS + 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LevelTransitionBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (supportActionBar != null) {
            val actionBar: ActionBar? = supportActionBar
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(false)
                actionBar.title = getString(R.string.nextLevel)
            }
        }

        timer = fixedRateTimer("timeTransition", false, 0L, 1 * 1000) {
            this@LevelTransitionActivity.runOnUiThread {
                if (!flagPaused) {
                    --timeLeftTransition
                    binding.tvTimeToNextLevel.text = timeLeftTransition.toString()
                } else
                    binding.tvTimeToNextLevel.text = getString(R.string.paused)
            }

            //Avançar
            if (timeLeftTransition <= 1) {
                cancel()
                passToNextLevel()
            }
        }

        binding.llLayout.setOnClickListener{
            flagPaused = !flagPaused

            if (flagPaused)
                binding.tvTimeToNextLevel.text = getString(R.string.paused)
            else
                binding.tvTimeToNextLevel.text = timeLeftTransition.toString()
        }
    }

    private fun passToNextLevel() {
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onPause() {
        super.onPause()
        flagPaused = true
    }

    override fun onResume() {
        super.onResume()
        flagPaused = false
    }

    override fun onBackPressed() {
        //TODO
    }
}