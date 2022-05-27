package elfak.mosis.iwalk

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.actionCodeSettings
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var alreadyHaveAccount : TextView
    private lateinit var email : TextInputEditText
    private lateinit var password : TextInputEditText
    private lateinit var confirmPassword : TextInputEditText
    private lateinit var register : Button
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = Firebase.auth

        email = findViewById(R.id.emailInput)
        password = findViewById(R.id.passwordInput)
        confirmPassword = findViewById(R.id.confirmPasswordInput)

        alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount);
        register = findViewById(R.id.buttonRegisterAct);

        alreadyHaveAccount.setOnClickListener {
            val i: Intent = Intent(this, LoginActivity::class.java)
            startActivity(i)
        }

       register.setOnClickListener {

           if (password.text.toString().equals(confirmPassword.text.toString()) && password.text.toString() != "") {

               auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                   .addOnCompleteListener(this) { task ->
                       if (task.isSuccessful) {

                           val user = auth.currentUser

                           user!!.sendEmailVerification()
                               .addOnCompleteListener(this) { task ->
                                   Log.d(ContentValues.TAG, "Email sent.")

                                   Toast.makeText(
                                       this@RegisterActivity,
                                       "Account created, Verification mail sent!",
                                       Toast.LENGTH_LONG
                                   ).show()
                               }

                           startActivity(Intent(applicationContext, MainActivity::class.java))

                       } else {
                           // If sign in fails, display a message to the user.
                           Toast.makeText(
                               this@RegisterActivity,
                               "Sign in failed!",
                               Toast.LENGTH_SHORT
                           ).show()
                           //todo nav to page
                       }
                   }
           }
           else{
               confirmPassword.error = "Password values need to have same value"
           }
       }
    }
}