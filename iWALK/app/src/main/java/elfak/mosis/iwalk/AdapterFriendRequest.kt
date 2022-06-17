package elfak.mosis.iwalk

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AdapterFriendRequest (var ctx: Context, friendRequestsList: MutableList<Friend>) :
    RecyclerView.Adapter<MyFriendRequestHolder>() {

    lateinit var friendRequestsList: MutableList<Friend>
    var baseAuth: FirebaseAuth? = null
    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyFriendRequestHolder {
        val mView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.custom_grid_requests_friends, viewGroup, false)
        return MyFriendRequestHolder(mView)
    }

    override fun onBindViewHolder(friendRequestHolder: MyFriendRequestHolder, position: Int) {
        friendRequestHolder.friendUsername.setText(friendRequestsList[position].getFriendUsername())
        Picasso.get().load(friendRequestsList[position].getFriendImage())
            .into(friendRequestHolder.friendImage)
        baseAuth = FirebaseAuth.getInstance()
        val usersRef: CollectionReference = db.collection("users")
        val friendRequests: CollectionReference = db.collection("friendRequests")
        auth = Firebase.auth

        friendRequestHolder.decline.setOnClickListener(View.OnClickListener {  v ->
            friendRequestsList.get(friendRequestHolder.getAdapterPosition()).getFriendId()?.let {
                db.collection("friendRequests")
                    .document(it)
                    .delete()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            db.collection("friendRequests").document(
                                friendRequestsList.get(friendRequestHolder.getAdapterPosition()).getFriendId()!!
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
                            friendRequestsList.remove(friendRequestsList.get(friendRequestHolder.getAdapterPosition()))
                            friendRequestHolder.layout.removeAllViews()
                            Toast.makeText(v.context, "Friend request is deleted!", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(
                                v.context,
                                "Error deleting post!",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.w(
                                "TAG",
                                "Error deleting documentAAAAAAAAAAAAAAAAAAAAAAAAA"
                            )
                        }
                    }
            }
        })
    }

    override fun getItemCount(): Int {
        return friendRequestsList.size
    }

    init {
        this.friendRequestsList = friendRequestsList
    }
}

class MyFriendRequestHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var friendImage: CircleImageView
    var friendUsername: TextView
    var accept: ImageView
    var decline: ImageView
    var layout: LinearLayout

    init {
        friendImage = itemView.findViewById(R.id.requests_friends_profile_picture)
        friendUsername = itemView.findViewById(R.id.requests_friends_username_value)
        accept = itemView.findViewById(R.id.requests_friends_accept_friend)
        decline = itemView.findViewById(R.id.requests_friends_decline_friend)
        layout = itemView.findViewById(R.id.custom_grid_requests_friends)
    }
}