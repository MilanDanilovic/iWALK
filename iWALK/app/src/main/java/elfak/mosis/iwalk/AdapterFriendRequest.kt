package elfak.mosis.iwalk

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AdapterFriendRequest (var ctx: Context, friendRequestsList: MutableList<Friend>) :
    RecyclerView.Adapter<MyFriendRequestHolder>() {

    lateinit var friendRequestsList: MutableList<Friend>
    var baseAuth: FirebaseAuth? = null
    private val docRef = FirebaseFirestore.getInstance()

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

    init {
        friendImage = itemView.findViewById(R.id.requests_friends_profile_picture)
        friendUsername = itemView.findViewById(R.id.requests_friends_username_value)
        accept = itemView.findViewById(R.id.requests_friends_accept_friend)
        decline = itemView.findViewById(R.id.requests_friends_decline_friend)
    }
}