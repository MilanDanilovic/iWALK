package elfak.mosis.iwalk

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
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
    private var requestAlreadySent: String = ""
    private var check: String = ""

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
        val usersRef: CollectionReference = db.collection("users")
        auth = Firebase.auth

        val bundle = this.arguments
        if (bundle != null) {
            username.setText(bundle.getString("user_username"))
            Picasso.get().load(bundle.getString("user_image"))
                .into(image)
            userId = bundle.getString("user_id")!!
        }

        usersReceiverRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        if (document.id == auth.currentUser!!.uid) {
                            var friends = arrayListOf<String>()
                            if (document["friends"] != null) {
                                friends = document["friends"] as ArrayList<String>
                                for (friend in friends) {
                                    if (friend.equals(userId)) {
                                        addFriend.setImageDrawable(resources.getDrawable(R.drawable.ic_friends)) //VEC SMO PRIJATELJI
                                        check = "true"
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.exception)
                }
            }

        friendRequests.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        if (document["receiver"].toString() == userId && document["sender"].toString() == auth.currentUser!!.uid) {
                            addFriend.setImageDrawable(resources.getDrawable(R.drawable.ic_cancel_changes))
                            requestAlreadySent = "true" //POSLALA SAM ZAHTEV
                            break
                        }
                        else if (document["receiver"].toString() == auth.currentUser!!.uid && document["sender"].toString() ==userId) {
                            addFriend.setImageDrawable(resources.getDrawable(R.drawable.ic_save_changes))
                            requestAlreadySent = "sentMe" //POSLAT MENI ZAHTEV
                            break
                        }
                        else {
                            addFriend.setImageDrawable(resources.getDrawable(R.drawable.ic_add_post))
                            requestAlreadySent = "false" //NIJE MI NI POSLAT NITI SAM POSLALA
                        }
                    }
                    if (check == "") {
                        addFriend.setImageDrawable(resources.getDrawable(R.drawable.ic_add_post))
                        requestAlreadySent = "false" //NIJE MI NI POSLAT NITI SAM POSLALA
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.exception)
                }
            }
            .addOnFailureListener{
                addFriend.setImageDrawable(resources.getDrawable(R.drawable.ic_add_post))
                requestAlreadySent = "false" //NIJE MI NI POSLAT NITI SAM POSLALA
            }



        addFriend.setOnClickListener(View.OnClickListener {
            if (requestAlreadySent == "true") {
                usersReceiverRef.get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (document in task.result) {
                                if (document["username"] == username.text.toString()) {
                                    friendRequests.get()
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                for (documentRequest in task.result) {
                                                    if (documentRequest.getString("sender") == auth.currentUser!!.uid && documentRequest.getString("receiver") == document.id) {
                                                        db.collection("friendRequests")
                                                            .document(documentRequest.id)
                                                            .delete()
                                                            .addOnCompleteListener { task ->
                                                                if (task.isSuccessful) {
                                                                    db.collection("friendRequests").document(
                                                                        documentRequest.id
                                                                    )
                                                                        .delete()
                                                                        .addOnSuccessListener {
                                                                            Log.d(
                                                                                "TAG",
                                                                                "DocumentSnapshot successfully deleted!"
                                                                            )
                                                                        }
                                                                        .addOnFailureListener { e ->
                                                                            Log.w(
                                                                                "TAG",
                                                                                "Error deleting documentAAAAAAAAAAAAAAAAAAAAAAAAA",
                                                                                e
                                                                            )
                                                                        }
                                                                    Toast.makeText(context, "Friend request is deleted!", Toast.LENGTH_LONG).show()
                                                                } else {
                                                                    Toast.makeText(
                                                                        context,
                                                                        "Error deleting friend request!",
                                                                        Toast.LENGTH_LONG
                                                                    ).show()
                                                                    Log.w(
                                                                        "TAG",
                                                                        "Error deleting documentAAAAAAAAAAAAAAAAAAAAAAAAA"
                                                                    )
                                                                }
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
            }
            else if (requestAlreadySent == "false") {
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
                                                            addFriend.setImageDrawable(resources.getDrawable(R.drawable.ic_cancel_changes))
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
            }
            else if (requestAlreadySent == "sentMe") {

                usersRef.get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (document in task.result) {
                                if (document.id == userId) { //DODAJEM KOD PRETRAZENOG USER-A

                                    var friends = arrayListOf<String>()
                                    if (document["friends"] == null) {
                                        friends.add(auth.currentUser!!.uid)
                                    }
                                    else {
                                        friends = document["friends"] as ArrayList<String>
                                        friends.add(auth.currentUser!!.uid)
                                    }

                                    val documentReference = userId?.let { it1 -> db.collection("users").document(it1) }
                                    documentReference?.update(
                                        "friends", friends
                                    )?.addOnCompleteListener(OnCompleteListener<Void?> { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(
                                                context,
                                                "Data successfully updated.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Error updating data.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    })?.addOnFailureListener(OnFailureListener { })
                                }
                                if (document.id == auth.currentUser!!.uid) { //DODAJEM KOD TRENUTNOG USER-A

                                    var friends = arrayListOf<String>()
                                    if (document["friends"] == null) {
                                        friends.add(userId)
                                    }
                                    else {
                                        friends = document["friends"] as ArrayList<String>
                                        friends.add(userId)
                                    }

                                    val documentReference = auth.currentUser!!.uid?.let { it1 -> db.collection("users").document(it1) }
                                    documentReference?.update(
                                        "friends", friends
                                    )?.addOnCompleteListener(OnCompleteListener<Void?> { task ->
                                        if (task.isSuccessful) {
                                            addFriend.setImageDrawable(resources.getDrawable(R.drawable.ic_friends))
                                            usersSenderRef.get()
                                                .addOnCompleteListener { taskDel ->
                                                    if (taskDel.isSuccessful) {
                                                        for (documentDel in taskDel.result) {
                                                            if (documentDel["username"] == username.text.toString()) {
                                                                friendRequests.get()
                                                                    .addOnCompleteListener { task ->
                                                                        if (task.isSuccessful) {
                                                                            for (documentRequest in task.result) {
                                                                                if (documentRequest.getString("sender") == userId && documentRequest.getString("receiver") == auth.currentUser!!.uid) {
                                                                                    db.collection("friendRequests")
                                                                                        .document(documentRequest.id)
                                                                                        .delete()
                                                                                        .addOnCompleteListener { task ->
                                                                                            if (task.isSuccessful) {
                                                                                                db.collection("friendRequests").document(
                                                                                                    documentRequest.id
                                                                                                )
                                                                                                    .delete()
                                                                                                    .addOnSuccessListener {
                                                                                                        Log.d(
                                                                                                            "TAG",
                                                                                                            "DocumentSnapshot successfully deleted!"
                                                                                                        )
                                                                                                    }
                                                                                                    .addOnFailureListener { e ->
                                                                                                        Log.w(
                                                                                                            "TAG",
                                                                                                            "Error deleting documentAAAAAAAAAAAAAAAAAAAAAAAAA",
                                                                                                            e
                                                                                                        )
                                                                                                    }
                                                                                                Toast.makeText(context, "Friend request is deleted!", Toast.LENGTH_LONG).show()
                                                                                            } else {
                                                                                                Toast.makeText(
                                                                                                    context,
                                                                                                    "Error deleting friend request!",
                                                                                                    Toast.LENGTH_LONG
                                                                                                ).show()
                                                                                                Log.w(
                                                                                                    "TAG",
                                                                                                    "Error deleting documentAAAAAAAAAAAAAAAAAAAAAAAAA"
                                                                                                )
                                                                                            }
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
                                                        Log.d("TAG", "Error getting documents: ", taskDel.exception)
                                                    }
                                                }
                                            Toast.makeText(
                                                context,
                                                "Data successfully updated.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Error updating data.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    })?.addOnFailureListener(OnFailureListener { })
                                }
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.exception)
                        }
                    }
            }
        })
    }

    fun append(arr: Array<String>, element: String): Array<String> {
        val list: MutableList<String> = arr.toMutableList()
        list.add(element)
        return list.toTypedArray()
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