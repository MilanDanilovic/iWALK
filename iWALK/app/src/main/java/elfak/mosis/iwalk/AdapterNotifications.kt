package elfak.mosis.iwalk

import android.content.Context
import android.content.DialogInterface
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.util.*

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
        if (URLUtil.isValidUrl(notificationsList[position].getDogImage1())) {
            Picasso.get().load(notificationsList[position].getDogImage1())
                .into(notificationHolder.dogPic1)
        }
        if (URLUtil.isValidUrl(notificationsList[position].getDogImage2())) {
            Picasso.get().load(notificationsList[position].getDogImage2())
                .into(notificationHolder.dogPic2)
        }

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

        notificationHolder.accept.setOnClickListener(View.OnClickListener { v ->
            val alertDialog = AlertDialog.Builder(ctx, R.style.Theme_PopUpDialog)
            alertDialog.setMessage("Are you sure you want to accept notification?")

            alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->

                notificationsList.get(notificationHolder.adapterPosition).getNotificationId()?.let {
                    docRef.collection("messages")
                        .get()
                        .addOnCompleteListener{ task ->
                            if (task.isSuccessful) {
                                for (document in task.result) {
                                    if (document.id == it) {
                                        val query = baseAuth!!.currentUser?.uid
                                        val dataToSave: MutableMap<String, Any> =
                                            HashMap()
                                        dataToSave["description"] = document["description"].toString()
                                        dataToSave["dogImage1Url"] = document["dogImage1Url"].toString()
                                        dataToSave["dogImage2Url"] = document["dogImage2Url"].toString()
                                        dataToSave["date"] = document["date"].toString()
                                        dataToSave["status"] = "IN_PROGRESS"
                                        dataToSave["timeOfPosting"] = document["timeOfSending"].toString()
                                        dataToSave["time"] = document["time"].toString()
                                        dataToSave["userId"] = document["senderId"].toString()
                                        dataToSave["walkerId"] = baseAuth!!.currentUser?.uid.toString()

                                        docRef.collection("posts").add(dataToSave).addOnSuccessListener {
                                            Log.d("TAG", "Post is saved! ")

                                            docRef.collection("messages")
                                                .document(document.id)
                                                .delete()
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        docRef.collection("messages").document(
                                                            notificationsList.get(notificationHolder.getAdapterPosition()).getNotificationId()!!
                                                        )
                                                            .delete()
                                                            .addOnSuccessListener {
                                                                Log.d(
                                                                    "TAG",
                                                                    "DocumentSnapshot successfully deleted!"
                                                                )
                                                            }
                                                            .addOnFailureListener { e ->
                                                                Log.w(
                                                                    "TAG",
                                                                    "Error deleting documentAAAAAAAAAAAAAAAAAAAAAAAAA",
                                                                    e
                                                                )
                                                            }
                                                        val activity= ctx as AppCompatActivity
                                                        val notifiactionsFragment= NotifiactionsFragment()
                                                        activity.supportFragmentManager.beginTransaction().replace(R.id.notifications_fragment, notifiactionsFragment).commit()
                                                        Toast.makeText(v.context, "Friend request is deleted!", Toast.LENGTH_LONG).show()
                                                    } else {
                                                        Toast.makeText(
                                                            v.context,
                                                            "Error deleting post!",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                        Log.w(
                                                            "TAG",
                                                            "Error deleting documentAAAAAAAAAAAAAAAAAAAAAAAAA"
                                                        )
                                                    }
                                                }



                                            Toast.makeText(
                                                ctx,
                                                "Post is saved in database!",
                                                Toast.LENGTH_LONG
                                            )
                                                .show()
                                        }.addOnFailureListener { e ->
                                            Toast.makeText(
                                                ctx,
                                                "Post is not saved! Try again! ",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            Log.w("TAG", "Post is not saved in database! ", e)
                                        }

                                    }
                                }
                            }
                            else {
                                Log.d("TAG", "Error getting documents: ", task.exception)
                            }
                        }
                }


            })
            alertDialog.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            alertDialog.show()

        })

        notificationHolder.decline.setOnClickListener(View.OnClickListener { v ->
            val alertDialog = AlertDialog.Builder(ctx, R.style.Theme_PopUpDialog)
            alertDialog.setMessage("Are you sure you want to decline notification?")

            alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                notificationsList.get(notificationHolder.getAdapterPosition()).getNotificationId()?.let {
                    docRef.collection("messages")
                        .document(it)
                        .delete()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                docRef.collection("messages").document(
                                    notificationsList.get(notificationHolder.getAdapterPosition()).getNotificationId()!!
                                )
                                    .delete()
                                    .addOnSuccessListener {
                                        Log.d(
                                            "TAG",
                                            "DocumentSnapshot successfully deleted!"
                                        )
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(
                                            "TAG",
                                            "Error deleting documentAAAAAAAAAAAAAAAAAAAAAAAAA",
                                            e
                                        )
                                    }
                                val activity= ctx as AppCompatActivity
                                val notifiactionsFragment= NotifiactionsFragment()
                                activity.supportFragmentManager.beginTransaction().replace(R.id.notifications_fragment, notifiactionsFragment).commit()
                                Toast.makeText(v.context, "Friend request is deleted!", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(
                                    v.context,
                                    "Error deleting post!",
                                    Toast.LENGTH_LONG
                                ).show()
                                Log.w(
                                    "TAG",
                                    "Error deleting documentAAAAAAAAAAAAAAAAAAAAAAAAA"
                                )
                            }
                        }
                }
            })
            alertDialog.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            alertDialog.show()

        })


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