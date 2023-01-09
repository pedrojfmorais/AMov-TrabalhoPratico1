package pt.isec.amov.a2018020733.trabalhopratico1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import pt.isec.amov.a2018020733.trabalhopratico1.databinding.LevelTransitionBinding
import pt.isec.amov.a2018020733.trabalhopratico1.models.EXTRA_GAME
import pt.isec.amov.a2018020733.trabalhopratico1.models.Game
import pt.isec.amov.a2018020733.trabalhopratico1.models.TRANSITION_TIME_SECONDS
import java.util.*
import kotlin.concurrent.fixedRateTimer

class LevelTransition : AppCompatActivity() {

    lateinit var binding: LevelTransitionBinding
    lateinit var game: Game
    lateinit var timer: Timer
    var flagPaused = false
    var timeLeftTransition = TRANSITION_TIME_SECONDS + 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LevelTransitionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //TODO: deprecated
        game = (intent.getSerializableExtra(EXTRA_GAME) as? Game)!!

        if (supportActionBar != null) {
            val actionBar: ActionBar? = supportActionBar
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(false)
                actionBar.title = getString(R.string.nextLevel)
            }
        }

        timer = fixedRateTimer("timeTransition", false, 0L, 1 * 1000) {
            this@LevelTransition.runOnUiThread {
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
        game.nextLevel()
        val intent = Intent(this, Singleplayer::class.java)
        intent.putExtra(EXTRA_GAME, game)
        startActivity(intent)
    }
}