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

class AdapterFavouriteWalkers(var ctx: Context, favouriteWalkersList: MutableList<FavouriteWalker>) :
    RecyclerView.Adapter<FavouriteWalkersHolder>() {

    lateinit var favouriteWalkersList: MutableList<FavouriteWalker>
    var baseAuth: FirebaseAuth? = null
    private val docRef = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): FavouriteWalkersHolder {
        val mView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.custom_grid_walkers_favourite, viewGroup, false)
        return FavouriteWalkersHolder(mView)
    }

    override fun onBindViewHolder(favouriteWalkersHolder: FavouriteWalkersHolder, position: Int) {
        favouriteWalkersHolder.username.setText(favouriteWalkersList[position].getWalkerUsername())
        if (URLUtil.isValidUrl(favouriteWalkersList[position].getWalkerImage())) {
            Picasso.get().load(favouriteWalkersList[position].getWalkerImage())
                .into(favouriteWalkersHolder.image)
        }

        baseAuth = FirebaseAuth.getInstance()

        favouriteWalkersHolder.layout.setOnClickListener(View.OnClickListener { v ->
            val activity= ctx as AppCompatActivity
            val walkerInfoFragment= WalkerInfoFragment()
            val bundle = Bundle()
            bundle.putString("user_id", favouriteWalkersList[position].getWalkerId())
            walkerInfoFragment.setArguments(bundle)
            activity.supportFragmentManager.beginTransaction().replace(R.id.favourite_walkers_fragment, walkerInfoFragment).commit()
        })

        favouriteWalkersHolder.sendMessage.setOnClickListener(View.OnClickListener { v ->
            val activity= ctx as AppCompatActivity
            val messageForWalkerFragment= MessageForWalkerFragment()
            val bundle = Bundle()
            bundle.putString("user_id", favouriteWalkersList[position].getWalkerId())
            messageForWalkerFragment.setArguments(bundle)
            activity.supportFragmentManager.beginTransaction().replace(R.id.favourite_walkers_fragment, messageForWalkerFragment).commit()
        })
    }

    override fun getItemCount(): Int {
        return favouriteWalkersList.size
    }

    init {
        this.favouriteWalkersList = favouriteWalkersList
    }

}
class FavouriteWalkersHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var image: CircleImageView
    var username: TextView
    var sendMessage: ImageView
    var layout: LinearLayout

    init {
        image = itemView.findViewById(R.id.favourite_walker_profile_picture)
        username = itemView.findViewById(R.id.favourite_walker_username_value)
        sendMessage = itemView.findViewById(R.id.walkers_send_message)
        layout = itemView.findViewById(R.id.custom_grid_walkers_favourite)
    }
}