package elfak.mosis.iwalk

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class MyPetsFragment : Fragment() {

    var adapterMyPets: AdapterMyPets? = null
    private val docRef = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    var recyclerView: RecyclerView? = null
    var pet: Pet? = null
    var petsList: ArrayList<Pet>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        petsList = ArrayList<Pet>()
        auth = Firebase.auth
        val petsRef: CollectionReference = docRef.collection("pets")
        recyclerView = view.findViewById<View>(R.id.my_pets_recycler) as RecyclerView
        petsRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        if (document.getString("userId") == auth.currentUser?.uid) {
                            val gridLayoutManager = GridLayoutManager(context, 1)
                            recyclerView!!.setLayoutManager(gridLayoutManager)
                            pet = Pet(
                                document.id,
                                document.getString("name"),
                                document.getString("breed"),
                                document.getString("weight"),
                                document.getString("description"),
                                document.getString("userId"),
                                document.getString("petImageUrl")
                            )
                            petsList!!.add(pet!!)
                            adapterMyPets = context?.let { AdapterMyPets(it, petsList!!) }
                            recyclerView!!.setAdapter(adapterMyPets)
                        }
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.exception)
                }
            }

        val addNewPet: Button = requireView().findViewById<Button>(R.id.my_pets_add_new_pet)
        addNewPet.setOnClickListener(View.OnClickListener {
            val fragment: Fragment = AddPetFragment()
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.my_pets_fragment, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_pets, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}