package elfak.mosis.iwalk

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class MyWalksFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val walks: TextView = requireView().findViewById<TextView>(R.id.walks_tab_my_walks)

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