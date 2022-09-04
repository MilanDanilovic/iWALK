package elfak.mosis.iwalk

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class WalkerInfoFragment : Fragment() {

    private lateinit var walkerName : TextView
    private lateinit var walkerSurname : TextView
    private lateinit var walkerUsername : TextView
    private lateinit var walkerScore : TextView
    private lateinit var walkerPhone : TextView
    private lateinit var walkerEmail : TextView
    private lateinit var walkerNumberOfWalks : TextView
    private lateinit var walkerImage : CircleImageView
    private lateinit var sendMessage : ImageView
    private lateinit var likeWalker : ImageView
    private lateinit var seePets : Button

    private lateinit var walkerId: String
    private var check: String = "no"

    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    val usersRef: CollectionReference = db.collection("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        val bundle = this.arguments
        if (bundle != null) {
            walkerId = bundle.getString("user_id")!!
        }

        walkerImage = requireView().findViewById<CircleImageView>(R.id.user_profile_picture)
        walkerName = requireView().findViewById<EditText>(R.id.walker_profile_name)
        walkerSurname = requireView().findViewById<EditText>(R.id.walker_profile_surname)
        walkerUsername = requireView().findViewById<EditText>(R.id.walker_profile_username_value)
        walkerScore = requireView().findViewById<EditText>(R.id.walker_profile_score_value)
        walkerNumberOfWalks = requireView().findViewById<EditText>(R.id.walker_profile_numwalks_value)
        walkerPhone = requireView().findViewById<EditText>(R.id.walker_profile_phone_value)
        walkerEmail = requireView().findViewById<EditText>(R.id.walker_profile_email_value)
        sendMessage = requireView().findViewById<ImageView>(R.id.walker_profile_send_message)
        likeWalker = requireView().findViewById<ImageView>(R.id.walker_profile_like)
        seePets = requireView().findViewById<Button>(R.id.button_see_my_pets)

        usersRef.get()
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        if (document.id == walkerId) {
                            Picasso.get().load(document.getString("profileImageUrl"))
                                .into(walkerImage)

                            walkerName.setText(document.getString("name"))
                            walkerSurname.setText(document.getString("surname"))
                            walkerUsername.setText(document.getString("username"))
                            walkerScore.setText(document["score"].toString())
                            walkerNumberOfWalks.setText(document["numberOfWalks"].toString())
                            walkerPhone.setText(document.getString("phone"))
                            walkerEmail.setText(document.getString("email"))
                        }
                        if (document.id == auth.currentUser!!.uid) {
                            var favourites = arrayListOf<String>()
                            if (document["favourites"] != null) {
                                favourites = document["favourites"] as ArrayList<String>
                                if (favourites.contains(walkerId)) {
                                    likeWalker.setImageDrawable(resources.getDrawable(R.drawable.ic_heart_full))
                                    check = "yes"
                                }
                                else {
                                    likeWalker.setImageDrawable(resources.getDrawable(R.drawable.ic_heart))
                                    check = "no"
                                }

                            }
                        }
                    }
                }
                else {
                    Log.d("TAG", "Error getting documents: ", task.exception)
                }
            }

        likeWalker.setOnClickListener(View.OnClickListener { v ->
            if (check == "yes") {//JESTE LAJKOVAN
                usersRef.get()
                    .addOnCompleteListener{ task ->
                        if (task.isSuccessful) {
                            for (document in task.result) {
                                if (document.id == auth.currentUser!!.uid) {

                                    var favourites = arrayListOf<String>()
                                    if (document["favourites"] != null) {
                                        favourites = document["favourites"] as ArrayList<String>
                                        if (favourites.contains(walkerId)) {
                                            favourites.remove(walkerId)

                                            val documentReference = auth!!.currentUser!!.uid.let { it1 -> db.collection("users").document(it1) }
                                            documentReference.update(
                                                "favourites", favourites
                                            )
                                                .addOnCompleteListener(OnCompleteListener<Void?> { task ->
                                                    if (task.isSuccessful) {

                                                        likeWalker.setImageDrawable(resources.getDrawable(R.drawable.ic_heart))
                                                        check = "no"

                                                        Toast.makeText(
                                                            v.context,
                                                            "Data successfully updated.",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    } else {
                                                        Toast.makeText(
                                                            v.context,
                                                            "Error updating data.",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }).addOnFailureListener(OnFailureListener { })


                                        }

                                    }

                                }
                            }
                        }
                        else {
                            Log.d("TAG", "Error getting documents: ", task.exception)
                        }
                    }
            }
            else if (check == "no") {//NIJE LAJKOVAN
                usersRef.get()
                    .addOnCompleteListener{ task ->
                        if (task.isSuccessful) {
                            for (document in task.result) {
                                if (document.id == auth.currentUser!!.uid) {

                                    var favourites = arrayListOf<String>()
                                    if (document["favourites"] != null) {
                                        favourites = document["favourites"] as ArrayList<String>
                                        favourites.add(walkerId)
                                    }
                                    else {
                                        favourites.add(walkerId)
                                    }

                                    val documentReference = auth!!.currentUser!!.uid.let { it1 -> db.collection("users").document(it1) }
                                    documentReference.update(
                                        "favourites", favourites
                                    )
                                        .addOnCompleteListener(OnCompleteListener<Void?> { task ->
                                            if (task.isSuccessful) {

                                                likeWalker.setImageDrawable(resources.getDrawable(R.drawable.ic_heart_full))
                                                check = "yes"

                                                Toast.makeText(
                                                    v.context,
                                                    "Data successfully updated.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    v.context,
                                                    "Error updating data.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }).addOnFailureListener(OnFailureListener { })

                                }
                            }
                        }
                        else {
                            Log.d("TAG", "Error getting documents: ", task.exception)
                        }
                    }
            }
        })

        seePets.setOnClickListener(View.OnClickListener {
            val activity= context as AppCompatActivity
            val personsPetsFragment = PersonsPetsFragment()
            val bundle = Bundle()
            bundle.putString("user_id", walkerId)
            personsPetsFragment.setArguments(bundle)
            activity.supportFragmentManager.beginTransaction().replace(R.id.walker_info_fragment, personsPetsFragment).commit()
        })

        sendMessage.setOnClickListener(View.OnClickListener {
            val activity= context as AppCompatActivity
            val messageForWalkerFragment = MessageForWalkerFragment()
            val bundle = Bundle()
            bundle.putString("user_id", walkerId)
            messageForWalkerFragment.setArguments(bundle)
            activity.supportFragmentManager.beginTransaction().replace(R.id.walker_info_fragment, messageForWalkerFragment).commit()
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_walker_info, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}