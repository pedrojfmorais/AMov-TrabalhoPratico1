package pt.isec.amov.a2018020733.trabalhopratico1

import android.graphics.Rect
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import pt.isec.amov.a2018020733.trabalhopratico1.databinding.SingleplayerBinding
import pt.isec.amov.a2018020733.trabalhopratico1.models.DIRECTION_HORIZONTAL
import pt.isec.amov.a2018020733.trabalhopratico1.models.DIRECTION_VERTICAL
import pt.isec.amov.a2018020733.trabalhopratico1.models.Game
import pt.isec.amov.a2018020733.trabalhopratico1.models.NUMBER_EQUATIONS_LEVEL
import kotlin.concurrent.fixedRateTimer
import kotlin.math.abs


class Singleplayer : AppCompatActivity(), GestureDetector.OnGestureListener {

    lateinit var binding: SingleplayerBinding
    var game = Game()

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SingleplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (supportActionBar != null) {
            val actionBar: ActionBar? = supportActionBar
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(false)
                actionBar.title = getString(R.string.singleplayerTitle)
            }
        }
        updateTextViews()
        startTimeLeft()

        binding.btnNext.setOnClickListener {
            if (game.getCurrentEquationNumber() == NUMBER_EQUATIONS_LEVEL)
                game.nextLevel()
            else
                game.nextEquation()

            updateTextViews()
        }
        binding.btnPrevious.setOnClickListener {
            game.previousEquation()
            updateTextViews()
        }
    }

    private fun updateTextViews() {
        val currentEquationBoard = game.getCurrentEquationBoard()

        binding.cell00.text = currentEquationBoard[0][0]
        binding.cell01.text = currentEquationBoard[0][1]
        binding.cell02.text = currentEquationBoard[0][2]
        binding.cell03.text = currentEquationBoard[0][3]
        binding.cell04.text = currentEquationBoard[0][4]


        binding.cell10.text = currentEquationBoard[1][0]
        binding.cell11.text = currentEquationBoard[1][1]
        binding.cell12.text = currentEquationBoard[1][2]
        binding.cell13.text = currentEquationBoard[1][3]
        binding.cell14.text = currentEquationBoard[1][4]


        binding.cell20.text = currentEquationBoard[2][0]
        binding.cell21.text = currentEquationBoard[2][1]
        binding.cell22.text = currentEquationBoard[2][2]
        binding.cell23.text = currentEquationBoard[2][3]
        binding.cell24.text = currentEquationBoard[2][4]


        binding.cell30.text = currentEquationBoard[3][0]
        binding.cell31.text = currentEquationBoard[3][1]
        binding.cell32.text = currentEquationBoard[3][2]
        binding.cell33.text = currentEquationBoard[3][3]
        binding.cell34.text = currentEquationBoard[3][4]


        binding.cell40.text = currentEquationBoard[4][0]
        binding.cell41.text = currentEquationBoard[4][1]
        binding.cell42.text = currentEquationBoard[4][2]
        binding.cell43.text = currentEquationBoard[4][3]
        binding.cell44.text = currentEquationBoard[4][4]

        binding.tvPoints.text = game.getPoints().toString()
        binding.tvTimeLeft.text = game.getTimeLeftLevel().toString()

        binding.tvEquation.text = game.getCurrentEquationNumber().toString()
        binding.tvLevel.text = game.getCurrentLevel().toString()

        binding.btnPrevious.isEnabled = game.getCurrentEquationNumber() != 1

        if (game.getCurrentEquationNumber() == NUMBER_EQUATIONS_LEVEL)
            binding.btnNext.text = getString(R.string.finishLevel)
        else
            binding.btnNext.text = getString(R.string.next)

    }

    private fun verificaConta(direction: Int, indice: Int) {
        game.verifyEquationSelected(direction, indice)
        binding.tvPoints.text = game.getPoints().toString()
    }

    private fun startTimeLeft() {
        fixedRateTimer("timeLeftCounter", false, 0L, 1 * 1000) {
            this@Singleplayer.runOnUiThread {

                game.decrementTimeLeft()
                game.incrementTimePlayed()

                binding.tvTimeLeft.text = game.getTimeLeftLevel().toString()
                binding.tvTimePlayed.text = game.getTimePlayed().toString()
            }
        }
    }

    override fun onBackPressed() {
        //TODO: confirmação sair
        super.onBackPressed()
    }

    // Gestos
    private val gestureDetector: GestureDetector by lazy {
        GestureDetector(applicationContext, this)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (gestureDetector.onTouchEvent(event))
            return true
        return super.onTouchEvent(event)
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        val direction =
            when (abs(e1.x - e2.x) > abs(e1.y - e2.y)) {
                true -> DIRECTION_HORIZONTAL
                false -> DIRECTION_VERTICAL
            }

        val viewBoundsPrimeiraLinhaInicio = Rect()
        findViewById<TextView>(R.id.cell00).getGlobalVisibleRect(viewBoundsPrimeiraLinhaInicio)

        val viewBoundsTerceiraLinhaInicio = Rect()
        findViewById<TextView>(R.id.cell20).getGlobalVisibleRect(viewBoundsTerceiraLinhaInicio)

        val viewBoundsQuintaLinhaInicio = Rect()
        findViewById<TextView>(R.id.cell40).getGlobalVisibleRect(viewBoundsQuintaLinhaInicio)


        val viewBoundsTerceiraColunaInicio = Rect()
        findViewById<TextView>(R.id.cell02).getGlobalVisibleRect(viewBoundsTerceiraColunaInicio)

        val viewBoundsQuintaColunaInicio = Rect()
        findViewById<TextView>(R.id.cell04).getGlobalVisibleRect(viewBoundsQuintaColunaInicio)


        if (
            (viewBoundsPrimeiraLinhaInicio.contains(
                viewBoundsPrimeiraLinhaInicio.centerX(), e1.y.toInt()
            ) && direction == DIRECTION_HORIZONTAL)
            ||
            (viewBoundsPrimeiraLinhaInicio.contains(
                e1.x.toInt(), viewBoundsPrimeiraLinhaInicio.centerY()
            ) && direction == DIRECTION_VERTICAL)
        )
            verificaConta(direction, 0)

        if (
            (viewBoundsTerceiraLinhaInicio.contains(
                viewBoundsTerceiraLinhaInicio.centerX(), e1.y.toInt()
            ) && direction == DIRECTION_HORIZONTAL)
            ||
            (viewBoundsTerceiraColunaInicio.contains(
                e1.x.toInt(), viewBoundsTerceiraColunaInicio.centerY()
            ) && direction == DIRECTION_VERTICAL)
        )
            verificaConta(direction, 2)

        if (
            (viewBoundsQuintaLinhaInicio.contains(
                viewBoundsQuintaLinhaInicio.centerX(), e1.y.toInt()
            ) && direction == DIRECTION_HORIZONTAL)
            ||
            (viewBoundsQuintaColunaInicio.contains(
                e1.x.toInt(), viewBoundsQuintaColunaInicio.centerY()
            ) && direction == DIRECTION_VERTICAL)
        )
            verificaConta(direction, 4)

        return false
    }

    override fun onDown(e: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent) {

    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent) {

    }
}