package elfak.mosis.iwalk

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.util.HashMap

class AdapterMapWalks(var ctx: Context, walksList: MutableList<Walks>) :
    RecyclerView.Adapter<MapWalksHolder>() {

    lateinit var walksList: MutableList<Walks>
    var baseAuth: FirebaseAuth? = null
    private val docRef = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MapWalksHolder {
        val mView: View = LayoutInflater.from(viewGroup.context)
        .inflate(R.layout.custom_grid_map_walks, viewGroup, false)
        return MapWalksHolder(mView)
    }

    override fun onBindViewHolder(mapWalksHolder: MapWalksHolder, position: Int) {
        mapWalksHolder.postDescription.setText(walksList[position].getPostDescription())
        mapWalksHolder.postDate.setText(walksList[position].getPostDate())
        mapWalksHolder.postTime.setText(walksList[position].getPostTime())
        if (URLUtil.isValidUrl(walksList[position].getPostDogImage1())) {
            Picasso.get().load(walksList[position].getPostDogImage1())
                .into(mapWalksHolder.postDogImage1)
        }

        walksList.get(mapWalksHolder.adapterPosition).getPostWalkerId()?.let {
            docRef.collection("users")
                .get()
                .addOnCompleteListener{ task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            if (document.id == it) {
                                mapWalksHolder.walkerUsername.setText(document.getString("username"))
                            }
                        }
                    }
                    else {
                        Log.d("TAG", "Error getting documents: ", task.exception)
                    }
                }
        }

        mapWalksHolder.finish.setOnClickListener(View.OnClickListener { v ->

            val documentReference = walksList.get(mapWalksHolder.adapterPosition).getPostId()?.let { it -> docRef.collection("markers").document(it) }
            documentReference?.update(
                "status", "FINISHED"
            )?.addOnCompleteListener(OnCompleteListener<Void?> { task ->
                if (task.isSuccessful) {
                    val placeFormView = LayoutInflater.from(ctx).inflate(R.layout.dialog_rate_user, null)
                    val dialog = AlertDialog.Builder(ctx)
                        .setTitle("Rate user")
                        .setView(placeFormView)
                        .setPositiveButton("Ok", null)
                        .setCancelable(false)
                        .show()

                    val userDocumentReference = docRef.collection("users")
                        .document(walksList[mapWalksHolder.adapterPosition].getPostWalkerId()!!)

                    val rateBar = placeFormView.findViewById<RatingBar>(R.id.rating_bar_user)

                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener{

                        val userReference = docRef.collection("users")
                            .document(walksList[mapWalksHolder.adapterPosition].getPostWalkerId()!!)

                        userDocumentReference.get()
                            .addOnCompleteListener { taskUser ->
                                if (taskUser.isSuccessful) {
                                    var score = taskUser.result.get("score")!! as Number
                                    var numberOfWalks = (taskUser.result.get("numberOfWalks")!! as Number).toDouble() + 1

                                    val avgScore = (score.toDouble()+rateBar.rating)/numberOfWalks
                                    val avgScoreTwoDecimal =
                                        (Math.round(avgScore * 100.0) / 100.0).toFloat()


                                    val dataToSave: MutableMap<String, Any> =
                                        HashMap()


                                    dataToSave["score"] = avgScoreTwoDecimal as Number
                                    dataToSave["numberOfWalks"] = numberOfWalks as Number


                                    userReference.update(dataToSave)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                            } else {
                                                Toast.makeText(
                                                    ctx,
                                                    "Error while updating data!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }.addOnFailureListener { }
                                }
                            }
                        dialog.dismiss()
                    }


                    val fragment: Fragment = MapWalksFragment()
                    val fragmentManager = (ctx as FragmentActivity).supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(
                        R.id.map_walks_fragment,
                        fragment
                    )
                    fragmentTransaction.addToBackStack(
                        null
                    )
                    fragmentTransaction.commit()

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
        })
    }

    override fun getItemCount(): Int {
        return walksList.size
    }

    init {
        this.walksList = walksList
    }
}

class MapWalksHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var postDescription: TextView
    var postDate: TextView
    var postTime: TextView
    var postDogImage1: ImageView
    var finish: Button
    var walkerUsername: TextView

    init {
        postDescription = itemView.findViewById(R.id.map_walks_description_value)
        postDate = itemView.findViewById(R.id.map_walks_date_value)
        postTime = itemView.findViewById(R.id.map_walks_time_value)
        postDogImage1 = itemView.findViewById(R.id.map_walks_dog1_pictue_value)
        finish = itemView.findViewById(R.id.map_walks_finish_button)
        walkerUsername = itemView.findViewById(R.id.map_walks_walker_username_value)
    }
}