package elfak.mosis.iwalk

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myWalks: TextView = requireView().findViewById<TextView>(R.id.my_walks_tab_walks)

        myWalks.setOnClickListener(View.OnClickListener {
            val fragment: Fragment = MyWalksFragment()
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