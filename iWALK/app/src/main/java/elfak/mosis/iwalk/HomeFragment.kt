package elfak.mosis.iwalk

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {

    var adapterTopWalkers: AdapterTopWalkers? = null
    private val docRef = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    var recyclerView: RecyclerView? = null
    var topWalkers: TopWalkers? = null
    var topWalkersList: ArrayList<TopWalkers>? = null
    val usersRef = docRef.collection("users")
    var topWalkersForList: TopWalkers? = null
    val usersList: MutableList<TopWalkers> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        topWalkersList = ArrayList<TopWalkers>()
        auth = Firebase.auth
        recyclerView = view.findViewById<View>(R.id.top_walkers_recycler) as RecyclerView

        usersRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        if (document.id != auth.currentUser!!.uid) {
                            topWalkersForList = TopWalkers(
                                document.id,
                                document.getString("username"),
                                document.getString("profileImageUrl"),
                                document.get("score") as Number
                            )
                            usersList.add(topWalkersForList!!)
                            Log.d("TAG", "JANAAA opet ", task.exception)
                        }
                    }
                    usersList.sortByDescending { it.getWalkerScore()!!.toDouble() }
                    val usersListSorted = usersList.take(20).toTypedArray()

                    for (user in usersListSorted) {
                        val gridLayoutManager = GridLayoutManager(context, 1)
                        recyclerView!!.setLayoutManager(gridLayoutManager)
                        topWalkers = TopWalkers(
                            user.getWalkerId(),
                            user.getWalkerUsername(),
                            user.getWalkerImage(),
                            user.getWalkerScore() as Number
                        )
                        topWalkersList!!.add(topWalkers!!)
                        adapterTopWalkers = context?.let { AdapterTopWalkers(it, topWalkersList!!) }
                        recyclerView!!.setAdapter(adapterTopWalkers)
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
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}