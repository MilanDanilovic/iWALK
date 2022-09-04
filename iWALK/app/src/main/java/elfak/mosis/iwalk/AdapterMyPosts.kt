package elfak.mosis.iwalk

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class AdapterMyPosts(var ctx: Context, postsList: MutableList<Post>) :
    RecyclerView.Adapter<MyPostHolder>() {

    lateinit var postsList: MutableList<Post>
    var baseAuth: FirebaseAuth? = null
    private val docRef = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyPostHolder {
        val mView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.custom_grid_my_posts, viewGroup, false)
        return MyPostHolder(mView)
    }

    override fun onBindViewHolder(myPostHolder: MyPostHolder, position: Int) {
        myPostHolder.postDescription.setText(postsList[position].getPostDescription())
        myPostHolder.postDate.setText(postsList[position].getPostDate())
        myPostHolder.postTime.setText(postsList[position].getPostTime())
        if (URLUtil.isValidUrl(postsList[position].getPostDogImage1())) {
            Picasso.get().load(postsList[position].getPostDogImage1())
                .into(myPostHolder.postDogImage1)
        }
        if (URLUtil.isValidUrl(postsList[position].getPostDogImage2())) {
            Picasso.get().load(postsList[position].getPostDogImage2())
                .into(myPostHolder.postDogImage2)
        }

        baseAuth = FirebaseAuth.getInstance()

        myPostHolder.postDelete.setOnClickListener(View.OnClickListener { v ->
            val alertDialog = AlertDialog.Builder(v.context, R.style.Theme_PopUpDialog)
            alertDialog.setMessage("Are you sure you want to delete post?")

            alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                val fragment: Fragment = PostsFragment()
                val activity= v!!.context as AppCompatActivity
                val fragmentManager: FragmentManager = activity.supportFragmentManager
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.posts_fragment, fragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()

                postsList.get(myPostHolder.getAdapterPosition()).getPostId()?.let {
                    docRef.collection("posts")
                        .document(it)
                        .delete()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                docRef.collection("posts").document(
                                    postsList.get(myPostHolder.getAdapterPosition()).getPostId()!!
                                )
                                    .delete()
                                    .addOnSuccessListener {
                                        Log.d(
                                            "TAG",
                                            "DocumentSnapshot successfully deleted!"
                                        )
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(
                                            "TAG",
                                            "Error deleting documentAAAAAAAAAAAAAAAAAAAAAAAAA",
                                            e
                                        )
                                    }
                                postsList.remove(postsList.get(myPostHolder.getAdapterPosition()))
                                Toast.makeText(v.context, "Post is deleted!", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(
                                    v.context,
                                    "Error deleting post!",
                                    Toast.LENGTH_LONG
                                ).show()
                                Log.w(
                                    "TAG",
                                    "Error deleting documentAAAAAAAAAAAAAAAAAAAAAAAAA"
                                )
                            }
                        }
                }
            })
            alertDialog.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            alertDialog.show()

        })
    }

    override fun getItemCount(): Int {
        return postsList.size
    }

    init {
        this.postsList = postsList
    }


}

class MyPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var postDescription: TextView
    var postDate: TextView
    var postTime: TextView
    var postDogImage1: ImageView
    var postDogImage2: ImageView
    var postDelete: ImageView

    init {
        postDescription = itemView.findViewById(R.id.my_post_description_value)
        postDate = itemView.findViewById(R.id.my_post_date_value)
        postTime = itemView.findViewById(R.id.my_post_time_value)
        postDogImage1 = itemView.findViewById(R.id.my_posts_dog1_pictue_value)
        postDogImage2 = itemView.findViewById(R.id.my_posts_dog2_picture_value)
        postDelete = itemView.findViewById(R.id.my_posts_delete)
    }
}