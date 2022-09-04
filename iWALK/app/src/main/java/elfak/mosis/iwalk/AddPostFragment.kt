package elfak.mosis.iwalk

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class AddPostFragment : Fragment() {

    private lateinit var dogImage1 : ImageView
    private lateinit var dogImage2 : ImageView
    private lateinit var description : EditText
    private lateinit var datePost : TextView
    private lateinit var timePost : TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var imageUri: Uri
    private lateinit var imageUri2: Uri
    private lateinit var imageUrl : String
    private lateinit var imageUrl2 : String
    private val storage = FirebaseStorage.getInstance().reference
    private val docRef = FirebaseFirestore.getInstance()
    private lateinit var pickDate : Button
    private lateinit var pickTime : Button

    //Calendar
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        dogImage1 = requireView().findViewById<ImageView>(R.id.dogPictureOne)
        dogImage2 = requireView().findViewById<ImageView>(R.id.dogPictureTwo)
        description = requireView().findViewById<EditText>(R.id.add_post_description_plain_text)
        datePost = requireView().findViewById<TextView>(R.id.add_post_date)
        timePost = requireView().findViewById<TextView>(R.id.add_post_time)
        pickDate = requireView().findViewById<Button>(R.id.pick_date_post)
        pickTime = requireView().findViewById<Button>(R.id.pick_time_post)

        val cancel: Button = requireView().findViewById<Button>(R.id.button_cancel_post)
        val save: Button = requireView().findViewById<Button>(R.id.button_add_post)

        pickDate.setOnClickListener {
            val dpd = DatePickerDialog( requireContext(),
                DatePickerDialog.OnDateSetListener { view: DatePicker, mYear: Int, mMOnth: Int, mDay: Int ->
                    val dateTextValue = "$mDay/${mMOnth+1}/$mYear"
                    datePost.text= dateTextValue

                },year,month,day)
            dpd.show()
        }

        pickTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener{ _: TimePicker, hour:Int, minute:Int->
                cal.set(Calendar.HOUR_OF_DAY,hour)
                cal.set(Calendar.MINUTE,minute)

                timePost.text = SimpleDateFormat("HH:mm").format(cal.time)
            }
            TimePickerDialog(requireContext(),timeSetListener,cal.get(Calendar.HOUR_OF_DAY),cal.get(
                Calendar.MINUTE),true).show()
        }

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

        dogImage1.setOnClickListener{
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100)
        }

        dogImage2.setOnClickListener{
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 101)
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

                if(!TextUtils.isEmpty(datePost.text.toString())) {
                    dataToSave["date"] = datePost.text.toString()
                }
                if(!TextUtils.isEmpty(timePost.text.toString())) {
                    dataToSave["time"] = timePost.text.toString()
                }
                if (query != null) {
                    dataToSave["userId"] = query
                }
                if(!TextUtils.isEmpty(imageUrl)){
                    dataToSave["dogImage1Url"] = imageUrl
                }
               if(!TextUtils.isEmpty(imageUrl2)){
                    dataToSave["dogImage2Url"] = imageUrl2
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 ) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    imageUri = data.data!!
                    dogImage1.setImageURI(imageUri)
                    val bitmap = MediaStore.Images.Media.getBitmap(this@AddPostFragment.context?.contentResolver, imageUri)
                    dogImage1.setImageBitmap(bitmap)
                    uploadImage()
                }
            }
        }
        else if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    imageUri2 = data.data!!
                    dogImage2.setImageURI(imageUri2)
                    val bitmap = MediaStore.Images.Media.getBitmap(this@AddPostFragment.context?.contentResolver, imageUri2)
                    dogImage2.setImageBitmap(bitmap)
                    uploadImage2()
                }
            }
        }
    }

    private fun uploadImage() {
        val ref = storage.child("postsPetImages1/" + System.currentTimeMillis())//FirebaseAuth.getInstance().currentUser?.uid
        val uploadTask = ref.putFile(imageUri)
        uploadTask.addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { uri ->

                imageUrl = uri.toString()

            }
        }
    }

    private fun uploadImage2() {
        val ref = storage.child("postsPetImages2/" + System.currentTimeMillis())
        val uploadTask = ref.putFile(imageUri2)
        uploadTask.addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { uri ->

                imageUrl2 = uri.toString()

            }
        }
    }
}