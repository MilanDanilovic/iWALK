package elfak.mosis.iwalk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONException
import org.json.JSONObject


class CustomGridFindFriendsFragment : Fragment() {

    private lateinit var username : TextView
    private lateinit var image : CircleImageView
    private lateinit var addFriend : ImageView
    private lateinit var addFriendBluetooth : ImageView
    private lateinit var CURRENT_STATE : String
    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        username = requireView().findViewById<TextView>(R.id.find_friends_username_value)
        image = requireView().findViewById<CircleImageView>(R.id.find_friends_profile_picture)
        addFriend = requireView().findViewById<ImageView>(R.id.find_friends_add_friend)
        addFriendBluetooth = requireView().findViewById<ImageView>(R.id.find_friends_add_friend_bluetooth)
        CURRENT_STATE = "not_friends"
        auth = Firebase.auth
        val friendRequestsRef: CollectionReference = db.collection("friends")

        val bundle = this.arguments
        if (bundle != null) {
            username.setText(bundle.getString("user_username"))
            Picasso.get().load(bundle.getString("user_image"))
                .into(image)
        }

        /*addFriend.setOnClickListener (View.OnClickListener {


        })*/
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