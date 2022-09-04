package elfak.mosis.iwalk

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var alreadyHaveAccount : TextView
    private lateinit var email : TextInputEditText
    private lateinit var password : TextInputEditText
    private lateinit var confirmPassword : TextInputEditText
    private lateinit var username : TextInputEditText
    private lateinit var name : TextInputEditText
    private lateinit var surname : TextInputEditText
    private lateinit var register : Button
    private lateinit var auth: FirebaseAuth
    private val docRef = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = Firebase.auth
        val db = Firebase.firestore


        email = findViewById(R.id.emailInput)
        password = findViewById(R.id.passwordInput)
        confirmPassword = findViewById(R.id.confirmPasswordInput)
        username = findViewById(R.id.usernameInput)
        name = findViewById(R.id.nameInput)
        surname = findViewById(R.id.surnameInput)

        alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount)
        register = findViewById(R.id.buttonRegisterAct)

        alreadyHaveAccount.setOnClickListener {
            val i: Intent = Intent(this, LoginActivity::class.java)
            startActivity(i)
        }

       register.setOnClickListener {

           if (password.text.toString() == confirmPassword.text.toString() && password.text.toString() != "") {

               val userName: String = username.text.toString()
               val usersRef: CollectionReference = db.collection("users")
               val query = usersRef.whereEqualTo("username", userName)

               query.get().addOnCompleteListener { task ->
                   if (task.isSuccessful) {
                       for (document in task.result) {
                           val pom = document.getString("username")
                           if (pom == userName) {
                               Log.d("TAG", "user exist")
                           }
                       }
                   }
                   if (task.result.size() == 0) {
                       Log.d("TAG", "User not exist")
                       auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                           .addOnCompleteListener(this) { task ->
                                   if (task.isSuccessful) {
                                       val nameVal: String =
                                           name.text.toString()
                                       val surnameVal: String =
                                           surname.text.toString()
                                       val usernameVal: String =
                                           username.text.toString()
                                       val emailVal = email.text.toString()
                                       val dataToSave: MutableMap<String, Any> =
                                           HashMap()
                                       dataToSave["name"] = nameVal
                                       dataToSave["surname"] = surnameVal
                                       dataToSave["username"] = usernameVal
                                       dataToSave["email"] = emailVal
                                       dataToSave["profileImageUrl"] = "default"
                                       dataToSave["numberOfWalks"] = 0
                                       dataToSave["score"] = 0
                                       dataToSave["phone"] = "Number is not entered"
                                       db.collection("users")
                                           .document(auth.currentUser!!.uid)
                                           .set(dataToSave).addOnSuccessListener(
                                               OnSuccessListener<Void?> {
                                                   Log.d(
                                                       "TAG",
                                                       "User is saved in database! "
                                                   )
                                                   auth.currentUser!!.sendEmailVerification()
                                                       .addOnCompleteListener(this) {
                                                           Log.d(ContentValues.TAG, "Email sent.")

                                                           Toast.makeText(
                                                               this@RegisterActivity,
                                                               "Account created, Verification mail sent!",
                                                               Toast.LENGTH_LONG
                                                           ).show()

                                                           startActivity(Intent(applicationContext, MainActivity::class.java))

                                                       }
                                               }).addOnFailureListener { e ->
                                                        Log.w("TAG", "User is not saved in database! ", e)
                                           }
                                   } else {
                                       Toast.makeText(
                                           this@RegisterActivity,
                                           "Sign in failed!",
                                           Toast.LENGTH_SHORT
                                       ).show()
                                   }
                               }
                   } else {
                       username.error = "Username already exists!"
                   }
               }
           }
           else{
               confirmPassword.error = "Password values need to have same value"
           }
       }
    }
}