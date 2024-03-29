package pt.isec.amov.a2018020733.trabalhopratico1

import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pt.isec.amov.a2018020733.trabalhopratico1.databinding.SingleplayerBinding
import pt.isec.amov.a2018020733.trabalhopratico1.models.*
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.math.abs


class SingleplayerActivity : AppCompatActivity(), GestureDetector.OnGestureListener {

    lateinit var binding: SingleplayerBinding
    private val game: Game by viewModels()
    private lateinit var auth: FirebaseAuth
    lateinit var timer: Timer
    var tempoBack = 0

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

        binding.btnNext.setOnClickListener {
            if (game.getCurrentEquationNumber() == NUMBER_EQUATIONS_LEVEL) {
                val intent = Intent(this, LevelTransitionActivity::class.java)
                startActivityForResult(intent, 1)

                timer.cancel()
            } else
                game.nextEquation()

            updateTextViews()
        }
        binding.btnPrevious.setOnClickListener {
            game.previousEquation()
            updateTextViews()
        }
    }

    override fun onPause() {
        super.onPause()
        timer.cancel()
    }

    override fun onResume() {
        super.onResume()
        startTimeLeft()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Collect data from the intent and use it
        game.nextLevel()
        updateTextViews()
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun acabarJogo() {
        auth = Firebase.auth

        val db = Firebase.firestore
        val v = auth.currentUser?.email?.let { db.collection(COLLECTION_PATH).document(it) }
        db.runTransaction { transaction ->
            val doc = v?.let { transaction.get(it) }
            if (doc?.exists() == true) {
                val storedPoints = (doc.getLong(COLLECTION_FIELD_POINTS) ?: 0)

                if (game.getPoints() > storedPoints) {
                    transaction.update(v, COLLECTION_FIELD_POINTS, game.getPoints())
                    transaction.update(v, COLLECTION_FIELD_TIME_PLAYED, game.getTimePlayed())
                }
                null
            } else {
                val scores = hashMapOf(
                    COLLECTION_FIELD_POINTS to game.getPoints(),
                    COLLECTION_FIELD_TIME_PLAYED to game.getTimePlayed()
                )
                auth.currentUser?.email?.let {
                    db.collection(COLLECTION_PATH).document(it).set(scores)
                        .addOnSuccessListener {
                            Log.i("TAG", "addDataToFirestore: Success")
                        }
                        .addOnFailureListener { e ->
                            Log.i("TAG", "addDataToFirestore: ${e.message}")
                        }
                }
            }
        }.addOnSuccessListener {
            Log.i("TAG", "updateDataInFirestoreTrans: Success")
        }.addOnFailureListener { e ->
            Log.i("TAG", "updateDataInFirestoreTrans: ${e.message}")
        }
        finish()
    }

    private fun updateTextViews() {
        val gameBoard = game.getCurrentBoard()
        val currentEquationBoard = gameBoard.gameBoard

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

        binding.btnNext.isEnabled =
            game.getCurrentBoard().maxValueFound || game.getCurrentBoard().secondMaxValueFound

        if (game.getCurrentEquationNumber() == NUMBER_EQUATIONS_LEVEL)
            binding.btnNext.text = getString(R.string.finishLevel)
        else
            binding.btnNext.text = getString(R.string.next)

        //CORES

        //RESETAR
        aplicaCor(DIRECTION_HORIZONTAL, 0, Color.BLACK)
        aplicaCor(DIRECTION_HORIZONTAL, 2, Color.BLACK)
        aplicaCor(DIRECTION_HORIZONTAL, 4, Color.BLACK)

        aplicaCor(DIRECTION_VERTICAL, 0, Color.BLACK)
        aplicaCor(DIRECTION_VERTICAL, 2, Color.BLACK)
        aplicaCor(DIRECTION_VERTICAL, 4, Color.BLACK)

        // APLICAR SE FOI ENCONTRADO
        if (gameBoard.secondMaxValueFound)
            aplicaCor(
                gameBoard.secondMaxValuePosition.first,
                gameBoard.secondMaxValuePosition.second,
                Color.rgb(255, 191, 0)
            )

        if (gameBoard.maxValueFound)
            aplicaCor(
                gameBoard.maxValuePosition.first,
                gameBoard.maxValuePosition.second,
                Color.GREEN
            )
    }

    private fun aplicaCor(direcao: Int, indice: Int, color: Int) {

        when (indice) {
            0 -> {
                when (direcao) {
                    DIRECTION_HORIZONTAL -> {
                        binding.cell00.setTextColor(color)
                        binding.cell01.setTextColor(color)
                        binding.cell02.setTextColor(color)
                        binding.cell03.setTextColor(color)
                        binding.cell04.setTextColor(color)
                    }
                    DIRECTION_VERTICAL -> {
                        binding.cell00.setTextColor(color)
                        binding.cell10.setTextColor(color)
                        binding.cell20.setTextColor(color)
                        binding.cell30.setTextColor(color)
                        binding.cell40.setTextColor(color)
                    }
                }
            }
            2 -> {
                when (direcao) {
                    DIRECTION_HORIZONTAL -> {
                        binding.cell20.setTextColor(color)
                        binding.cell21.setTextColor(color)
                        binding.cell22.setTextColor(color)
                        binding.cell23.setTextColor(color)
                        binding.cell24.setTextColor(color)
                    }
                    DIRECTION_VERTICAL -> {
                        binding.cell02.setTextColor(color)
                        binding.cell12.setTextColor(color)
                        binding.cell22.setTextColor(color)
                        binding.cell32.setTextColor(color)
                        binding.cell42.setTextColor(color)
                    }
                }
            }
            4 -> {
                when (direcao) {
                    DIRECTION_HORIZONTAL -> {
                        binding.cell40.setTextColor(color)
                        binding.cell41.setTextColor(color)
                        binding.cell42.setTextColor(color)
                        binding.cell43.setTextColor(color)
                        binding.cell44.setTextColor(color)
                    }
                    DIRECTION_VERTICAL -> {
                        binding.cell04.setTextColor(color)
                        binding.cell14.setTextColor(color)
                        binding.cell24.setTextColor(color)
                        binding.cell34.setTextColor(color)
                        binding.cell44.setTextColor(color)
                    }
                }
            }
        }
    }

    private fun verificaConta(direction: Int, indice: Int) {
        game.verifyEquationSelected(direction, indice)
        binding.tvPoints.text = game.getPoints().toString()
        updateTextViews()
    }

    private fun startTimeLeft() {
        timer = fixedRateTimer("timeLeftCounter", false, 0L, 1 * 1000) {
            this@SingleplayerActivity.runOnUiThread {
                tempoBack--
                game.decrementTimeLeft()
                game.incrementTimePlayed()

                binding.tvTimeLeft.text = game.getTimeLeftLevel().toString()
                binding.tvTimePlayed.text = game.getTimePlayed().toString()
            }

            //Parar a cena
            if (game.getTimeLeftLevel() <= 0) {
                acabarJogo()
                cancel()
            }
        }
    }

    override fun onBackPressed() {
        if (tempoBack <= 0) {
            tempoBack = 5
            Toast.makeText(
                applicationContext,
                getString(R.string.pressBackExit),
                Toast.LENGTH_SHORT
            ).show()
        } else
            acabarJogo()
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