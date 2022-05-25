package elfak.mosis.iwalk

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var alreadyHaveAccount : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        alreadyHaveAccount = findViewById(R.id.buttonLogin);

        alreadyHaveAccount.setOnClickListener {
            val i: Intent = Intent(this, LoginActivity::class.java)
            startActivity(i)
        }
    }
}