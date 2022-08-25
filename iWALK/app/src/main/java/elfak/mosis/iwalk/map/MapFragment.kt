package elfak.mosis.iwalk.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import elfak.mosis.iwalk.databinding.FragmentMapBinding

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var map: GoogleMap
    private val LOCATION_PERMISSION_REQUEST=1

    @SuppressLint("MissingPermission")
    private fun getLocationAccess(){
        if (requireActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && activity?.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
            map.isMyLocationEnabled = true
        }
            else
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),LOCATION_PERMISSION_REQUEST)
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if(grantResults.contains(PackageManager.PERMISSION_GRANTED)){
                map.isMyLocationEnabled = true
            }
            else {
                Toast.makeText(activity,"User has not granted location access permission", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
                //mozda ne radi
            }
        }
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.onResume()

        binding.mapView.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap?.let {
            map = it
            getLocationAccess()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
