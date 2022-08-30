package elfak.mosis.iwalk

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class AllFriendsFragment : Fragment() {

    var adapterAllFriends: AdapterFriendAll? = null
    private val docRef = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    var recyclerView: RecyclerView? = null
    var friend: Friend? = null
    var friendsList: ArrayList<Friend>? = null
    var flag: String? = null
    val usersRef: CollectionReference = docRef.collection("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val requests: TextView = requireView().findViewById<TextView>(R.id.request_friends_tab_all)
        val find: TextView = requireView().findViewById<TextView>(R.id.find_friends_tab_all)

        friendsList = ArrayList<Friend>()
        auth = Firebase.auth

        recyclerView = view?.findViewById<View>(R.id.all_friends_recycler) as RecyclerView

        requests.setOnClickListener(View.OnClickListener {
            val fragment: Fragment = RequestsFriendsFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.all_friends_fragment, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        })

        find.setOnClickListener(View.OnClickListener {
            val fragment: Fragment = FindFriendsFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.all_friends_fragment, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        })

        usersRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        if (document.id != auth.currentUser?.uid) {
                            if (document["friends"] != null) {
                                var friends = arrayListOf<String>()
                                friends = document["friends"] as ArrayList<String>
                                for (friend in friends) {
                                    if (friend.equals(auth.currentUser?.uid)) {
                                        flag = "true"
                                        break
                                    }
                                    else {
                                        flag = "false"
                                    }
                                }
                            if (flag == "true") {
                                val gridLayoutManager = GridLayoutManager(context, 1)
                                recyclerView!!.setLayoutManager(gridLayoutManager)
                                friend = Friend(
                                    document.id,
                                    document.getString("username"),
                                    document.getString("profileImageUrl")
                                )
                                friendsList!!.add(friend!!)
                                adapterAllFriends = context?.let { AdapterFriendAll(it, friendsList!!) }
                                recyclerView!!.setAdapter(adapterAllFriends)
                            }
                            }

                        }
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.exception)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_friends, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}