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

class PostsFragment : Fragment() {

    var adapterMyPosts: AdapterMyPosts? = null
    private val docRef = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    var recyclerView: RecyclerView? = null
    var post: Post? = null
    var postsList: ArrayList<Post>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postsList = ArrayList<Post>()
        auth = Firebase.auth
        val postsRef: CollectionReference = docRef.collection("posts")
        recyclerView = view.findViewById<View>(R.id.my_posts_recycler) as RecyclerView
        postsRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        if (document.getString("userId") == auth.currentUser?.uid && document.getString("status") == "OPEN") {
                            val gridLayoutManager = GridLayoutManager(context, 1)
                            recyclerView!!.setLayoutManager(gridLayoutManager)
                            post = Post(
                                document.id,
                                document.getString("description"),
                                document.getString("date"),
                                document.getString("time"),
                                document.getString("userId"),
                                document.getString("dogImage1Url"),
                                document.getString("dogImage2Url")
                            )
                            postsList!!.add(post!!)
                            adapterMyPosts = context?.let { AdapterMyPosts(it, postsList!!) }
                            recyclerView!!.setAdapter(adapterMyPosts)
                        }
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.exception)
                }
            }

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