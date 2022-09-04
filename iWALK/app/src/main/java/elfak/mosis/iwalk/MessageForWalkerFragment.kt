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
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

class MessageForWalkerFragment : Fragment() {

    private lateinit var descriptionMessage : EditText
    private lateinit var dateMessage : TextView
    private lateinit var timeMessage : TextView
    private lateinit var dogPicOneMessage : ImageView
    private lateinit var dogPicTwoMessage : ImageView
    private lateinit var pickDate : Button
    private lateinit var pickTime : Button

    private lateinit var auth: FirebaseAuth
    private lateinit var imageUri: Uri
    private lateinit var imageUri2: Uri
    private lateinit var imageUrl : String
    private lateinit var imageUrl2 : String
    private val storage = FirebaseStorage.getInstance().reference
    private val docRef = FirebaseFirestore.getInstance()

    //Calendar
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    private lateinit var walkerId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cancel: Button = requireView().findViewById<Button>(R.id.button_cancel_message)
        val send: Button = requireView().findViewById<Button>(R.id.button_send_message)

        descriptionMessage = requireView().findViewById<EditText>(R.id.message_for_walker_description_value)
        dateMessage = requireView().findViewById<TextView>(R.id.date_value_message)
        timeMessage = requireView().findViewById<TextView>(R.id.time_value_message)
        dogPicOneMessage = requireView().findViewById<ImageView>(R.id.messageDogPictureOne)
        dogPicTwoMessage = requireView().findViewById<ImageView>(R.id.messageDogPictureTwo)
        pickDate = requireView().findViewById<Button>(R.id.pick_date_message)
        pickTime = requireView().findViewById<Button>(R.id.pick_time_message)

        auth = Firebase.auth

        val bundle = this.arguments
        if (bundle != null) {
            walkerId = bundle.getString("user_id")!!
        }

        dogPicOneMessage.setOnClickListener{
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100)
        }

        dogPicTwoMessage.setOnClickListener{
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 101)
        }

        pickDate.setOnClickListener {
            val dpd = DatePickerDialog( requireContext(),
                DatePickerDialog.OnDateSetListener { view: DatePicker, mYear: Int, mMOnth: Int, mDay: Int ->
                val dateTextValue = "$mDay/${mMOnth+1}/$mYear"
                dateMessage.text= dateTextValue

            },year,month,day)
            dpd.show()
        }

        pickTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener{ _: TimePicker, hour:Int, minute:Int->
                cal.set(Calendar.HOUR_OF_DAY,hour)
                cal.set(Calendar.MINUTE,minute)

                timeMessage.text = SimpleDateFormat("HH:mm").format(cal.time)
            }
            TimePickerDialog(requireContext(),timeSetListener,cal.get(Calendar.HOUR_OF_DAY),cal.get(
                Calendar.MINUTE),true).show()
        }

        cancel.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this.requireContext(), R.style.Theme_PopUpDialog)
            alertDialog.setMessage("Are you sure you want to cancel message?")

            alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                val fragment: Fragment = HomeFragment()
                val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.message_for_walker_fragment, fragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            })
            alertDialog.setNegativeButton("No", DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
            alertDialog.show()
        }

        send.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this.requireContext(), R.style.Theme_PopUpDialog)
            alertDialog.setMessage("Are you sure you want to send message?")

            alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->

                val query = auth.currentUser?.uid

                val dataToSave: MutableMap<String, Any> =
                    HashMap()

                if(!TextUtils.isEmpty(descriptionMessage.text.toString())) {
                    dataToSave["description"] = descriptionMessage.text.toString()
                }

                if(!TextUtils.isEmpty(dateMessage.text.toString())) {
                    dataToSave["date"] = dateMessage.text.toString()
                }

                if(!TextUtils.isEmpty(timeMessage.text.toString())) {
                    dataToSave["time"] = timeMessage.text.toString()
                }

                if(!TextUtils.isEmpty(imageUrl)){
                    dataToSave["dogImage1Url"] = imageUrl
                }
                if(!TextUtils.isEmpty(imageUrl2)){
                    dataToSave["dogImage2Url"] = imageUrl2
                }

                if (query != null) {
                    dataToSave["senderId"] = query
                }

                dataToSave["receiverId"] = walkerId

                dataToSave["timeOfSending"] = Calendar.getInstance().time

                dataToSave["status"] = "OPEN"

                docRef.collection("messages").add(dataToSave).addOnSuccessListener {
                    Log.d("TAG", "messages is saved! ")
                    val fragment: Fragment = HomeFragment()
                    val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                    val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.message_for_walker_fragment, fragment)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                    Toast.makeText(
                        context,
                        "messages is saved in database!",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }.addOnFailureListener { e ->
                    Toast.makeText(
                        context,
                        "messages is not saved! Try again! ",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.w("TAG", "messages is not saved in database! ", e)
                }
            })
            alertDialog.setNegativeButton("No", DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
            alertDialog.show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message_for_walker, container, false)
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
                    dogPicOneMessage.setImageURI(imageUri)
                    val bitmap = MediaStore.Images.Media.getBitmap(this@MessageForWalkerFragment.context?.contentResolver, imageUri)
                    dogPicOneMessage.setImageBitmap(bitmap)
                    uploadImage()
                }
            }
        }
        else if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    imageUri2 = data.data!!
                    dogPicTwoMessage.setImageURI(imageUri2)
                    val bitmap = MediaStore.Images.Media.getBitmap(this@MessageForWalkerFragment.context?.contentResolver, imageUri2)
                    dogPicTwoMessage.setImageBitmap(bitmap)
                    uploadImage2()
                }
            }
        }
    }

    private fun uploadImage() {
        val ref = storage.child("postsPetImages1/" + System.currentTimeMillis())
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