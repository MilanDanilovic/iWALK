package elfak.mosis.iwalk

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AdapterTopWalkers(var ctx: Context, topWalkersList: MutableList<TopWalkers>) :
    RecyclerView.Adapter<TopWalkersHolder>() {

    lateinit var topWalkersList: MutableList<TopWalkers>
    var baseAuth: FirebaseAuth? = null
    private val docRef = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): TopWalkersHolder {
        val mView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.custom_grid_top_walkers, viewGroup, false)
        return TopWalkersHolder(mView)
    }

    override fun onBindViewHolder(topWalkersHolder: TopWalkersHolder, position: Int) {
        topWalkersHolder.username.setText(topWalkersList[position].getWalkerUsername())
        topWalkersHolder.score.setText(topWalkersList[position].getWalkerScore().toString())
        if (URLUtil.isValidUrl(topWalkersList[position].getWalkerImage())) {
            Picasso.get().load(topWalkersList[position].getWalkerImage())
                .into(topWalkersHolder.image)
        }


        baseAuth = FirebaseAuth.getInstance()

        topWalkersHolder.layout.setOnClickListener(View.OnClickListener { v ->
            val activity= ctx as AppCompatActivity
            val walkerInfoFragment= WalkerInfoFragment()
            val bundle = Bundle()
            bundle.putString("user_id", topWalkersList[position].getWalkerId())
            walkerInfoFragment.setArguments(bundle)
            activity.supportFragmentManager.beginTransaction().replace(R.id.home_fragment, walkerInfoFragment).commit()
        })

        topWalkersHolder.sendMessage.setOnClickListener(View.OnClickListener { v ->
            val activity= ctx as AppCompatActivity
            val messageForWalkerFragment= MessageForWalkerFragment()
            val bundle = Bundle()
            bundle.putString("user_id", topWalkersList[position].getWalkerId())
            messageForWalkerFragment.setArguments(bundle)
            activity.supportFragmentManager.beginTransaction().replace(R.id.home_fragment, messageForWalkerFragment).commit()
        })
    }

    override fun getItemCount(): Int {
        return topWalkersList.size
    }

    init {
        this.topWalkersList = topWalkersList
    }

}

class TopWalkersHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var image: CircleImageView
    var username: TextView
    var score: TextView
    var sendMessage: ImageView
    var layout: LinearLayout

    init {
        image = itemView.findViewById(R.id.top_walker_profile_picture)
        username = itemView.findViewById(R.id.top_walker_username_value)
        score = itemView.findViewById(R.id.top_walker_score_value)
        sendMessage = itemView.findViewById(R.id.top_walkers_send_message)
        layout = itemView.findViewById(R.id.custom_grid_top_walkers)
    }
}