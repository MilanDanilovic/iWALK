package elfak.mosis.iwalk

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class AdapterFinishedMyWalks(var ctx: Context, walksList: MutableList<Walks>) :
    RecyclerView.Adapter<FinishedMyWalksHolder>() {

    lateinit var walksList: MutableList<Walks>
    var baseAuth: FirebaseAuth? = null
    private val docRef = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): FinishedMyWalksHolder {
        val mView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.custom_grid_finished_my_walks, viewGroup, false)
        return FinishedMyWalksHolder(mView)
    }

    override fun onBindViewHolder(finishedMyWalksHolder: FinishedMyWalksHolder, position: Int) {
        finishedMyWalksHolder.postDate.setText(walksList[position].getPostDate())
        finishedMyWalksHolder.postTime.setText(walksList[position].getPostTime())
        if (URLUtil.isValidUrl(walksList[position].getPostDogImage1())) {
            Picasso.get().load(walksList[position].getPostDogImage1())
                .into(finishedMyWalksHolder.postDogImage1)
        }
        if (URLUtil.isValidUrl(walksList[position].getPostDogImage2())) {
            Picasso.get().load(walksList[position].getPostDogImage2())
                .into(finishedMyWalksHolder.postDogImage2)
        }

    }

    override fun getItemCount(): Int {
        return walksList.size
    }

    init {
        this.walksList = walksList
    }
}

class FinishedMyWalksHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var postDate: TextView
    var postTime: TextView
    var postDogImage1: ImageView
    var postDogImage2: ImageView

    init {
        postDate = itemView.findViewById(R.id.my_finished_walks_date_value)
        postTime = itemView.findViewById(R.id.my_finished_walks_time_value)
        postDogImage1 = itemView.findViewById(R.id.my_finished_walks_dog1_pictue_value)
        postDogImage2 = itemView.findViewById(R.id.my_finished_walks_dog2_picture_value)
    }
}