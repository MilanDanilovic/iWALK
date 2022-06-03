package elfak.mosis.iwalk

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class AdapterMyPets(var ctx: Context, petsList: MutableList<Pet>) :
    RecyclerView.Adapter<MyPetHolder>() {

    lateinit var petsList: MutableList<Pet>
    var baseAuth: FirebaseAuth? = null
    private val docRef = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyPetHolder {
        val mView: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.custom_grid_pets, viewGroup, false)
        return MyPetHolder(mView)
    }

    override fun onBindViewHolder(myPetHolder: MyPetHolder, position: Int) {
        myPetHolder.petName.setText(petsList[position].getPetName())
        myPetHolder.petBreed.setText(petsList[position].getPetBreed())
        myPetHolder.petWeight.setText(petsList[position].getPetWeight())
        myPetHolder.petDescription.setText(petsList[position].getPetDescription())
        baseAuth = FirebaseAuth.getInstance()
        myPetHolder.constraintLayoutMyPets.setOnClickListener(View.OnClickListener { v ->
            val activity= v!!.context as AppCompatActivity
            val editPetFragment= EditPetFragment()
            val bundle = Bundle()
            bundle.putString("pet_name", petsList[myPetHolder.adapterPosition].getPetName())
            bundle.putString("pet_breed", petsList[myPetHolder.adapterPosition].getPetBreed())
            bundle.putString("pet_weight", petsList[myPetHolder.adapterPosition].getPetWeight())
            bundle.putString("pet_description", petsList[myPetHolder.adapterPosition].getPetDescription())
            editPetFragment.setArguments(bundle)
            activity.supportFragmentManager.beginTransaction().replace(R.id.my_pets_fragment, editPetFragment).commit()
            //val intent = Intent(ctx, EditPetFragment::class.java)
            //val lista = ArrayList<String>()
            //petsList[myPetHolder.adapterPosition].getPetName()?.let { lista.add(it) }
            //petsList[myPetHolder.adapterPosition].getPetBreed()?.let { lista.add(it) }
            //petsList[myPetHolder.adapterPosition].getPetWeight()?.let { lista.add(it) }
            //petsList[myPetHolder.adapterPosition].getPetDescription()?.let { lista.add(it) }
            //intent.putStringArrayListExtra("myPetsList", lista)
            //ctx.startActivity(intent)

            /*val myFragment: Fragment = EditPetFragment()


            val yourFragment = EditPetFragment()
            val args = Bundle()
            args.putString("pet_name", petsList[myPetHolder.adapterPosition].getPetName())
            args.putString("pet_breed", petsList[myPetHolder.adapterPosition].getPetBreed())
            args.putString("pet_weight", petsList[myPetHolder.adapterPosition].getPetWeight())
            args.putString("pet_description", petsList[myPetHolder.adapterPosition].getPetDescription())
            yourFragment.setArguments(args)*/
        })
    }

    override fun getItemCount(): Int {
        return petsList.size
    }

    init {
        this.petsList = petsList
    }
}

//Ovo se menja :D
class MyPetHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var petImage: ImageView
    var petName: TextView
    var petBreed: TextView
    var petWeight: TextView
    var petDescription: TextView
    var constraintLayoutMyPets: ConstraintLayout

    init {
        petImage = itemView.findViewById(R.id.users_pet_picture)
        petName = itemView.findViewById(R.id.users_pet_name_value)
        petBreed = itemView.findViewById(R.id.users_pet_dog_breed_value)
        petWeight = itemView.findViewById(R.id.users_pet_weight_value)
        petDescription = itemView.findViewById(R.id.users_pet_description_value)
        constraintLayoutMyPets = itemView.findViewById(R.id.constraint_layout_pets)
    }
}