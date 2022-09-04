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
        if (URLUtil.isValidUrl(petsList[position].getPetImage())) {
            Picasso.get().load(petsList[position].getPetImage())
                .into(personsPetHolder.petImage)
        }

        baseAuth = FirebaseAuth.getInstance()

        personsPetHolder.layout.setOnClickListener(View.OnClickListener { v ->
            val activity= ctx as AppCompatActivity
            val petInfoFragment= PetInfoFragment()
            val bundle = Bundle()
            bundle.putString("pet_name", petsList[position].getPetName())
            bundle.putString("pet_breed", petsList[position].getPetBreed())
            bundle.putString("pet_weight", petsList[position].getPetWeight())
            bundle.putString("pet_description", petsList[position].getPetDescription())
            bundle.putString("pet_image", petsList[position].getPetImage())
            petInfoFragment.setArguments(bundle)
            activity.supportFragmentManager.beginTransaction().replace(R.id.persons_pets_fragment, petInfoFragment).commit()
        })
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
    var layout: LinearLayout

    init {
        petImage = itemView.findViewById(R.id.users_pet_picture)
        petName = itemView.findViewById(R.id.users_pet_name_value)
        petBreed = itemView.findViewById(R.id.users_pet_dog_breed_value)
        petWeight = itemView.findViewById(R.id.users_pet_weight_value)
        petDescription = itemView.findViewById(R.id.users_pet_description_value)
        layout = itemView.findViewById(R.id.custom_grid_pets)
    }
}