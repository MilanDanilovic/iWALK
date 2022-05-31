package elfak.mosis.iwalk

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class PostsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val newPosts: TextView = requireView().findViewById<TextView>(R.id.new_posts_tab_my_posts)
        newPosts.setOnClickListener(View.OnClickListener {
            val fragment: Fragment = NewPostsFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.posts_fragment, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}