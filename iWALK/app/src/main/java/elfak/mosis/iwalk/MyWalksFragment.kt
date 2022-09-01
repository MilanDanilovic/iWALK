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

class MyWalksFragment : Fragment() {

    var adapterInProgressMyWalks: AdapterInProgressMyWalks? = null
    private val docRef = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    var recyclerViewInProgress: RecyclerView? = null
    var myWalksInProgress: Walks? = null
    var myWalksInProgressList: ArrayList<Walks>? = null

    var adapterFinishedMyWalks: AdapterFinishedMyWalks? = null
    var recyclerViewFinished: RecyclerView? = null
    var myWalksFinished: Walks? = null
    var myWalksFinishedList: ArrayList<Walks>? = null

    val usersRef: CollectionReference = docRef.collection("posts")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val walks: TextView = requireView().findViewById<TextView>(R.id.walks_tab_my_walks)

        myWalksInProgressList = ArrayList<Walks>()
        myWalksFinishedList = ArrayList<Walks>()
        auth = Firebase.auth

        val inProgressMyWalksRef: CollectionReference = docRef.collection("posts")
        recyclerViewInProgress = view.findViewById<View>(R.id.active_my_walks_recycler) as RecyclerView
        inProgressMyWalksRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        if (document.getString("walker") == auth.currentUser?.uid && document.getString("status") == "IN_PROGRESS") {
                            val gridLayoutManager = GridLayoutManager(context, 1)
                            recyclerViewInProgress!!.setLayoutManager(gridLayoutManager)
                            myWalksInProgress = Walks(
                                document.id,
                                document.getString("description"),
                                document.getString("date"),
                                document.getString("time"),
                                document.getString("userId"),
                                document.getString("dogImage1Url"),
                                document.getString("dogImage2Url"),
                                document.getString("status"),
                                document.getString("walkerId")
                            )
                            myWalksInProgressList!!.add(myWalksInProgress!!)
                            adapterInProgressMyWalks = context?.let { AdapterInProgressMyWalks(it, myWalksInProgressList!!) }
                            recyclerViewInProgress!!.setAdapter(adapterInProgressMyWalks)
                        }
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.exception)
                }
            }

        val finishedMyWalksRef: CollectionReference = docRef.collection("posts")
        recyclerViewFinished = view.findViewById<View>(R.id.old_my_walks_recycler) as RecyclerView
        finishedMyWalksRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        if (document.getString("walker") == auth.currentUser?.uid && document.getString("status") == "FINISHED") {
                            val gridLayoutManager = GridLayoutManager(context, 1)
                            recyclerViewFinished!!.setLayoutManager(gridLayoutManager)
                            myWalksFinished = Walks(
                                document.id,
                                document.getString("description"),
                                document.getString("date"),
                                document.getString("time"),
                                document.getString("userId"),
                                document.getString("dogImage1Url"),
                                document.getString("dogImage2Url"),
                                document.getString("status"),
                                document.getString("walkerId")
                            )
                            myWalksFinishedList!!.add(myWalksFinished!!)
                            adapterFinishedMyWalks = context?.let { AdapterFinishedMyWalks(it, myWalksFinishedList!!) }
                            recyclerViewFinished!!.setAdapter(adapterFinishedMyWalks)
                        }
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.exception)
                }
            }

        walks.setOnClickListener(View.OnClickListener {
            val fragment: Fragment = WalksFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.my_walks_fragment, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_walks, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}