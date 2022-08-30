package elfak.mosis.iwalk

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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
        Picasso.get().load(postsList[position].getPostDogImage1())
            .into(newPostHolder.postDogImage1)
        Picasso.get().load(postsList[position].getPostDogImage2())
            .into(newPostHolder.postDogImage2)

        baseAuth = FirebaseAuth.getInstance()
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