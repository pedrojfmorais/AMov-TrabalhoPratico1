package pt.isec.amov.a2018020733.trabalhopratico1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import pt.isec.amov.a2018020733.trabalhopratico1.databinding.RegisterBinding
import pt.isec.amov.a2018020733.trabalhopratico1.models.getTempFilename
import pt.isec.amov.a2018020733.trabalhopratico1.models.setPic
import java.io.File

class RegisterActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 1234
    }

    lateinit var binding: RegisterBinding
    private var imagePath: String? = null

    private val strEmail
        get() = binding.edEmail.text.toString()

    private val strPass
        get() = binding.edPassword.text.toString()

    private val strPassConfirm
        get() = binding.edPasswordConfirm.text.toString()

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = RegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnChangePhoto.setOnClickListener { takePhoto() }

        auth = Firebase.auth

        binding.btnRegister.setOnClickListener {
            createUserWithEmail()
        }

        verifyPermissions()
        updatePreview()

    }

    private var permissionsGranted = false

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grantResults ->
        permissionsGranted = grantResults.values.all { it }
    }

    private fun verifyPermissions() {

        requestPermissionsLauncher.launch(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == PERMISSIONS_REQUEST_CODE) {

            permissionsGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }

            return
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun updatePreview() {
        if (imagePath != null)
            setPic(binding.frPreview, imagePath!!)
        else
            binding.frPreview.background = ResourcesCompat.getDrawable(
                resources,
                android.R.drawable.ic_menu_report_image, null
            )
    }

    var startActivityForTakePhotoResult = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (!success)
            imagePath = null
        updatePreview()
    }

    private fun takePhoto() {
        imagePath = getTempFilename(this)
        startActivityForTakePhotoResult.launch(
            FileProvider.getUriForFile(
                this,
                "pt.isec.amov.a2018020733.trabalhopratico1.android.fileprovider",
                File(imagePath)
            )
        )
    }

    fun createUserWithEmail() {
        binding.tvError.text = ""

        if (strEmail.isBlank() || strPass.isBlank())
            return

        if (strPass != strPassConfirm) {
            binding.tvError.text = getString(R.string.errorDiferentPasswords)
            return
        }

        if (strPass.length < 6) {
            binding.tvError.text = getString(R.string.passwordLength)
            return
        }

        auth.createUserWithEmailAndPassword(strEmail, strPass)
            .addOnSuccessListener(this) {
                showUser(auth.currentUser)
                updateImageFirebase()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener(this) {
                showUser(null)
            }
    }

    fun updateImageFirebase() {
        FirebaseAuth.getInstance().signInAnonymously()

        val imageUri = FileProvider.getUriForFile(
            this,
            "pt.isec.amov.a2018020733.trabalhopratico1.android.fileprovider",
            imagePath?.let { File(it) }!!
        )

        val ref = FirebaseStorage.getInstance().getReference("/users/$strEmail")

        ref.putFile(imageUri!!).addOnSuccessListener {
            Log.i(
                "PerfilActivity",
                "Colocada com sucesso na FireBaseStorage a imagem: ${it.metadata?.path}"
            )

            ref.downloadUrl.addOnSuccessListener { url ->
                Log.i("PerfilActivity", "Localizaçao da imagem: $url")

                val db = Firebase.firestore
                val utilizador = db.collection("Utilizadores").document(strEmail)

                utilizador.get(Source.SERVER)
                    .addOnSuccessListener {
                        utilizador.update("urlImagem", url.toString())
                    }
            }
        }
    }

    fun showUser(user: FirebaseUser? = auth.currentUser) {
        val str = when (user) {
            null -> getString(R.string.errorCreatingUser)
            else -> getString(R.string.userRegisteredSuccessfully)
        }
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}

