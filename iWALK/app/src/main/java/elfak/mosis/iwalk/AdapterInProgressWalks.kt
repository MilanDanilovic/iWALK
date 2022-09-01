package elfak.mosis.iwalk

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso


class AdapterInProgressWalks(var ctx: Context, walksList: MutableList<Walks>) :
    RecyclerView.Adapter<WalksHolder>() {

    lateinit var walksList: MutableList<Walks>
    var baseAuth: FirebaseAuth? = null
    private val docRef = FirebaseFirestore.getInstance()
    var popUp: PopupWindow? = null
    var click = true

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): WalksHolder {
        val mView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.custom_grid_in_progress_walks, viewGroup, false)
        return WalksHolder(mView)
    }

    override fun onBindViewHolder(walksHolder: WalksHolder, position: Int) {
        walksHolder.postDescription.setText(walksList[position].getPostDescription())
        walksHolder.postDate.setText(walksList[position].getPostDate())
        walksHolder.postTime.setText(walksList[position].getPostTime())
        Picasso.get().load(walksList[position].getPostDogImage1())
            .into(walksHolder.postDogImage1)
        Picasso.get().load(walksList[position].getPostDogImage2())
            .into(walksHolder.postDogImage2)

        walksList.get(walksHolder.adapterPosition).getPostWalkerId()?.let {
            docRef.collection("users")
                .get()
                .addOnCompleteListener{ task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            if (document.id == it) {
                                walksHolder.walkerUsername.setText(document.getString("username"))
                            }
                        }
                    }
                    else {
                        Log.d("TAG", "Error getting documents: ", task.exception)
                    }
                }
        }

        walksHolder.finishWalk.setOnClickListener(View.OnClickListener { v ->

            val documentReference = walksList.get(walksHolder.adapterPosition).getPostId()?.let { it -> docRef.collection("posts").document(it) }
            documentReference?.update(
                "status", "FINISHED"
            )?.addOnCompleteListener(OnCompleteListener<Void?> { task ->
                if (task.isSuccessful) {

                    popUp = PopupWindow(ctx)
                    val layout = LinearLayout(ctx)
                    val mainLayout = LinearLayout(ctx)
                    val gradeLabel = TextView(ctx)
                    gradeLabel.setText("Select grade for walker")

                    /*val fragment: Fragment = WalksFragment()
                    val fragmentManager = (ctx as FragmentActivity).supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(
                        R.id.walks_fragment,
                        fragment
                    )
                    fragmentTransaction.addToBackStack(
                        null
                    )
                    fragmentTransaction.commit()*/

                    Toast.makeText(
                        ctx,
                        "Data successfully updated.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        ctx,
                        "Error updating data.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })?.addOnFailureListener(OnFailureListener { })



            /*

            val but = Button(this)
            but.text = "Click Me"
            but.setOnClickListener(object : OnClickListener() {
                fun onClick(v: View?) {
                    if (click) {
                        popUp.showAtLocation(layout, Gravity.BOTTOM, 10, 10)
                        popUp.update(50, 50, 300, 80)
                        click = false
                    } else {
                        popUp.dismiss()
                        click = true
                    }
                }
            })*/
            })



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