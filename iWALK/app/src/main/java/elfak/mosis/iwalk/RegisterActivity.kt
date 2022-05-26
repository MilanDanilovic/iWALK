package elfak.mosis.iwalk

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var alreadyHaveAccount : TextView
    private lateinit var register : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount);
        register = findViewById(R.id.buttonRegisterAct);

        alreadyHaveAccount.setOnClickListener {
            val i: Intent = Intent(this, LoginActivity::class.java)
            startActivity(i)
        }

       register.setOnClickListener {
            val i: Intent = Intent(this, HomeActivity::class.java)
            startActivity(i)
        }
    }
}