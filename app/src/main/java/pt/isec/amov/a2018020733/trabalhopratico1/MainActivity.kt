package pt.isec.amov.a2018020733.trabalhopratico1

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import pt.isec.amov.a2018020733.trabalhopratico1.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: MainActivityBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    lateinit var clickado : String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (supportActionBar != null) {
            val actionBar: ActionBar? = supportActionBar
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(false)
            }
        }

        binding.btnLogout.setOnClickListener {
            signOut()
        }

        binding.btnSingleplayer.setOnClickListener{
            val intent = Intent(this, Singleplayer::class.java)
            startActivity(intent)
        }

        auth = Firebase.auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    fun signOut() {
        if (auth.currentUser != null) {
            googleSignInClient.signOut()
            auth.signOut()
        }

        showUser(auth.currentUser)

        if(auth.currentUser == null) {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }

    fun showUser(user : FirebaseUser? = auth.currentUser) {
        val str = when (user) {
            null -> getString(R.string.logout_successful)
            else -> getString(R.string.error_logging_out)
        }
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, EditUser::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}