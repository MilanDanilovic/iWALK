package elfak.mosis.iwalk

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var login : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login = findViewById(R.id.buttonLoginAct);

        login.setOnClickListener {
            val i: Intent = Intent(this, HomeActivity::class.java)
            startActivity(i)
        }
    }
}