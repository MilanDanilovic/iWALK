package elfak.mosis.iwalk

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class AdapterPersonsPets(var ctx: Context, petsList: MutableList<Pet>) :
    RecyclerView.Adapter<PersonsPetHolder>() {

    lateinit var petsList: MutableList<Pet>
    var baseAuth: FirebaseAuth? = null
    private val docRef = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PersonsPetHolder {
        val mView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.custom_grid_pets, viewGroup, false)
        return PersonsPetHolder(mView)
    }

    override fun onBindViewHolder(personsPetHolder: PersonsPetHolder, position: Int) {
        personsPetHolder.petName.setText(petsList[position].getPetName())
        personsPetHolder.petBreed.setText(petsList[position].getPetBreed())
        personsPetHolder.petWeight.setText(petsList[position].getPetWeight())
        personsPetHolder.petDescription.setText(petsList[position].getPetDescription())
        Picasso.get().load(petsList[position].getPetImage())
            .into(personsPetHolder.petImage)
        baseAuth = FirebaseAuth.getInstance()
    }

    override fun getItemCount(): Int {
        return petsList.size
    }

    init {
        this.petsList = petsList
    }
}

class PersonsPetHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var petImage: ImageView
    var petName: TextView
    var petBreed: TextView
    var petWeight: TextView
    var petDescription: TextView

    init {
        petImage = itemView.findViewById(R.id.users_pet_picture)
        petName = itemView.findViewById(R.id.users_pet_name_value)
        petBreed = itemView.findViewById(R.id.users_pet_dog_breed_value)
        petWeight = itemView.findViewById(R.id.users_pet_weight_value)
        petDescription = itemView.findViewById(R.id.users_pet_description_value)
    }
}