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

class PersonsPetsFragment : Fragment() {

    private lateinit var walkerId: String

    var adapterPersonsPets: AdapterPersonsPets? = null
    var recyclerView: RecyclerView? = null
    var personsPet: Pet? = null
    var personsPetsList: ArrayList<Pet>? = null

    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        val bundle = this.arguments
        if (bundle != null) {
            walkerId = bundle.getString("user_id")!!
        }

        personsPetsList = ArrayList<Pet>()
        auth = Firebase.auth
        val petsRef: CollectionReference = db.collection("pets")
        recyclerView = view.findViewById<View>(R.id.users_pets_recycler) as RecyclerView

        petsRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        if (document.getString("userId") == walkerId) {
                            val gridLayoutManager = GridLayoutManager(context, 1)
                            recyclerView!!.setLayoutManager(gridLayoutManager)
                            personsPet = Pet(
                                document.id,
                                document.getString("name"),
                                document.getString("breed"),
                                document.getString("weight"),
                                document.getString("description"),
                                document.getString("userId"),
                                document.getString("petImageUrl")
                            )
                            personsPetsList!!.add(personsPet!!)
                            adapterPersonsPets = context?.let { AdapterPersonsPets(it, personsPetsList!!) }
                            recyclerView!!.setAdapter(adapterPersonsPets)
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
        return inflater.inflate(R.layout.fragment_persons_pets, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}