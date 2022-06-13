package elfak.mosis.iwalk

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CustomGridFindFriendsFragment : Fragment() {

    private lateinit var username : TextView
    private lateinit var image : CircleImageView
    private lateinit var addFriend : ImageView
    private lateinit var addFriendBluetooth : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        username = requireView().findViewById<TextView>(R.id.find_friends_username_value)
        image = requireView().findViewById<CircleImageView>(R.id.find_friends_profile_picture)
        addFriend = requireView().findViewById<ImageView>(R.id.find_friends_add_friend)
        addFriendBluetooth = requireView().findViewById<ImageView>(R.id.find_friends_add_friend_bluetooth)

        val bundle = this.arguments
        if (bundle != null) {
            username.setText(bundle.getString("user_username"))
            Picasso.get().load(bundle.getString("user_image"))
                .into(image)
        }
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