package elfak.mosis.iwalk

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import de.hdodenhof.circleimageview.CircleImageView


class AdapterFriendAll (var ctx: Context, allFriendsList: MutableList<Friend>) :
    RecyclerView.Adapter<AllFriendsHolder>() {

    lateinit var allFriendsList: MutableList<Friend>
    var baseAuth: FirebaseAuth? = null
    private val docRef = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): AllFriendsHolder {
        val mView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.custom_grid_all_friends, viewGroup, false)
        return AllFriendsHolder(mView)
    }

    override fun onBindViewHolder(allFriendsHolder: AllFriendsHolder, position: Int) {
        allFriendsHolder.friendUsername.setText(allFriendsList[position].getFriendUsername())
        Picasso.get().load(allFriendsList[position].getFriendImage())
            .into(allFriendsHolder.friendImage)
        baseAuth = FirebaseAuth.getInstance()

        allFriendsHolder.delete.setOnClickListener(View.OnClickListener { v->
            val alertDialog = AlertDialog.Builder(v.context, R.style.Theme_PopUpDialog)
            alertDialog.setMessage("Are you sure you want to delete this friend?")

            alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->

                allFriendsList.get(allFriendsHolder.adapterPosition).getFriendId()?.let {
                    docRef.collection("users")
                        .get()
                        .addOnCompleteListener{ task ->
                            if (task.isSuccessful) {
                                for (document in task.result) {
                                    if (document.id == baseAuth!!.currentUser!!.uid) {
                                        var friends = arrayListOf<String>()
                                        if (document["friends"] != null) {
                                            friends = document["friends"] as ArrayList<String>
                                            if (friends.contains(it)) {
                                                friends.remove(it)

                                                val documentReference = baseAuth!!.currentUser!!.uid.let { it1 -> docRef.collection("users").document(it1) }
                                                documentReference.update(
                                                    "friends", friends
                                                )
                                                    .addOnCompleteListener(OnCompleteListener<Void?> { task ->
                                                        if (task.isSuccessful) {

                                                            docRef.collection("users")
                                                                .get()
                                                                .addOnCompleteListener { taskOfFriend ->
                                                                    if (taskOfFriend.isSuccessful) {
                                                                        for (documentOfFriend in taskOfFriend.result) {
                                                                            if (documentOfFriend.id == it) {
                                                                                var friendsOfFriend = arrayListOf<String>()
                                                                                if (documentOfFriend["friends"] != null) {
                                                                                    friendsOfFriend = documentOfFriend["friends"] as ArrayList<String>
                                                                                    if (friendsOfFriend.contains(baseAuth!!.currentUser!!.uid)) {
                                                                                        friendsOfFriend.remove(baseAuth!!.currentUser!!.uid)

                                                                                        val documentReferenceOfFriend = it.let { it1 -> docRef.collection("users").document(it1) }
                                                                                        documentReferenceOfFriend.update(
                                                                                            "friends", friendsOfFriend
                                                                                        )
                                                                                            .addOnCompleteListener(OnCompleteListener<Void?> { task ->
                                                                                                if (task.isSuccessful) {

                                                                                                    val fragment: Fragment = AllFriendsFragment()
                                                                                                    val fragmentManager = (ctx as FragmentActivity).supportFragmentManager
                                                                                                    val fragmentTransaction = fragmentManager.beginTransaction()
                                                                                                    fragmentTransaction.replace(
                                                                                                        R.id.all_friends_fragment,
                                                                                                        fragment
                                                                                                    )
                                                                                                    fragmentTransaction.addToBackStack(
                                                                                                        null
                                                                                                    )
                                                                                                    fragmentTransaction.commit()

                                                                                                    Toast.makeText(
                                                                                                        v.context,
                                                                                                        "Data successfully updated.",
                                                                                                        Toast.LENGTH_SHORT
                                                                                                    ).show()
                                                                                                } else {
                                                                                                    Toast.makeText(
                                                                                                        v.context,
                                                                                                        "Error updating data.",
                                                                                                        Toast.LENGTH_SHORT
                                                                                                    ).show()
                                                                                                }
                                                                                            }).addOnFailureListener(OnFailureListener { })
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                    else {
                                                                        Log.d("TAG", "Error getting documents: ", task.exception)
                                                                    }
                                                                }
                                                            Toast.makeText(
                                                                v.context,
                                                                "Data successfully updated.",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        } else {
                                                            Toast.makeText(
                                                                v.context,
                                                                "Error updating data.",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }).addOnFailureListener(OnFailureListener { })
                                            }
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
    }

    override fun getItemCount(): Int {
        return allFriendsList.size
    }

    init {
        this.allFriendsList = allFriendsList
    }
}

class AllFriendsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var friendImage: CircleImageView
    var friendUsername: TextView
    var delete: ImageView

    init {
        friendImage = itemView.findViewById(R.id.all_friends_profile_picture)
        friendUsername = itemView.findViewById(R.id.all_friends_username_value)
        delete = itemView.findViewById(R.id.all_friends_delete_friend)
    }
}
