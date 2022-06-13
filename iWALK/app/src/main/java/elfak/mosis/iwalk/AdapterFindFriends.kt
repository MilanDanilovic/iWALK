package elfak.mosis.iwalk

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AdapterFindFriends(var ctx: Context, findFriendsList: MutableList<Friend>) :
    RecyclerView.Adapter<FindFriendsHolder>() {

    lateinit var findFriendsList: MutableList<Friend>
    var baseAuth: FirebaseAuth? = null
    private val docRef = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): FindFriendsHolder {
        val mView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.custom_grid_pets, viewGroup, false)
        return FindFriendsHolder(mView)
    }

    override fun onBindViewHolder(findFriendsHolder: FindFriendsHolder, position: Int) {
        findFriendsHolder.username.setText(findFriendsList[position].getFriendUsername())
        Picasso.get().load(findFriendsList[position].getFriendImage())
            .into(findFriendsHolder.userImage)
        baseAuth = FirebaseAuth.getInstance()
        /*findFriendsHolder.addFriend.setOnClickListener(View.OnClickListener {

        })*/
    }

    override fun getItemCount(): Int {
        return findFriendsList.size
    }

    init {
        this.findFriendsList = findFriendsList
    }
}

class FindFriendsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var userImage: CircleImageView
    var username: TextView
    var addFriend: Button
    var addFriendBluetooth: Button

    init {
        userImage = itemView.findViewById(R.id.find_friends_profile_picture)
        username = itemView.findViewById(R.id.find_friends_username_value)
        addFriend = itemView.findViewById(R.id.find_friends_add_friend)
        addFriendBluetooth = itemView.findViewById(R.id.find_friends_add_friend_bluetooth)
    }
}