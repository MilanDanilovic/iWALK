package elfak.mosis.iwalk

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class AdapterInProgressWalks(var ctx: Context, walksList: MutableList<Walks>) :
    RecyclerView.Adapter<WalksHolder>() {

    lateinit var walksList: MutableList<Walks>
    var baseAuth: FirebaseAuth? = null
    private val docRef = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): WalksHolder {
        val mView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.custom_grid_in_progress_walks, viewGroup, false)
        return WalksHolder(mView)
    }

    override fun onBindViewHolder(walksHolder: WalksHolder, position: Int) {
        walksHolder.postDescription.setText(walksList[position].getPostDescription())
        walksHolder.postDate.setText(walksList[position].getPostDate())
        walksHolder.postTime.setText(walksList[position].getPostTime())
        walksHolder.walkerUsername.setText(walksList[position].getPostWalkerUsername())
        Picasso.get().load(walksList[position].getPostDogImage1())
            .into(walksHolder.postDogImage1)
        Picasso.get().load(walksList[position].getPostDogImage2())
            .into(walksHolder.postDogImage2)
    }

    override fun getItemCount(): Int {
        return walksList.size
    }

    init {
        this.walksList = walksList
    }
}

class WalksHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var postDescription: TextView
    var postDate: TextView
    var postTime: TextView
    var postDogImage1: ImageView
    var postDogImage2: ImageView
    var finishWalk: Button
    var walkerUsername: TextView

    init {
        postDescription = itemView.findViewById(R.id.in_progress_walks_description_value)
        postDate = itemView.findViewById(R.id.in_progress_walks_date_value)
        postTime = itemView.findViewById(R.id.in_progress_walks_time_value)
        postDogImage1 = itemView.findViewById(R.id.in_progress_walks_dog1_pictue_value)
        postDogImage2 = itemView.findViewById(R.id.in_progress_walks_dog2_picture_value)
        finishWalk = itemView.findViewById(R.id.in_progress_walks_cancel_button)
        walkerUsername = itemView.findViewById(R.id.in_progress_walks_walker_username_value)
    }
}