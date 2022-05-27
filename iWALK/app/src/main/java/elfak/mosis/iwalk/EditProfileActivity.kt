package elfak.mosis.iwalk

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class EditProfileActivity : AppCompatActivity() {

    private lateinit var cancel : ImageView
    private lateinit var save : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)

        cancel = findViewById(R.id.edit_user_cancel)
        save = findViewById(R.id.edit_user_save)

        cancel.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this, R.style.Theme_PopUpDialog)
            alertDialog.setMessage("Are you sure you want to cancel changes?")

            alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                supportFragmentManager.beginTransaction()
                    .add(android.R.id.content, ProfileFragment()).commit()
            })
            alertDialog.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            alertDialog.show()
        }

        save.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this, R.style.Theme_PopUpDialog)
            alertDialog.setMessage("Are you sure you want to save changes?")

            alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                supportFragmentManager.beginTransaction()
                    .add(android.R.id.content, ProfileFragment()).commit()
            })
            alertDialog.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            alertDialog.show()
        }
    }
}