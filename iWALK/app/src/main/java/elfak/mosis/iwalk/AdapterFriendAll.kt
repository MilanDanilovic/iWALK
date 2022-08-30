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

class AdapterFriendAll (var ctx: Context, allFriendsList: MutableList<Friend>) :
    RecyclerView.Adapter<AllFriendsHolder>() {

    lateinit var allFriendsList: MutableList<Friend>
    var baseAuth: FirebaseAuth? = null
    private val docRef = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): AllFriendsHolder {
        val mView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.custom_grid_all_friends, viewGroup, false)
        return AllFriendsHolder(mView)
    }

    override fun onBindViewHolder(allFriendsHolder: AllFriendsHolder, position: Int) {
        allFriendsHolder.friendUsername.setText(allFriendsList[position].getFriendUsername())
        Picasso.get().load(allFriendsList[position].getFriendImage())
            .into(allFriendsHolder.friendImage)
        baseAuth = FirebaseAuth.getInstance()
    }

    override fun getItemCount(): Int {
        return allFriendsList.size
    }

    init {
        this.allFriendsList = allFriendsList
    }
}

class AllFriendsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var friendImage: CircleImageView
    var friendUsername: TextView
    var delete: ImageView

    init {
        friendImage = itemView.findViewById(R.id.all_friends_profile_picture)
        friendUsername = itemView.findViewById(R.id.all_friends_username_value)
        delete = itemView.findViewById(R.id.all_friends_delete_friend)
    }
}
