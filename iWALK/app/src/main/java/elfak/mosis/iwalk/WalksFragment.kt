package elfak.mosis.iwalk

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class WalksFragment : Fragment() {

    var adapterInProgressWalks: AdapterInProgressWalks? = null
    private val docRef = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    var recyclerViewInProgress: RecyclerView? = null
    var walksInProgress: Walks? = null
    var walksInProgressList: ArrayList<Walks>? = null

    var adapterFinishedWalks: AdapterFinishedWalks? = null
    var recyclerViewFinished: RecyclerView? = null
    var walksFinished: Walks? = null
    var walksFinishedList: ArrayList<Walks>? = null

    private val postsRef = docRef.collection("posts")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myWalks: TextView = requireView().findViewById<TextView>(R.id.my_walks_tab_walks)
        val mapWalks: TextView = requireView().findViewById<TextView>(R.id.map_walks_tab_walks)

        walksInProgressList = ArrayList<Walks>()
        walksFinishedList = ArrayList<Walks>()
        auth = Firebase.auth

        recyclerViewInProgress = view.findViewById<View>(R.id.active_walks_recycler) as RecyclerView
        postsRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        if (document.getString("userId") == auth.currentUser?.uid && document.getString("status") == "IN_PROGRESS") {
                            val gridLayoutManager = GridLayoutManager(context, 1)
                            recyclerViewInProgress!!.setLayoutManager(gridLayoutManager)
                            walksInProgress = Walks(
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
                            walksInProgressList!!.add(walksInProgress!!)
                            adapterInProgressWalks = context?.let { AdapterInProgressWalks(it, walksInProgressList!!) }
                            recyclerViewInProgress!!.setAdapter(adapterInProgressWalks)
                        }
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.exception)
                }
            }.addOnFailureListener { e ->
                Toast.makeText(
                    this@WalksFragment.context,
                    "Error:" + e,
                    Toast.LENGTH_SHORT
                ).show()
            }

        recyclerViewFinished = view.findViewById<View>(R.id.old_walks_recycler) as RecyclerView
        postsRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        if (document.getString("userId") == auth.currentUser?.uid && document.getString("status") == "FINISHED") {
                            val gridLayoutManager = GridLayoutManager(context, 1)
                            recyclerViewFinished!!.setLayoutManager(gridLayoutManager)
                            walksFinished = Walks(
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
                            walksFinishedList!!.add(walksFinished!!)
                            adapterFinishedWalks = context?.let { AdapterFinishedWalks(it, walksFinishedList!!) }
                            recyclerViewFinished!!.setAdapter(adapterFinishedWalks)
                        }
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.exception)
                }
            }

        myWalks.setOnClickListener(View.OnClickListener {
            val fragment: Fragment = MyWalksFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.walks_fragment, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        })

        mapWalks.setOnClickListener(View.OnClickListener {
            val fragment: Fragment = MapWalksFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.walks_fragment, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_walks, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}