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

class AdapterInProgressMyWalks(var ctx: Context, walksList: MutableList<Walks>) :
    RecyclerView.Adapter<InProgressMyWalksHolder>() {

    lateinit var walksList: MutableList<Walks>
    var baseAuth: FirebaseAuth? = null
    private val docRef = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): InProgressMyWalksHolder {
        val mView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.custom_grid_in_progress_my_walks, viewGroup, false)
        return InProgressMyWalksHolder(mView)
    }

    override fun onBindViewHolder(inProgressMyWalksHolder: InProgressMyWalksHolder, position: Int) {
        inProgressMyWalksHolder.postDescription.setText(walksList[position].getPostDescription())
        inProgressMyWalksHolder.postDate.setText(walksList[position].getPostDate())
        inProgressMyWalksHolder.postTime.setText(walksList[position].getPostTime())
        Picasso.get().load(walksList[position].getPostDogImage1())
            .into(inProgressMyWalksHolder.postDogImage1)
        Picasso.get().load(walksList[position].getPostDogImage2())
            .into(inProgressMyWalksHolder.postDogImage2)
    }

    override fun getItemCount(): Int {
        return walksList.size
    }

    init {
        this.walksList = walksList
    }
}

class InProgressMyWalksHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var postDescription: TextView
    var postDate: TextView
    var postTime: TextView
    var postDogImage1: ImageView
    var postDogImage2: ImageView
    var cancel: Button

    init {
        postDescription = itemView.findViewById(R.id.my_in_progress_walks_description_value)
        postDate = itemView.findViewById(R.id.my_in_progress_walks_date_value)
        postTime = itemView.findViewById(R.id.my_in_progress_walks_time_value)
        postDogImage1 = itemView.findViewById(R.id.my_in_progress_walks_dog1_pictue_value)
        postDogImage2 = itemView.findViewById(R.id.my_in_progress_walks_dog2_picture_value)
        cancel = itemView.findViewById(R.id.my_in_progress_walks_cancel_button)
    }
}