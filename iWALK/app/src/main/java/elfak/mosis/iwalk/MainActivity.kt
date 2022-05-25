package elfak.mosis.iwalk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var login : Button
    private lateinit var register : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        login = findViewById(R.id.buttonLogin);
        register = findViewById(R.id.buttonRegister);

        login.setOnClickListener {
            val i: Intent = Intent(this, LoginActivity::class.java)
            startActivity(i)
        }
        register.setOnClickListener {
            val i: Intent = Intent(this, RegisterActivity::class.java)
            startActivity(i)
        }
    }
}