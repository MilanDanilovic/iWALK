package elfak.mosis.iwalk

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class FavouriteWalkersFragment : Fragment() {

    var adapterFavouriteWalkers: AdapterFavouriteWalkers? = null
    private val docRef = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    var recyclerView: RecyclerView? = null
    var favouriteWalkers: FavouriteWalker? = null
    var favouriteWalkersList: ArrayList<FavouriteWalker>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favouriteWalkersList = ArrayList<FavouriteWalker>()
        auth = Firebase.auth
        val usersRef: CollectionReference = docRef.collection("users")
        val friendsRef: CollectionReference = docRef.collection("users")
        recyclerView = view.findViewById<View>(R.id.favourite_walkers_recycler) as RecyclerView

        usersRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        if (document.id == auth.currentUser?.uid) {
                            if (document["favourites"] != null) {
                                var favourites = arrayListOf<String>()
                                favourites = document["favourites"] as ArrayList<String>

                                friendsRef.get()
                                    .addOnCompleteListener { taskFriends ->
                                        if (taskFriends.isSuccessful) {
                                            for (documentFriend in taskFriends.result) {
                                                if (favourites.contains(documentFriend.id)) {
                                                    val gridLayoutManager = GridLayoutManager(context, 1)
                                                    recyclerView!!.setLayoutManager(gridLayoutManager)
                                                    favouriteWalkers = FavouriteWalker(
                                                        documentFriend.getString("profileImageUrl"),
                                                        documentFriend.getString("username"),
                                                        documentFriend.id,
                                                    )
                                                    favouriteWalkersList!!.add(favouriteWalkers!!)
                                                    adapterFavouriteWalkers = context?.let { AdapterFavouriteWalkers(it, favouriteWalkersList!!) }
                                                    recyclerView!!.setAdapter(adapterFavouriteWalkers)
                                                }
                                            }
                                        } else {
                                            Log.d("TAG", "Error getting documents: ", task.exception)
                                        }
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
        return inflater.inflate(R.layout.fragment_favourite_walkers, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}