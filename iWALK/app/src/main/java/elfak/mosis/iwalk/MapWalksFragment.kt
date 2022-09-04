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

class MapWalksFragment : Fragment() {

    var adapterInProgressMyWalks: AdapterMapMyWalks? = null
    private val docRef = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    var recyclerViewInProgressMyWalks: RecyclerView? = null
    var myWalksInProgress: Walks? = null
    var myWalksInProgressList: ArrayList<Walks>? = null

    var adapterInProgressWalks: AdapterMapWalks? = null
    var recyclerViewInProgressWalks: RecyclerView? = null
    var walksInProgress: Walks? = null
    var walksInProgressList: ArrayList<Walks>? = null

    private val markersRef = docRef.collection("markers")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val walks: TextView = requireView().findViewById<TextView>(R.id.walks_tab_map_walks)
        val myWalks: TextView = requireView().findViewById<TextView>(R.id.my_walks_tab_map_walks)

        myWalksInProgressList = ArrayList<Walks>()
        walksInProgressList = ArrayList<Walks>()
        auth = Firebase.auth

        recyclerViewInProgressMyWalks = view.findViewById<View>(R.id.my_walks_map_walks_recycler) as RecyclerView
        markersRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        if (document.getString("walkerId") == auth.currentUser?.uid && document.getString("status") == "IN_PROGRESS") {
                            val gridLayoutManager = GridLayoutManager(context, 1)
                            recyclerViewInProgressMyWalks!!.setLayoutManager(gridLayoutManager)
                            myWalksInProgress = Walks(
                                document.id,
                                document.getString("description"),
                                document.getString("date"),
                                document.getString("time"),
                                document.getString("userId"),
                                document.getString("dogImage1Url"),
                                document.getString("status"),
                                document.getString("walkerId")
                            )
                            myWalksInProgressList!!.add(myWalksInProgress!!)
                            adapterInProgressMyWalks = context?.let { AdapterMapMyWalks(it, myWalksInProgressList!!) }
                            recyclerViewInProgressMyWalks!!.setAdapter(adapterInProgressMyWalks)
                        }
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.exception)
                }
            }.addOnFailureListener { e ->
                Toast.makeText(
                    this@MapWalksFragment.context,
                    "Error:" + e,
                    Toast.LENGTH_SHORT
                ).show()
            }

        recyclerViewInProgressWalks = view.findViewById<View>(R.id.walks_map_walks_recycler) as RecyclerView
        markersRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        if (document.getString("userId") == auth.currentUser?.uid && document.getString("status") == "IN_PROGRESS") {
                            val gridLayoutManager = GridLayoutManager(context, 1)
                            recyclerViewInProgressWalks!!.setLayoutManager(gridLayoutManager)
                            walksInProgress = Walks(
                                document.id,
                                document.getString("description"),
                                document.getString("date"),
                                document.getString("time"),
                                document.getString("userId"),
                                document.getString("dogImage1Url"),
                                document.getString("status"),
                                document.getString("walkerId")
                            )
                            walksInProgressList!!.add(walksInProgress!!)
                            adapterInProgressWalks = context?.let { AdapterMapWalks(it, walksInProgressList!!) }
                            recyclerViewInProgressWalks!!.setAdapter(adapterInProgressWalks)
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
            fragmentTransaction.replace(R.id.map_walks_fragment, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        })

        myWalks.setOnClickListener(View.OnClickListener {
            val fragment: Fragment = MyWalksFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.map_walks_fragment, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map_walks, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}