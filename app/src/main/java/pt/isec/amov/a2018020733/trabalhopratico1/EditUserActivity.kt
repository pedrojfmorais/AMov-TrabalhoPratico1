package pt.isec.amov.a2018020733.trabalhopratico1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import pt.isec.amov.a2018020733.trabalhopratico1.databinding.EditUserBinding
import pt.isec.amov.a2018020733.trabalhopratico1.models.getTempFilename
import pt.isec.amov.a2018020733.trabalhopratico1.models.setPic
import java.io.File

class EditUserActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 12345
    }

    lateinit var binding: EditUserBinding
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
        binding = EditUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.edEmail.setText(auth.currentUser!!.email)

        binding.btnChangePhoto.setOnClickListener { takePhoto() }

        auth = Firebase.auth

        binding.btnEdit.setOnClickListener {
            updateUserWithEmail()
        }
        if (supportActionBar != null) {
            val actionBar: ActionBar? = supportActionBar
            if (actionBar != null) {
                actionBar.title = getString(R.string.edit_user_title);
            }
        }

        verify_permissions()
        updatePreview()
        getImageFirebase()
    }

    private var permissionsGranted = false

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grantResults ->
        permissionsGranted = grantResults.values.all { it }
    }

    private fun verify_permissions() {

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

    fun updateUserWithEmail() {
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

        auth.currentUser!!.updateEmail(strEmail)
            .addOnSuccessListener(this) {
                auth.currentUser!!.updatePassword(strPass)
                    .addOnSuccessListener {
                        updateImageFirebase()
                        showResult(true)
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener(this) {
                        showResult(false)
                    }
            }
            .addOnFailureListener(this) {
                showResult(false)
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

    fun showResult(good: Boolean) {
        val str = when (good) {
            false -> getString(R.string.error_edit_user)
            true -> getString(R.string.user_update_successful)
        }
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }

    fun getImageFirebase() {
        FirebaseAuth.getInstance().signInAnonymously()

        val imageFile = File.createTempFile(
            "image", ".img",
            this.externalCacheDir
        )

        imagePath = imageFile.absolutePath

        FirebaseStorage.getInstance().reference.child("/users/$strEmail")
            .getFile(imageFile).addOnSuccessListener {
                setPic(binding.frPreview, imagePath!!)
            }
    }
}
