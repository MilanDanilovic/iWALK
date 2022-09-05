package elfak.mosis.iwalk

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class EditPetFragment : Fragment() {

    private lateinit var name : EditText
    private lateinit var breed : EditText
    private lateinit var weight : EditText
    private lateinit var description : EditText
    private lateinit var image : CircleImageView
    private lateinit var imageUri: Uri
    private lateinit var imageUrl : String
    private val storage = FirebaseStorage.getInstance().reference
    var petId: String? = null
    private val docRef = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        name = requireView().findViewById<EditText>(R.id.pet_edit_name_value)
        breed = requireView().findViewById<EditText>(R.id.pet_edit_breed_value)
        weight = requireView().findViewById<EditText>(R.id.pet_edit_weight_value)
        description = requireView().findViewById<EditText>(R.id.pet_edit_description_value)
        image = requireView().findViewById<CircleImageView>(R.id.pet_edit_picture)

        val bundle = this.arguments
        if (bundle != null) {
            name.setText(bundle.getString("pet_name"))
            breed.setText(bundle.getString("pet_breed"))
            weight.setText(bundle.getString("pet_weight"))
            description.setText(bundle.getString("pet_description"))
            petId = bundle.getString("pet_id")
            Picasso.get().load(bundle.getString("pet_image"))
                .into(image)
            imageUrl = bundle.getString("pet_image")!!
        }

        image.setOnClickListener{
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 71)
        }

        val cancel: Button = requireView().findViewById<Button>(R.id.button_cancel_edit_pet)
        val edit: Button = requireView().findViewById<Button>(R.id.button_edit_pet)

        cancel.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this.requireContext(), R.style.Theme_PopUpDialog)
            alertDialog.setMessage("Are you sure you want to cancel changes?")

            alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                val fragment: Fragment = MyPetsFragment()
                val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.edit_pet_fragment, fragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            })
            alertDialog.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            alertDialog.show()
        }

        edit.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this.requireContext(), R.style.Theme_PopUpDialog)
            alertDialog.setMessage("Are you sure you want to save changes?")

            alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->

                val newName = name.text.toString()
                val newBreed = breed.text.toString()
                val newWeight = weight.text.toString()
                val newDescription = description.text.toString()
                val newImage = imageUrl

                val documentReference = petId?.let { it1 -> docRef.collection("pets").document(it1) }
                documentReference?.update(
                    "name", newName,
                    "breed", newBreed,
                    "weight", newWeight,
                    "description", newDescription,
                    "petImageUrl", newImage
                )?.addOnCompleteListener(OnCompleteListener<Void?> { task ->
                    if (task.isSuccessful) {
                        val fragment: Fragment = MyPetsFragment()
                        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                        fragmentTransaction.replace(R.id.edit_pet_fragment, fragment)
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.commit()
                        Toast.makeText(
                            context,
                            "Pet edited!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Error editing pet!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })?.addOnFailureListener(OnFailureListener { })
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
        return inflater.inflate(R.layout.fragment_edit_pet, container, false)
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
                    val bitmap = MediaStore.Images.Media.getBitmap(this@EditPetFragment.context?.contentResolver, imageUri)
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