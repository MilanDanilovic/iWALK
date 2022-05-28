package elfak.mosis.iwalk

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class AddPetActivity : AppCompatActivity() {

    private lateinit var cancel : Button
    private lateinit var save : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addpet)

        cancel = findViewById(R.id.button_add_pet)
        save = findViewById(R.id.button_cancel_add_pet)

        cancel.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this, R.style.Theme_PopUpDialog)
            alertDialog.setMessage("Are you sure you want to cancel adding a new pet?")

            alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                val i: Intent = Intent(this, HomeActivity::class.java)
                startActivity(i)
            })
            alertDialog.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            alertDialog.show()
        }

        save.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this, R.style.Theme_PopUpDialog)
            alertDialog.setMessage("Are you sure you want to save new pet?")

            alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                val i: Intent = Intent(this, HomeActivity::class.java)
                startActivity(i)
            })
            alertDialog.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            alertDialog.show()
        }
    }
}