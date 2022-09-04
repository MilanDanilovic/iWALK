package elfak.mosis.iwalk

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView

class AddPetFragment : Fragment() {

    private lateinit var name : EditText
    private lateinit var breed : EditText
    private lateinit var weight : EditText
    private lateinit var description : EditText
    private lateinit var image : CircleImageView
    private lateinit var imageUri: Uri
    private lateinit var imageUrl : String
    private val storage = FirebaseStorage.getInstance().reference
    private lateinit var auth: FirebaseAuth
    private val docRef = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        name = requireView().findViewById<EditText>(R.id.pet_add_name_value)
        breed = requireView().findViewById<EditText>(R.id.pet_add_breed_value)
        weight = requireView().findViewById<EditText>(R.id.pet_add_weight_value)
        description = requireView().findViewById<EditText>(R.id.pet_add_description_value)
        image = requireView().findViewById<CircleImageView>(R.id.pet_add_picture)

        image.setOnClickListener{
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 71)
        }

        val cancel: Button = requireView().findViewById<Button>(R.id.button_cancel_add_pet)
        cancel.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this.requireContext(), R.style.Theme_PopUpDialog)
            alertDialog.setMessage("Are you sure you want to cancel adding new pet?")

            alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                val fragment: Fragment = MyPetsFragment()
                val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.add_pet_fragment, fragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            })
            alertDialog.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            alertDialog.show()
        }

        val save: Button = requireView().findViewById<Button>(R.id.button_add_pet)
        save.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this.requireContext(), R.style.Theme_PopUpDialog)
            alertDialog.setMessage("Are you sure you want to save new pet?")

            alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                val fragment: Fragment = MyPetsFragment()
                val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.add_pet_fragment, fragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()

                val usersRef = docRef.collection("users")
                val petsRef = docRef.collection("pets")
                val query = auth.currentUser?.uid
                val documentReference = docRef.collection("users")
                    .document(FirebaseAuth.getInstance().currentUser!!.uid)

                val dataToSave: MutableMap<String, Any> =
                    HashMap()

                if(!TextUtils.isEmpty(name.text.toString())) {
                    dataToSave["name"] = name.text.toString()
                }
                if(!TextUtils.isEmpty(breed.text.toString())) {
                    dataToSave["breed"] = breed.text.toString()
                }
                if(!TextUtils.isEmpty(weight.text.toString())) {
                    dataToSave["weight"] = weight.text.toString()
                }
                if(!TextUtils.isEmpty(description.text.toString())) {
                    dataToSave["description"] = description.text.toString()
                }
                if (query != null) {
                    dataToSave["userId"] = query
                }
                if(!TextUtils.isEmpty(imageUrl)){
                    dataToSave["petImageUrl"] = imageUrl
                }

                docRef.collection("pets").add(dataToSave).addOnSuccessListener {
                    Log.d("TAG", "Pet is saved! ")
                    Toast.makeText(
                        context,
                        "Pet is saved in database!",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }.addOnFailureListener { e ->
                    Toast.makeText(
                        context,
                        "Pet is not saved! Try again! ",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.w("TAG", "Pet is not saved in database! ", e)
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
        return inflater.inflate(R.layout.fragment_add_pet, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 71 ) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    imageUri = data.data!!
                    image.setImageURI(imageUri)
                    val bitmap = MediaStore.Images.Media.getBitmap(this@AddPetFragment.context?.contentResolver, imageUri)
                    image.setImageBitmap(bitmap)
                    uploadImage()
                }
            }
        }
    }

    private fun uploadImage() {
        val ref = storage.child("myPetsImages/" + System.currentTimeMillis())
        val uploadTask = ref.putFile(imageUri)
        uploadTask.addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { uri ->

                imageUrl = uri.toString()

            }
        }
    }

}