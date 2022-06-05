package elfak.mosis.iwalk

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment : Fragment() {

    private lateinit var score : TextView
    private lateinit var numberOfWalks : TextView
    private lateinit var username : TextView
    private lateinit var phoneNumber: TextView
    private lateinit var email : TextView
    private lateinit var name : TextView
    private lateinit var surname : TextView
    private lateinit var image : CircleImageView

    private lateinit var  auth : FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private lateinit var user : FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        user = auth.currentUser!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val edit: ImageView = requireView().findViewById<ImageView>(R.id.user_profile_edit)
        edit.setOnClickListener(View.OnClickListener {
            val fragment: Fragment = EditProfileFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.profile_fragment, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        })

		val seePets: Button = requireView().findViewById<Button>(R.id.button_see_my_pets)
        seePets.setOnClickListener(View.OnClickListener {
            val fragment: Fragment = MyPetsFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.profile_fragment, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        })
        score = requireView().findViewById<TextView>(R.id.user_profile_score_value)
        numberOfWalks = requireView().findViewById<TextView>(R.id.user_profile_numwalks_value)
        username = requireView().findViewById<TextView>(R.id.user_profile_username_value)
        phoneNumber = requireView().findViewById<TextView>(R.id.user_profile_phone_value)
        email = requireView().findViewById<TextView>(R.id.user_profile_email_value)
        name = requireView().findViewById<TextView>(R.id.user_profile_name)
        surname = requireView().findViewById<TextView>(R.id.user_profile_surname)
        image = requireView().findViewById<CircleImageView>(R.id.user_profile_picture)

        val userIdValue: String = user.uid

        val usersRef: CollectionReference = db.collection("users")
        usersRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        if (document.id == userIdValue) {
                            username.text = document["username"].toString()
                            email.text = document["email"].toString()
                            name.text = document["name"].toString()
                            surname.text = document["surname"].toString()
                            phoneNumber.text = document["phone"].toString()
                            numberOfWalks.text = document["numberOfWalks"].toString()
                            score.text = document["score"].toString()
                            if (document["profileImageUrl"].toString() != "default") {
                                Picasso.get().load(document["profileImageUrl"].toString())
                                    .into(image)
                            }
                            Log.d("TAG", document.id + " => " + document["username"])
                            break
                        }
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.exception)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}