package elfak.mosis.iwalk.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import elfak.mosis.iwalk.R
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import elfak.mosis.iwalk.AdapterMyPets
import elfak.mosis.iwalk.Pet
import elfak.mosis.iwalk.databinding.FragmentMapBinding

class MapFragment : Fragment(), OnMapReadyCallback {

    data class UserFromDatabase(
        val email: String? = null,
        val latitude: Number? = null,
        val longitude: Number? = null,
        val name: String? = null,
        val numberOfWalks: String? = null,
        val phone: String? = null,
        val profileImageUrl:String? = null,
        val score:String? = null,
        val surname:String? = null,
        val username:String? = null
    )
    var listOfOtherUsers:MutableList<UserFromDatabase> = mutableListOf<UserFromDatabase>()

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private var auth: FirebaseAuth =  Firebase.auth
	private var markers: MutableList<Marker> = mutableListOf()

    private var isCameraInitiallySet: Boolean = false
    private lateinit var map: GoogleMap
    private val LOCATION_PERMISSION_REQUEST=1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private val docRef = FirebaseFirestore.getInstance()


    private var currentUserMarker: Marker? = null
    private var listOfOtherUsersMarkers = ArrayList<Marker>()

    @SuppressLint("MissingPermission")
    private fun getLocationAccess(){
        if (requireActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && activity?.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
            map.isMyLocationEnabled = true //TODO Check if we want to have option to access current location everytime
            getLocationUpdates()
            startLocationUpdates()
        }
            else
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),LOCATION_PERMISSION_REQUEST)
    }

    private fun getLocationUpdates() {
        locationRequest = LocationRequest()
        locationRequest.interval = 15000
        locationRequest.fastestInterval = 10000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        locationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                if(locationResult.locations.isNotEmpty()){
                    val lastLocation = locationResult.lastLocation
                    if(lastLocation != null){
                        val latLng = LatLng(lastLocation.latitude,lastLocation.longitude)

                        updateCurrentUserLocation(latLng)

                        val bearing = lastLocation.bearing
                        if(!isCameraInitiallySet) { //TODO Check if we want to have camera fixed to our marker
                            map.moveCamera(
                                CameraUpdateFactory.newCameraPosition(
                                    CameraPosition.Builder()
                                        .target(
                                            LatLng(
                                                lastLocation.latitude,
                                                lastLocation.longitude
                                            )
                                        )
                                        .bearing(bearing)
                                        .zoom(18f)
                                        .tilt(30f)
                                        .build()
                                )
                            )
                            isCameraInitiallySet = true
                        }

                        currentUserMarker?.remove()
                        currentUserMarker = map.addMarker(MarkerOptions()
                            .position(LatLng(lastLocation.latitude,lastLocation.longitude))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_location_marker))
                            .flat(true)
                            .anchor(0.5f,0.5f)
                            .rotation(lastLocation.bearing))

                        for(marker in listOfOtherUsersMarkers){
                            marker.remove()
                        }

                        getAllUsersFromDatabase()


                        for(user in listOfOtherUsers){
                            val userLatLng = LatLng(user.latitude as Double, user.longitude as Double)
                            val markerOptions = MarkerOptions().position(userLatLng)
                            val userMarker = map.addMarker(markerOptions)
                            if (userMarker != null) {
                                    listOfOtherUsersMarkers.add(userMarker)
                            }
                        }

                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates(){
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
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
            }
        }
    }


    private fun getAllUsersFromDatabase() {
        val documentReference = docRef.collection("users")
        documentReference.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        if (document.id != auth.currentUser?.uid) {
                            val userFromDatabase = UserFromDatabase(
                                document.getString("email"),
                                document.get("latitude") as Number?,
                                document.get("longitude") as Number?,
                                document.getString("name"),
                                document.getString("numberOfWalks"),
                                document.getString("phone"),
                                document.getString("profileImageUrl"),
                                document.getString("score"),
                                document.getString("surname"),
                                document.getString("username")
                            )
                            listOfOtherUsers.add(userFromDatabase)
                        }
                    }
                }
            }
    }

    private fun updateCurrentUserLocation(latLng: LatLng){

        val documentReference = docRef.collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)

        val dataToSave: MutableMap<String, Any> =
            HashMap()

        dataToSave["latitude"] = latLng.latitude
        dataToSave["longitude"] = latLng.longitude


        documentReference.update(dataToSave)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                } else {
                    Toast.makeText(
                        this@MapFragment.context,
                        "Error while updating data!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener { }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.onResume()

        binding.mapView.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        Snackbar.make(requireView(), "Long press to add a marker", Snackbar.LENGTH_SHORT)
            .setAction("OK", {})
            .setActionTextColor(ContextCompat.getColor(requireContext(),android.R.color.white))
            .show()

    }

    override fun onMapReady(googleMap: GoogleMap) {

        val mMap = googleMap

        mMap.setOnInfoWindowClickListener { markerToDelete ->
            Log.i("TAG", "onWindowClickListener - delete marker")

            val markersRef: CollectionReference = docRef.collection("markers")
            markersRef.get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            if (document.getString("userId") == auth.currentUser?.uid && document.getString("latitude") == markerToDelete.position.latitude.toString() && document.getString("longitude") == markerToDelete.position.longitude.toString()) {
                                docRef.collection("markers")
                                    .document()
                                    .delete()
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            docRef.collection("markers").document(
                                                document.id
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
                                            //markers.remove(markerToDelete)
                                            //markerToDelete.remove()
                                        } else {
                                            Log.w(
                                                "TAG",
                                                "Error deleting documentAAAAAAAAAAAAAAAAAAAAAAAAA"
                                            )
                                        }
                                    }
                            }
                        }
                    } else {
                        Log.d("TAG", "Error getting marker documents: ", task.exception)
                    }
                }
        }

        mMap.setOnMapLongClickListener { latlng ->
            Log.i("TAG", "onMapLongClickListener")
            showAlertDialog(latlng)
        }
        googleMap?.let {
            map = it
            getLocationAccess()
        }
    }

    private fun showAlertDialog(latlng: LatLng) {
        val placeFormView = LayoutInflater.from(context).inflate(R.layout.dialog_create_place, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add post")
            .setView(placeFormView)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Ok", null)
            .show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener{
            val title = placeFormView.findViewById<EditText>(R.id.etTitle).text.toString()
            val description = placeFormView.findViewById<EditText>(R.id.etDescription).text.toString()

            if (title.trim().isEmpty() || description.trim().isEmpty()) {
                Toast.makeText(
                    context,
                    "Fields must not be empty! ",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val query = auth.currentUser?.uid
            val dataToSave: MutableMap<String, Any> =
                HashMap()

            if(!TextUtils.isEmpty(title)) {
                dataToSave["title"] = title
            }
            if(!TextUtils.isEmpty(description)) {
                dataToSave["description"] = description
            }

            dataToSave["latitude"] = latlng.latitude
            dataToSave["longitude"] = latlng.longitude

            if (query != null) {
                dataToSave["userId"] = query
            }

            docRef.collection("markers").add(dataToSave).addOnSuccessListener {
                Log.d("TAG", "Marker is saved! ")
                val marker = map.addMarker(MarkerOptions().position(latlng).title(title).snippet(description))
                markers.add(marker!!)
                dialog.dismiss()
            }.addOnFailureListener { e ->
                Log.w("TAG", "Marker is not saved in database! ", e)
            }

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
