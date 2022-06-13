package elfak.mosis.iwalk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlin.reflect.typeOf


class FindFriendsFragment : Fragment() {

    private lateinit var search : EditText
    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    var recyclerView: RecyclerView? = null
    var friend: Friend? = null
    var friendList: ArrayList<Friend>? = null
    var adapterFindFriends: AdapterFindFriends? = null

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
        val query: Query = usersRef.whereEqualTo("username", search.getText().toString())

        val searchInput = search.text.toString()

        friendList = ArrayList<Friend>()
        auth = Firebase.auth
        recyclerView = view.findViewById<View>(R.id.find_friends_recycler) as RecyclerView

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
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            //val pom: String = document["username"].toString()
                            if (document["username"].toString() == searchInput) {
                                val gridLayoutManager = GridLayoutManager(context, 1)
                                recyclerView!!.setLayoutManager(gridLayoutManager)
                                friend = Friend(
                                    document.id,
                                    document.getString("username"),
                                    document.getString("profileImageUrl")
                                )
                                friendList!!.add(friend!!)
                                adapterFindFriends = context?.let { AdapterFindFriends(it, friendList!!) }
                                recyclerView!!.setAdapter(adapterFindFriends)
                            }
                            Log.d("TAG", "user exist")
                        }
                    } else {
                        Log.d("TAG", "Error getting documents: ", task.exception)
                    }
                    if (task.result.size() == 0) {
                        Toast.makeText(
                            context,
                            "User does not exist! ",
                            Toast.LENGTH_SHORT
                        ).show()
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