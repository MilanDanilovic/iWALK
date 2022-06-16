package elfak.mosis.iwalk

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsClient.getPackageName
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
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
        val usersRef = db.collection("users")
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
                        else if (document["receiver"].toString() == auth.currentUser!!.uid && document["sender"].toString() ==userId) {
                            addFriend.setImageDrawable(resources.getDrawable(R.drawable.ic_save_changes))
                            requestAlreadySent = "sentMe"
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
                                                                    db.collection("friendRequestsposts").document(
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
                usersReceiverRef.get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (document in task.result) {
                                if (document.id == userId) { //DODAJEM KOD PRETRAZENOG USER-A

                                    val query = usersRef.whereEqualTo("username", document["username"])
                                    query.get().addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val dataToSave: MutableMap<Array<String>, Any> =
                                                HashMap()
                                            val friend = mutableMapOf(
                                                "friendId" to auth.currentUser!!.uid
                                            )

                                            dataToSave[arrayOf("friends")] = friend

                                            val documentReference = db.collection("users").document(userId)
                                            documentReference.update(dataToSave)
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        Toast.makeText(
                                                            context,
                                                            "Profile is successfully updated.",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    } else {
                                                        Toast.makeText(
                                                            context,
                                                            "Error while updating data!",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }.addOnFailureListener { }
                                        }
                                    }
                                    /*

                                    usersRef.whereArrayContains("friends", friend).get().addOnCompleteListener{ task ->
                                        task.apply {
                                            if (task.isSuccessful) {
                                                for (documentFriends in result) {
                                                    val docIdRef = usersRef.document(documentFriends.id)
                                                    docIdRef.update("friends", FieldValue.arrayUnion(friend)).addOnCompleteListener{ add ->
                                                        if (add.isSuccessful) {
                                                            docIdRef.update("friends", FieldValue.arrayUnion(FriendshipFriend(auth.currentUser!!.uid))).addOnCompleteListener{ additionTask ->
                                                                if (additionTask.isSuccessful) {
                                                                    Toast.makeText(
                                                                        context,
                                                                        "Update complete! ",
                                                                        Toast.LENGTH_LONG
                                                                    ).show()
                                                                    Log.w("TAG", "Friend request is not saved in database! ")
                                                                } else {
                                                                    additionTask.exception?.message?.let {
                                                                        Log.e("TAG", it)
                                                                    }
                                                                }
                                                            }
                                                        } else {
                                                            add.exception?.message?.let {
                                                                Log.e("TAG", it)
                                                            }
                                                        }
                                                    }
                                                }
                                            } else {
                                                task.exception?.message?.let {
                                                    Log.e("TAG", it)
                                                }
                                            }
                                        }
                                    }*/
                                }
                                if (document.id == auth.currentUser!!.uid) {

                                    /*val friend = mutableMapOf(
                                        "friendId" to userId
                                    )

                                    usersRef.whereArrayContains("friends", friend).get().addOnCompleteListener{ task ->
                                        task.apply {
                                            if (task.isSuccessful) {
                                                for (documentFriends in result) {
                                                    val docIdRef = usersRef.document(documentFriends.id)
                                                    docIdRef.update("friends", FieldValue.arrayUnion(friend)).addOnCompleteListener{ add ->
                                                        if (add.isSuccessful) {
                                                            docIdRef.update("friends", FieldValue.arrayUnion(FriendshipFriend(userId))).addOnCompleteListener{ additionTask ->
                                                                if (additionTask.isSuccessful) {
                                                                    Toast.makeText(
                                                                        context,
                                                                        "Update complete! ",
                                                                        Toast.LENGTH_LONG
                                                                    ).show()
                                                                    Log.w("TAG", "Friend request is not saved in database! ")
                                                                } else {
                                                                    additionTask.exception?.message?.let {
                                                                        Log.e("TAG", it)
                                                                    }
                                                                }
                                                            }
                                                        } else {
                                                            add.exception?.message?.let {
                                                                Log.e("TAG", it)
                                                            }
                                                        }
                                                    }
                                                }
                                            } else {
                                                task.exception?.message?.let {
                                                    Log.e("TAG", it)
                                                }
                                            }
                                        }
                                    }*/
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