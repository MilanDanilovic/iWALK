package elfak.mosis.iwalk

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class EditPetActivity : AppCompatActivity() {

    private lateinit var cancel : Button
    private lateinit var edit : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editpet)

        cancel = findViewById(R.id.button_edit_pet)
        edit = findViewById(R.id.button_cancel_edit_pet)

        cancel.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this, R.style.Theme_PopUpDialog)
            alertDialog.setMessage("Are you sure you want to cancel changes?")

            alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                val i: Intent = Intent(this, HomeActivity::class.java)
                startActivity(i)
            })
            alertDialog.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            alertDialog.show()
        }

        edit.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this, R.style.Theme_PopUpDialog)
            alertDialog.setMessage("Are you sure you want to save changes?")

            alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                val i: Intent = Intent(this, HomeActivity::class.java)
                startActivity(i)
            })
            alertDialog.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            alertDialog.show()
        }
    }
}