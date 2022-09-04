package elfak.mosis.iwalk

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PetInfoFragment : Fragment() {

    private lateinit var dogName : TextView
    private lateinit var dogBreed : TextView
    private lateinit var dogWeight : TextView
    private lateinit var dogDescription : TextView
    private lateinit var dogImage : CircleImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dogImage = requireView().findViewById<CircleImageView>(R.id.pet_info_picture)
        dogName = requireView().findViewById<TextView>(R.id.pet_info_name_value)
        dogBreed = requireView().findViewById<TextView>(R.id.pet_info_breed_value)
        dogWeight = requireView().findViewById<TextView>(R.id.pet_info_weight_value)
        dogDescription = requireView().findViewById<TextView>(R.id.pet_info_description_value)

        val bundle = this.arguments
        if (bundle != null) {
            dogName.setText(bundle.getString("pet_name"))
            dogBreed.setText(bundle.getString("pet_breed"))
            dogWeight.setText(bundle.getString("pet_weight"))
            dogDescription.setText(bundle.getString("pet_description"))
            Picasso.get().load(bundle.getString("pet_image"))
                .into(dogImage)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pet_info, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}