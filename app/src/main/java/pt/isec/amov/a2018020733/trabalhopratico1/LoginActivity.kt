package pt.isec.amov.a2018020733.trabalhopratico1

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import pt.isec.amov.a2018020733.trabalhopratico1.databinding.LoginBinding


class LoginActivity : AppCompatActivity() {

    lateinit var binding: LoginBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient


    private val strEmail
        get() = binding.edEmail.text.toString()

    private val strPass
        get() = binding.edPassword.text.toString()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            signInWithEmail()
        }

        binding.btnCreateAccount.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnAccountGoogle.setOnClickListener {
            signInWithGoogle.launch(googleSignInClient.signInIntent)
        }

        auth = Firebase.auth

        if (auth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser != null)
            showUser(auth.currentUser)
    }

    fun signInWithEmail() {

        if (strEmail.isBlank() || strPass.isBlank())
            return

        auth.signInWithEmailAndPassword(strEmail, strPass)
            .addOnSuccessListener(this) { result ->
                showUser(auth.currentUser)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener(this) { e ->
                showUser(null)
            }
    }

    fun showUser(user: FirebaseUser? = auth.currentUser) {
        val str = when (user) {
            null -> getString(R.string.errorLogin)
            else -> getString(R.string.welcome) + " " + auth.currentUser!!.email
        }
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }

    val signInWithGoogle = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (_: ApiException) {
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener(this) {
                showUser(auth.currentUser)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener(this) {
                showUser(auth.currentUser)
            }
    }
}