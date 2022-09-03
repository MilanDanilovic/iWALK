package elfak.mosis.iwalk

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class AdapterNotifications(var ctx: Context, notificationsList: MutableList<Notification>) :
    RecyclerView.Adapter<NotificationsHolder>() {

    lateinit var notificationsList: MutableList<Notification>
    var baseAuth: FirebaseAuth? = null
    private val docRef = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): NotificationsHolder {
        val mView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.custom_grid_notifiations, viewGroup, false)
        return NotificationsHolder(mView)
    }

    override fun onBindViewHolder(notificationHolder: NotificationsHolder, position: Int) {
        notificationHolder.description.setText(notificationsList[position].getDescription())
        notificationHolder.date.setText(notificationsList[position].getDate())
        notificationHolder.time.setText(notificationsList[position].getTime())
        Picasso.get().load(notificationsList[position].getDogImage1())
            .into(notificationHolder.dogPic1)
        Picasso.get().load(notificationsList[position].getDogImage2())
            .into(notificationHolder.dogPic2)

        baseAuth = FirebaseAuth.getInstance()

        notificationsList.get(notificationHolder.adapterPosition).getSenderId()?.let {
            docRef.collection("users")
                .get()
                .addOnCompleteListener{ task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            if (document.id == it) {
                                notificationHolder.username.setText(document.getString("username"))
                            }
                        }
                    }
                    else {
                        Log.d("TAG", "Error getting documents: ", task.exception)
                    }
                }
        }

        /*notificationHolder.accept.setOnClickListener(View.OnClickListener { v ->

        })

        notificationHolder.decline.setOnClickListener(View.OnClickListener { v ->

        })*/


    }

    override fun getItemCount(): Int {
        return notificationsList.size
    }

    init {
        this.notificationsList = notificationsList
    }
}

class NotificationsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var dogPic1: ImageView
    var dogPic2: ImageView
    var username: TextView
    var description: TextView
    var date: TextView
    var time: TextView
    var accept: Button
    var decline: Button

    init {
        dogPic1 = itemView.findViewById(R.id.notifications_dog_picture1)
        dogPic2 = itemView.findViewById(R.id.notifications_dog_picture2)
        username = itemView.findViewById(R.id.notifications_username_value)
        description = itemView.findViewById(R.id.notifications_content_value)
        date = itemView.findViewById(R.id.notifications_date_value)
        time = itemView.findViewById(R.id.notifications_time_value)
        accept = itemView.findViewById(R.id.accept_message)
        decline = itemView.findViewById(R.id.decline_message)
    }
}