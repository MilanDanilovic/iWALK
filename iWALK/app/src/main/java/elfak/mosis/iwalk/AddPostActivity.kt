package elfak.mosis.iwalk

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class AddPostActivity : AppCompatActivity() {

    private lateinit var cancel : Button
    private lateinit var save : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addpost)

        cancel = findViewById(R.id.button_cancel_post)
        save = findViewById(R.id.button_add_post)

        cancel.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this, R.style.Theme_PopUpDialog)
            alertDialog.setMessage("Are you sure you want to cancel post?")

            alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                val i: Intent = Intent(this, HomeActivity::class.java)
                startActivity(i)
                })
            alertDialog.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            alertDialog.show()
        }

        save.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this, R.style.Theme_PopUpDialog)
            alertDialog.setMessage("Are you sure you want to save post?")

            alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                val i: Intent = Intent(this, HomeActivity::class.java)
                startActivity(i)
            })
            alertDialog.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            alertDialog.show()
        }
    }
}