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

class RequestsFriendsFragment : Fragment() {

    var adapterFriendRequest: AdapterFriendRequest? = null
    private val docRef = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    var recyclerView: RecyclerView? = null
    var friend: Friend? = null
    var friendRequestsList: ArrayList<Friend>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val all: TextView = requireView().findViewById<TextView>(R.id.all_friends_tab_requests)
        val find: TextView = requireView().findViewById<TextView>(R.id.find_friends_tab_requests)

        friendRequestsList = ArrayList<Friend>()
        auth = Firebase.auth
        val friendRequestRef: CollectionReference = docRef.collection("friendRequests")
        recyclerView = view.findViewById<View>(R.id.requests_friends_recycler) as RecyclerView
        friendRequestRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        if (document.getString("receiver") == auth.currentUser?.uid) {
                            val gridLayoutManager = GridLayoutManager(context, 1)
                            recyclerView!!.setLayoutManager(gridLayoutManager)
                            friend = Friend(
                                document.id,
                                document.getString("senderUsername"),
                                document.getString("senderImage")
                            )
                            friendRequestsList!!.add(friend!!)
                            adapterFriendRequest = context?.let { AdapterFriendRequest(it, friendRequestsList!!) }
                            recyclerView!!.setAdapter(adapterFriendRequest)
                        }
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.exception)
                }
            }


        all.setOnClickListener(View.OnClickListener {
            val fragment: Fragment = AllFriendsFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.requests_friends_fragment, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        })

        find.setOnClickListener(View.OnClickListener {
            val fragment: Fragment = FindFriendsFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.requests_friends_fragment, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_requests_friends, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}