package elfak.mosis.iwalk

import android.content.Context
import android.util.Log
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

class AdapterFinishedWalks(var ctx: Context, walksList: MutableList<Walks>) :
    RecyclerView.Adapter<FinishedWalksHolder>() {

    lateinit var walksList: MutableList<Walks>
    var baseAuth: FirebaseAuth? = null
    private val docRef = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): FinishedWalksHolder {
        val mView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.custom_grid_finished_walks, viewGroup, false)
        return FinishedWalksHolder(mView)
    }

    override fun onBindViewHolder(finishedWalksHolder: FinishedWalksHolder, position: Int) {
        finishedWalksHolder.postDate.setText(walksList[position].getPostDate())
        finishedWalksHolder.postTime.setText(walksList[position].getPostTime())
        Picasso.get().load(walksList[position].getPostDogImage1())
            .into(finishedWalksHolder.postDogImage1)
        Picasso.get().load(walksList[position].getPostDogImage2())
            .into(finishedWalksHolder.postDogImage2)

        walksList.get(finishedWalksHolder.adapterPosition).getPostWalkerId()?.let {
            docRef.collection("users")
                .get()
                .addOnCompleteListener{ task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            if (document.id == it) {
                                finishedWalksHolder.walkerUsername.setText(document.getString("username"))
                            }
                        }
                    }
                    else {
                        Log.d("TAG", "Error getting documents: ", task.exception)
                    }
                }
        }


    }

    override fun getItemCount(): Int {
        return walksList.size
    }

    init {
        this.walksList = walksList
    }
}

class FinishedWalksHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var postDate: TextView
    var postTime: TextView
    var postDogImage1: ImageView
    var postDogImage2: ImageView
    var walkerUsername: TextView

    init {
        postDate = itemView.findViewById(R.id.finished_walks_date_value)
        postTime = itemView.findViewById(R.id.finished_walks_time_value)
        postDogImage1 = itemView.findViewById(R.id.finished_walks_dog1_pictue_value)
        postDogImage2 = itemView.findViewById(R.id.finished_walks_dog2_picture_value)
        walkerUsername = itemView.findViewById(R.id.finished_walks_walker_username_value)
    }
}