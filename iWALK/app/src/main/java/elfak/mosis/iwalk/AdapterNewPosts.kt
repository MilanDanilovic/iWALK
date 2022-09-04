package elfak.mosis.iwalk

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class AdapterNewPosts(var ctx: Context, postsList: MutableList<Post>) :
    RecyclerView.Adapter<NewPostHolder>() {

    lateinit var postsList: MutableList<Post>
    var baseAuth: FirebaseAuth? = null
    private val docRef = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): NewPostHolder {
        val mView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.custom_grid_new_posts, viewGroup, false)
        return NewPostHolder(mView)
    }

    override fun onBindViewHolder(newPostHolder: NewPostHolder, position: Int) {
        val postText: String = postsList[position].getPostDescription() + "\n" + postsList[position].getPostDate() + " " + postsList[position].getPostTime()
        newPostHolder.postDescription.setText(postText)
        if (URLUtil.isValidUrl(postsList[position].getPostDogImage1())) {
            Picasso.get().load(postsList[position].getPostDogImage1())
                .into(newPostHolder.postDogImage1)
        }
        if (URLUtil.isValidUrl(postsList[position].getPostDogImage2())) {
            Picasso.get().load(postsList[position].getPostDogImage2())
                .into(newPostHolder.postDogImage2)
        }

        baseAuth = FirebaseAuth.getInstance()

        newPostHolder.acceptPost.setOnClickListener(View.OnClickListener { v ->

            postsList.get(newPostHolder.adapterPosition).getPostId()?.let {
                val documentReference = it?.let { it1 -> docRef.collection("posts").document(it1) }
                documentReference?.update(
                    "status", "IN_PROGRESS",
                    "walkerId", baseAuth?.currentUser?.uid
                )?.addOnCompleteListener(OnCompleteListener<Void?> { task ->
                    if (task.isSuccessful) {
                        val fragment: Fragment = NewPostsFragment()
                        val fragmentManager = (ctx as FragmentActivity).supportFragmentManager
                        val fragmentTransaction = fragmentManager.beginTransaction()
                        fragmentTransaction.replace(
                            R.id.new_posts_fragment,
                            fragment
                        )
                        fragmentTransaction.addToBackStack(
                            null
                        )
                        fragmentTransaction.commit()
                        Toast.makeText(
                            ctx,
                            "Post accepted!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            ctx,
                            "Error accepting post!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })?.addOnFailureListener(OnFailureListener { })
            }


        })
    }

    override fun getItemCount(): Int {
        return postsList.size
    }

    init {
        this.postsList = postsList
    }
}

class NewPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var postDescription: TextView
    var postDogImage1: ImageView
    var postDogImage2: ImageView
    var acceptPost: ImageView

    init {
        postDescription = itemView.findViewById(R.id.new_post_description_value)
        postDogImage1 = itemView.findViewById(R.id.new_posts_dog1_pictue_value)
        postDogImage2 = itemView.findViewById(R.id.new_posts_dog2_picture_value)
        acceptPost = itemView.findViewById(R.id.new_posts_accept)
    }
}