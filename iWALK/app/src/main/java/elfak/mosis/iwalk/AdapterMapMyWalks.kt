package elfak.mosis.iwalk

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class AdapterMapMyWalks(var ctx: Context, walksList: MutableList<Walks>) :
    RecyclerView.Adapter<MapMyWalksHolder>() {

    lateinit var walksList: MutableList<Walks>
    var baseAuth: FirebaseAuth? = null
    private val docRef = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MapMyWalksHolder {
        val mView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.custom_grid_map_my_walks, viewGroup, false)
        return MapMyWalksHolder(mView)
    }

    override fun onBindViewHolder(mapMyWalksHolder: MapMyWalksHolder, position: Int) {
        mapMyWalksHolder.postDescription.setText(walksList[position].getPostDescription())
        mapMyWalksHolder.postDate.setText(walksList[position].getPostDate())
        mapMyWalksHolder.postTime.setText(walksList[position].getPostTime())
        if (URLUtil.isValidUrl(walksList[position].getPostDogImage1())) {
            Picasso.get().load(walksList[position].getPostDogImage1())
                .into(mapMyWalksHolder.postDogImage1)
        }

        mapMyWalksHolder.cancel.setOnClickListener(View.OnClickListener { v ->
            val alertDialog = AlertDialog.Builder(ctx, R.style.Theme_PopUpDialog)
            alertDialog.setMessage("Are you sure you want to cancel this walk?")

            alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->

                val documentReference = walksList.get(mapMyWalksHolder.adapterPosition).getPostId()?.let { it -> docRef.collection("markers").document(it) }

                documentReference?.update(
                    "status", "OPEN",
                    "walkerId", ""
                )?.addOnCompleteListener(OnCompleteListener<Void?> { task ->
                    if (task.isSuccessful) {

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

            alertDialog.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            alertDialog.show()
        })
    }

    override fun getItemCount(): Int {
        return walksList.size
    }

    init {
        this.walksList = walksList
    }
}

class MapMyWalksHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var postDescription: TextView
    var postDate: TextView
    var postTime: TextView
    var postDogImage1: ImageView
    var cancel: Button

    init {
        postDescription = itemView.findViewById(R.id.my_map_walks_description_value)
        postDate = itemView.findViewById(R.id.my_map_walks_date_value)
        postTime = itemView.findViewById(R.id.my_map_walks_time_value)
        postDogImage1 = itemView.findViewById(R.id.my_map_walks_dog1_pictue_value)
        cancel = itemView.findViewById(R.id.my_map_walks_cancel_button)
    }
}