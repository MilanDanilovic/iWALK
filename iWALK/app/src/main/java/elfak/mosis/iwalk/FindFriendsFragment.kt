package elfak.mosis.iwalk

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import kotlin.properties.Delegates


class FindFriendsFragment : Fragment() {

    private lateinit var search : EditText
    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val all: TextView = requireView().findViewById<TextView>(R.id.all_friends_tab_find)
        val requests: TextView = requireView().findViewById<TextView>(R.id.request_friends_tab_find)
        val findButton: Button = requireView().findViewById<Button>(R.id.find_friend_button)
        search = requireView().findViewById<EditText>(R.id.find_friend_input)

        val usersRef: CollectionReference = db.collection("users")
        auth = Firebase.auth

        all.setOnClickListener(View.OnClickListener {
            val fragment: Fragment = AllFriendsFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.find_friends_fragment, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        })

        requests.setOnClickListener(View.OnClickListener {
            val fragment: Fragment = RequestsFriendsFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.find_friends_fragment, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        })

        findButton.setOnClickListener(View.OnClickListener {
            usersRef.get()
                .addOnCompleteListener { task ->
                    var counter = 0
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            val searchInput = search.text.toString()
                            if (document["username"].toString() == searchInput) {
                                counter = 1
                                val activity= context as AppCompatActivity
                                val customFindFriendFragment= CustomGridFindFriendsFragment()
                                val bundle = Bundle()
                                bundle.putString("user_username", document.getString("username"))
                                bundle.putString("user_image", document.getString("profileImageUrl"))
                                customFindFriendFragment.setArguments(bundle)
                                activity.supportFragmentManager.beginTransaction().replace(R.id.linear_layout_replace, customFindFriendFragment).commit()
                                Log.d("TAG", "user exist")
                            }
                        }
                    } else {
                        Log.d("TAG", "Error getting documents: ", task.exception)
                    }
                    if (counter == 0 ) {
                        val linearLayoutReplace: LinearLayout = requireView().findViewById<LinearLayout>(R.id.linear_layout_replace)
                        linearLayoutReplace.removeAllViews()
                        Toast.makeText(
                            context,
                            "User does not exist! ",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("TAG", "user does not exist")
                    }
                }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find_friends, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}