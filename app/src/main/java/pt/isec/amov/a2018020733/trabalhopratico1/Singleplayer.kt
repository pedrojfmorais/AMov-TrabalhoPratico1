package pt.isec.amov.a2018020733.trabalhopratico1

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.contains
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import pt.isec.amov.a2018020733.trabalhopratico1.databinding.SingleplayerBinding
import kotlin.math.abs


class Singleplayer : AppCompatActivity(), GestureDetector.OnGestureListener {

    lateinit var binding: SingleplayerBinding

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

    }

    private fun verificaConta(direction: Int, indice: Int) {



        Log.i("TAG", "$direction $indice")
    }

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
                true -> 1 // horizontal
                false -> 2 // vertical
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
            ) && direction == 1)
            ||
            (viewBoundsPrimeiraLinhaInicio.contains(
                e1.x.toInt(), viewBoundsPrimeiraLinhaInicio.centerY()
            ) && direction == 2)
        )
            verificaConta(direction, 0)

        if (
            (viewBoundsTerceiraLinhaInicio.contains(
                viewBoundsTerceiraLinhaInicio.centerX(), e1.y.toInt()
            ) && direction == 1)
            ||
            (viewBoundsTerceiraColunaInicio.contains(
                e1.x.toInt(), viewBoundsTerceiraColunaInicio.centerY()
            ) && direction == 2)
        )
            verificaConta(direction, 2)

        if (
            (viewBoundsQuintaLinhaInicio.contains(
                viewBoundsQuintaLinhaInicio.centerX(), e1.y.toInt()
            ) && direction == 1)
            ||
            (viewBoundsQuintaColunaInicio.contains(
                e1.x.toInt(), viewBoundsQuintaColunaInicio.centerY()
            ) && direction == 2)
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