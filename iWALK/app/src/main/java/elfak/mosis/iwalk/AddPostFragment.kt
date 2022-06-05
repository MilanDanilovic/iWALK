package elfak.mosis.iwalk

import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.*

class AddPostFragment : Fragment() {

    private lateinit var dogImage1 : ImageView
    private lateinit var dogImage2 : ImageView
    private lateinit var description : EditText
    private lateinit var date : DatePicker
    private lateinit var time : EditText
    private lateinit var auth: FirebaseAuth
    private val docRef = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        dogImage1 = requireView().findViewById<ImageView>(R.id.dogPictureOne)
        dogImage2 = requireView().findViewById<ImageView>(R.id.dogPictureTwo)
        description = requireView().findViewById<EditText>(R.id.add_post_description_plain_text)
        date = requireView().findViewById<DatePicker>(R.id.add_post_date_plain_text)
        time = requireView().findViewById<EditText>(R.id.add_post_time_plain_text)

        val cancel: Button = requireView().findViewById<Button>(R.id.button_cancel_post)
        val save: Button = requireView().findViewById<Button>(R.id.button_add_post)

        cancel.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this.requireContext(), R.style.Theme_PopUpDialog)
            alertDialog.setMessage("Are you sure you want to cancel post?")

            alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                val fragment: Fragment = HomeFragment()
                val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.add_post_fragment, fragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            })
            alertDialog.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            alertDialog.show()
        }

        save.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this.requireContext(), R.style.Theme_PopUpDialog)
            alertDialog.setMessage("Are you sure you want to save post?")

            alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                val fragment: Fragment = HomeFragment()
                val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.add_post_fragment, fragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()

                val query = auth.currentUser?.uid

                val dataToSave: MutableMap<String, Any> =
                    HashMap()

                val dateForSaving : String

                if(!TextUtils.isEmpty(description.text.toString())) {
                    dataToSave["description"] = description.text.toString()
                }

                dateForSaving = date.dayOfMonth.toString() + "." + (date.month + 1).toString() + "." + date.year.toString()
                dataToSave["date"] = dateForSaving

                if(!TextUtils.isEmpty(time.text.toString())) {
                    dataToSave["time"] = time.text.toString()
                }
                if (query != null) {
                    dataToSave["userId"] = query
                }
                dataToSave["timeOfPosting"] = Calendar.getInstance().time
                dataToSave["status"] = "OPEN"

                docRef.collection("posts").add(dataToSave).addOnSuccessListener {
                    Log.d("TAG", "Post is saved! ")
                    Toast.makeText(
                        context,
                        "Post is saved in database!",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }.addOnFailureListener { e ->
                    Toast.makeText(
                        context,
                        "Post is not saved! Try again! ",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.w("TAG", "Post is not saved in database! ", e)
                }
            })
            alertDialog.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            alertDialog.show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_post, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}