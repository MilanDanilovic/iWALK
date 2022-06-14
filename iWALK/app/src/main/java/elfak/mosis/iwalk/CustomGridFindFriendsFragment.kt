package elfak.mosis.iwalk

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsClient.getPackageName
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class CustomGridFindFriendsFragment : Fragment() {

    private lateinit var username : TextView
    private lateinit var image : CircleImageView
    private lateinit var addFriend : ImageView
    private lateinit var addFriendBluetooth : ImageView
    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private lateinit var requestAlreadySent: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        username = requireView().findViewById<TextView>(R.id.find_friends_username_value)
        image = requireView().findViewById<CircleImageView>(R.id.find_friends_profile_picture)
        addFriend = requireView().findViewById<ImageView>(R.id.find_friends_add_friend)
        addFriendBluetooth = requireView().findViewById<ImageView>(R.id.find_friends_add_friend_bluetooth)
        val usersSenderRef: CollectionReference = db.collection("users")
        val usersReceiverRef: CollectionReference = db.collection("users")
        val friendRequests: CollectionReference = db.collection("friendRequests")
        auth = Firebase.auth

        val bundle = this.arguments
        if (bundle != null) {
            username.setText(bundle.getString("user_username"))
            Picasso.get().load(bundle.getString("user_image"))
                .into(image)
            userId = bundle.getString("user_id")!!
        }

        friendRequests.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        if (document["receiver"].toString() == userId && document["sender"].toString() == auth.currentUser!!.uid) {
                            addFriend.setImageDrawable(resources.getDrawable(R.drawable.ic_cancel_changes))
                            requestAlreadySent = "true"
                            break
                        }
                        else {
                            addFriend.setImageDrawable(resources.getDrawable(R.drawable.ic_add_post))
                            requestAlreadySent = "false"
                        }
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.exception)
                }
            }

        addFriend.setOnClickListener (View.OnClickListener {
            usersSenderRef.get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            if (document.id == auth.currentUser?.uid) {
                                var usernameToSave: String = document["username"].toString()
                                var userImageToSave: String = document["profileImageUrl"].toString()
                                usersReceiverRef.get()
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            for (documentReceiver in task.result) {
                                                if (documentReceiver.getString("username") == username.text.toString()) {

                                                    val dataToSave: MutableMap<String, Any> =
                                                        HashMap()
                                                    dataToSave["senderUsername"] = usernameToSave
                                                    dataToSave["senderImage"] = userImageToSave
                                                    dataToSave["sender"] = auth.currentUser?.uid!!
                                                    dataToSave["receiver"] = documentReceiver.id

                                                    db.collection("friendRequests").add(dataToSave).addOnSuccessListener {
                                                        Log.d("TAG", "Friend request is saved! ")
                                                        Toast.makeText(
                                                            context,
                                                            "Friend request is sent!",
                                                            Toast.LENGTH_LONG
                                                        )
                                                            .show()
                                                    }.addOnFailureListener { e ->
                                                        Toast.makeText(
                                                            context,
                                                            "Friend request is not saved! Try again! ",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                        Log.w("TAG", "Friend request is not saved in database! ", e)
                                                    }
                                                }
                                            }
                                        } else {
                                            Log.d("TAG", "Error getting documents: ", task.exception)
                                        }
                                    }
                            }
                        }
                    } else {
                        Log.d("TAG", "Error getting documents: ", task.exception)
                    }
                }
        })
    }

    private fun sendFriendRequest() {
        TODO("Not yet implemented")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_custom_grid_find_friends, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}