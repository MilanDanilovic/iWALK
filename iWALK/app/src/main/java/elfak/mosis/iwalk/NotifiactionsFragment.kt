package elfak.mosis.iwalk

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class NotifiactionsFragment : Fragment() {

    var adapterNotifications: AdapterNotifications? = null
    private val docRef = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    var recyclerView: RecyclerView? = null
    var notification: Notification? = null
    var notificationsList: ArrayList<Notification>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationsList = ArrayList<Notification>()
        auth = Firebase.auth
        val notificationsRef: CollectionReference = docRef.collection("messages")
        recyclerView = view.findViewById<View>(R.id.notifications_recycler) as RecyclerView

        notificationsRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        if (document.getString("receiverId") == auth.currentUser?.uid && document.getString("status") == "OPEN") {
                            val gridLayoutManager = GridLayoutManager(context, 1)
                            recyclerView!!.setLayoutManager(gridLayoutManager)
                            notification = Notification(
                                document.id,
                                document.getString("description"),
                                document.getString("date"),
                                document.getString("time"),
                                document.getString("status"),
                                document.getString("senderId"),
                                document.getString("receiverId"),
                                document.getString("dogImage1Url"),
                                document.getString("dogImage2Url")
                            )
                            notificationsList!!.add(notification!!)
                            adapterNotifications = context?.let { AdapterNotifications(it, notificationsList!!) }
                            recyclerView!!.setAdapter(adapterNotifications)
                        }
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.exception)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifiactions, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}