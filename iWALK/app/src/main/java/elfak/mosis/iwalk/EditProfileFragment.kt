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
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.collections.HashMap

class EditProfileFragment : Fragment() {

    private lateinit var newName : EditText
    private lateinit var newSurname : EditText
    private lateinit var newUsername : EditText
    private lateinit var newPhone : EditText

    private lateinit var baseAuth: FirebaseAuth
    private lateinit var imageUri: Uri
    private lateinit var userImage : CircleImageView
    private lateinit var imageUrl : String
    private val docRef = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        baseAuth = FirebaseAuth.getInstance()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userImage = requireView().findViewById<CircleImageView>(R.id.user_profile_picture)
        val cancel: ImageView = requireView().findViewById<ImageView>(R.id.edit_user_cancel)
        val save: ImageView = requireView().findViewById<ImageView>(R.id.edit_user_save)

        newName = requireView().findViewById<EditText>(R.id.user_profile_name_value)
        newSurname = requireView().findViewById<EditText>(R.id.user_profile_surname_value)
        newUsername = requireView().findViewById<EditText>(R.id.user_profile_username_value)
        newPhone = requireView().findViewById<EditText>(R.id.user_profile_phone_value)

        userImage.setOnClickListener{

            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 71)
        }

        cancel.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this.requireContext(), R.style.Theme_PopUpDialog)
            alertDialog.setMessage("Are you sure you want to cancel changes?")

            alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                val fragment: Fragment = ProfileFragment()
                val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.edit_profile_fragment, fragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            })
            alertDialog.setNegativeButton("No", DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
            alertDialog.show()
        }

        save.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this.requireContext(), R.style.Theme_PopUpDialog)
            alertDialog.setMessage("Are you sure you want to save changes?")

            alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                val fragment: Fragment = ProfileFragment()
                val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.edit_profile_fragment, fragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()

                val userName = newUsername.text.toString()
                val usersRef = docRef.collection("users")
                val query = usersRef.whereEqualTo("username", userName)
                val documentReference = docRef.collection("users")
                    .document(FirebaseAuth.getInstance().currentUser!!.uid)

                if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(
                        this@EditProfileFragment.context,
                        "Username field can not be empty!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else{
                    query.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (document in task.result) {
                                val pom = document.getString("username")
                                if (pom == userName) {
                                    Log.d("TAG", "user exist")
                                }
                            }
                        }
                        if (task.result.size() == 0) {

                            val dataToSave: MutableMap<String, Any> =
                                HashMap()

                            if(!TextUtils.isEmpty(newName.text.toString())) {
                                dataToSave["name"] = newName.text.toString()
                            }
                            if(!TextUtils.isEmpty(newSurname.text.toString())) {
                                dataToSave["surname"] = newSurname.text.toString()
                            }
                            if(!TextUtils.isEmpty(newUsername.text.toString())) {
                                dataToSave["username"] = newUsername.text.toString()
                            }
                            if(!TextUtils.isEmpty(newPhone.text.toString())) {
                                dataToSave["phone"] = newPhone.text.toString()
                            }
                            if(!TextUtils.isEmpty(imageUrl)){
                                dataToSave["profileImageUrl"] = imageUrl
                            }


                            Log.d("TAG", "User does not exist")
                            documentReference.update(dataToSave)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            this@EditProfileFragment.context,
                                            "Profile is successfully updated.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        startActivity(
                                            Intent(
                                                this@EditProfileFragment.context,
                                                HomeActivity::class.java
                                            )
                                        )
                                    } else {
                                        Toast.makeText(
                                            this@EditProfileFragment.context,
                                            "Error while updating data!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }.addOnFailureListener { }
                        } else {
                            newUsername.error = "Username already exists!"
                        }
                    }
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
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
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
                }
                userImage.setImageURI(imageUri)
                //mora se pamtiti u storage
                val bitmap = MediaStore.Images.Media.getBitmap(this@EditProfileFragment.context?.contentResolver, imageUri)
                userImage.setImageBitmap(bitmap)

                uploadImage()
            }
        }
    }
    private fun uploadImage() {
        val ref = storage.child("userImages/" + FirebaseAuth.getInstance().currentUser?.uid)
        val uploadTask = ref.putFile(imageUri)
        uploadTask.addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { uri ->

                imageUrl = uri.toString()

            }
        }
    }

}