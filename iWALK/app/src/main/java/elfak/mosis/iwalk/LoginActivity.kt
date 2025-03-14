package elfak.mosis.iwalk

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import elfak.mosis.iwalk.services.helpers.ServiceHelper

class LoginActivity : AppCompatActivity() {

    private lateinit var login : Button
    private lateinit var auth: FirebaseAuth
    private lateinit var email : TextInputEditText
    private lateinit var password : TextInputEditText
    private lateinit var forgottenPassword : TextView
    private val docRef = FirebaseFirestore.getInstance()
    private val permissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        var areGranted = true
        for(permission in permissions)
            areGranted = areGranted && permission.value

        if(areGranted){
            ServiceHelper.startLocationService(this, this@LoginActivity)
        }
        startActivity(Intent(applicationContext, HomeActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login = findViewById(R.id.buttonLoginAct)
        email = findViewById(R.id.emailInput)
        password = findViewById(R.id.passwordInput)
        forgottenPassword = findViewById(R.id.forgottenPass)
        auth = Firebase.auth

        login.setOnClickListener {

                val userMail = email.text.toString().trim { it <= ' ' }
                val pass = password.text.toString().trim { it <= ' ' }
                if (TextUtils.isEmpty(userMail)) {
                    email.error = "Email field can not be empty!"
                    return@setOnClickListener
                }
                if (TextUtils.isEmpty(pass)) {
                    password.error = "Password field can not be empty"
                    return@setOnClickListener
                }
                if (pass.length < 6) {
                    password.error = "Your password needs to have at least 6 characters!"
                    return@setOnClickListener
                }
                auth.signInWithEmailAndPassword(userMail, pass)
                    .addOnCompleteListener(OnCompleteListener<AuthResult?> { task ->
                        if (task.isSuccessful) {
                            if(auth.currentUser!!.isEmailVerified) {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Succesful login!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startBackgroundService()
                            } else{

                                auth.currentUser!!.sendEmailVerification()
                                    .addOnCompleteListener(this) { task ->
                                        Log.d(ContentValues.TAG, "Email sent.")

                                        Toast.makeText(
                                            this@LoginActivity,
                                            "User email address must be verified, Verification mail sent!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                            }
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "Error during login!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })

        }

        forgottenPassword.setOnClickListener{
                val resetMail = EditText(this)
                val passwordResetDialog = AlertDialog.Builder(this)
                passwordResetDialog.setTitle("Do you want to reset your password?")
                passwordResetDialog.setMessage("Enter your email address to get a recovery link.")
                passwordResetDialog.setView(resetMail)
                passwordResetDialog.setPositiveButton(
                    "Accept"
                ) { _, _ ->
                    val mail: String = resetMail.text.toString()
                    auth.sendPasswordResetEmail(mail)
                        .addOnSuccessListener(OnSuccessListener<Void?> {
                            Toast.makeText(
                                this@LoginActivity,
                                "Recovery link is sent to your email!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }).addOnFailureListener(
                            OnFailureListener {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Error! Recovery link is not sent!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            })
                }
                passwordResetDialog.setNegativeButton(
                    "Cancel"
                ) { _, _ -> }
                passwordResetDialog.create().show()

        }
    }

    private fun checkPermission(): Boolean {
        val resultFineLocation = ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)
        val resultCoarseLocation = ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION)
        return resultFineLocation == PackageManager.PERMISSION_GRANTED && resultCoarseLocation == PackageManager.PERMISSION_GRANTED
    }
    private fun startBackgroundService() {
        if(!checkPermission()) {
            permissions.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION))
        } else {
            ServiceHelper.startLocationService(this, this@LoginActivity)
            startActivity(Intent(applicationContext, HomeActivity::class.java))
        }
    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            if(currentUser.isEmailVerified && !currentUser.isAnonymous) {
                startBackgroundService()
            } else{
                Toast.makeText(
                    this@LoginActivity,
                    "User email address must be verified!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

}