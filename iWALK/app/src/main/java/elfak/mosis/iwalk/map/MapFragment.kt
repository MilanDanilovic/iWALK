package elfak.mosis.iwalk.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import elfak.mosis.iwalk.R
import elfak.mosis.iwalk.databinding.FragmentMapBinding
import kotlinx.coroutines.*
import org.w3c.dom.Text
import java.io.InputStream
import java.net.URL
import java.net.URLConnection
import java.text.SimpleDateFormat
import java.time.Month
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.*


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

    data class PostMarkerFromDatabase(
        val description:String? = null,
        val latitude: Number? = null,
        val longitude: Number? = null,
        val title: String? = null,
        val userId: String? = null,
        val dogImage1Url: String? = null,
        val dogImage2Url: String? = null,
        val desiredWalkTime: String?=null
    )
    var listOfOtherUsers:MutableList<UserFromDatabase> = mutableListOf<UserFromDatabase>()

    private var returnValue:Boolean=true
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private var auth: FirebaseAuth =  Firebase.auth
	private var postMarkers: MutableList<Marker> = mutableListOf()
    private lateinit var mapOptions : ImageView
    private val arrayChecked = booleanArrayOf(true,false)
    private val mapOptionsArray = arrayOf("Show users","Filter")
    private var arrayCheckedFilterDialog = booleanArrayOf(false,false)
    private val mapOptionsArrayFilterDialog = arrayOf("Score","Distance")
    private var showUsersFlag = true
    private var radius = 500.00
    private var distanceFilter = false
    private var scoreFilter = false

    private var isCameraInitiallySet: Boolean = false
    private lateinit var map: GoogleMap
    private val LOCATION_PERMISSION_REQUEST=1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private lateinit var imageUri: Uri
    private lateinit var imageUri2: Uri
    private var imageUrl : String? = null
    private var imageUrl2 : String? = null
    private lateinit var dogImage1 : ImageView
    private lateinit var dogImage2 : ImageView
    private lateinit var pickTimeButton: Button
    private lateinit var tvPostTime: TextView
    private lateinit var pickDateButton: Button
    private lateinit var tvPostDate: TextView

    private val docRef = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().reference



    private var currentUserMarker: Marker? = null
    private var listOfOtherUsersMarkers:MutableList<Marker> =  mutableListOf<Marker>()

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
                        val customInfoWindowAdapter = context?.let { CustomInfoWindowAdapter(it) }
                        map.setInfoWindowAdapter(customInfoWindowAdapter)
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



                        showPostMarkers(currentUserMarker!!)

                        markerOnInfoWindowClick(map)

                        getAllUsersFromDatabase()

                        if(listOfOtherUsersMarkers.isNotEmpty()) {
                            var valuesMarkesToRemove:MutableList<Marker> =  mutableListOf<Marker>()

                            for (marker in listOfOtherUsersMarkers) {
                                valuesMarkesToRemove.add(marker)
                                marker.remove()
                            }
                            listOfOtherUsersMarkers.removeAll(valuesMarkesToRemove)
                        }

                        for(user in listOfOtherUsers) {
                            if (user.latitude != null && user.longitude != null && showUsersFlag) {
                                val markerOptions: MarkerOptions
                                val userLatLng =
                                    LatLng(user.latitude as Double, user.longitude as Double)
                                val urlToUserProfileImage = user.profileImageUrl
                                val userMarker: Marker
                                val userDescription = "Full name: "+user.name +" "+user.surname+"\r\n"+"Email: "+user.email+
                                        "\r\n"+"Number of walks: "+user.numberOfWalks+"\r\n"+"Score: "+user.score
                                //START distance section
                                if(!distanceFilter) {
                                    if (URLUtil.isValidUrl(urlToUserProfileImage)) {
                                        val imageURL = URL(urlToUserProfileImage)
                                        val connection: URLConnection = imageURL.openConnection()
                                        val iconStream: InputStream = connection.getInputStream()
                                        val bmp = BitmapFactory.decodeStream(iconStream)
                                        val resizedBitmap = getResizedBitmap(bmp, 200)
                                        val croppedBitmap = getCroppedBitmap(resizedBitmap)

                                        markerOptions = MarkerOptions().position(userLatLng)
                                            .icon(BitmapDescriptorFactory.fromBitmap(croppedBitmap))
                                            .flat(true)
                                            .anchor(0.5f, 0.5f)
                                            .title("Username: " + user.username)
                                            .snippet(userDescription)
                                        userMarker = map.addMarker(markerOptions)!!
                                        listOfOtherUsersMarkers.add(userMarker)

                                    } else {
                                        markerOptions = MarkerOptions().position(userLatLng)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_user))
                                            .flat(true)
                                            .anchor(0.5f, 0.5f)
                                            .title("Username: " + user.username)
                                            .snippet(userDescription)
                                        userMarker = map.addMarker(markerOptions)!!
                                        listOfOtherUsersMarkers.add(userMarker)
                                    }
                                } else{
                                    if(getDistance(currentUserMarker!!.position.latitude, currentUserMarker!!.position.longitude
                                            ,userLatLng.latitude,userLatLng.longitude)<radius)
                                    if (URLUtil.isValidUrl(urlToUserProfileImage)) {
                                        val imageURL = URL(urlToUserProfileImage)
                                        val connection: URLConnection = imageURL.openConnection()
                                        val iconStream: InputStream = connection.getInputStream()
                                        val bmp = BitmapFactory.decodeStream(iconStream)
                                        val resizedBitmap = getResizedBitmap(bmp, 200)
                                        val croppedBitmap = getCroppedBitmap(resizedBitmap)

                                        markerOptions = MarkerOptions().position(userLatLng)
                                            .icon(BitmapDescriptorFactory.fromBitmap(croppedBitmap))
                                            .flat(true)
                                            .anchor(0.5f, 0.5f)
                                            .title("Username: " + user.username)
                                            .snippet(userDescription)
                                        userMarker = map.addMarker(markerOptions)!!
                                        listOfOtherUsersMarkers.add(userMarker)

                                    } else {
                                        markerOptions = MarkerOptions().position(userLatLng)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_user))
                                            .flat(true)
                                            .anchor(0.5f, 0.5f)
                                            .title("Username: " + user.username)
                                            .snippet(userDescription)
                                        userMarker = map.addMarker(markerOptions)!!
                                        listOfOtherUsersMarkers.add(userMarker)
                                    }
                                }
                                //END distance section

                            }
                        }

                    }
                }
            }
        }
    }

    private fun showPostMarkers(currentUserMakrer: Marker){
        if(postMarkers.isNotEmpty()) {
            var valuesPostMarkesToRemove:MutableList<Marker> =  mutableListOf<Marker>()

            for (marker in postMarkers) {
                valuesPostMarkesToRemove.add(marker)
                marker.remove()
            }
            postMarkers.removeAll(valuesPostMarkesToRemove)
        }

        val documentReference = docRef.collection("markers")
        documentReference.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        if (document["status"] == "OPEN") {
                            val markerFromDatabase = PostMarkerFromDatabase(
                                document.getString("description"),
                                document.get("latitude") as Number?,
                                document.get("longitude") as Number?,
                                document.getString("title"),
                                document.getString("userId"),
                                document.getString("dogImage1Url"),
                                document.getString("dogImage2Url"),
                                document.getString("date")+" "+document.getString("time")
                            )
                            val latLng = LatLng(
                                markerFromDatabase.latitude as Double,
                                markerFromDatabase.longitude as Double
                            )
                            val marker = map.addMarker(
                                MarkerOptions().position(latLng).title(markerFromDatabase.title)
                                    .snippet(markerFromDatabase.description+"\r\n"+"Should be walked on: "+markerFromDatabase.desiredWalkTime)
                            )
                            postMarkers.add(marker!!)
                            displayAcceptNearPostDialog(currentUserMarker!!,marker)
                        }
                    }
                }
            }
            .addOnFailureListener { e->
                Log.d(TAG, "Markers not fetched: $e")
            }
    }

    private fun checkIfUserIsCloseToPostMarker(userMarker:Marker, postMarker: Marker):Boolean{
        val distanceBetweenMarkers = getDistance(userMarker.position.latitude,userMarker.position.longitude,
            postMarker.position.latitude,postMarker.position.longitude)

        return distanceBetweenMarkers<50
    }

    private fun checkIfPostMarkerBelongsToCurrentUser(postMarker: Marker) : Boolean{

        val documentReference = docRef.collection("markers")
        documentReference.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        if (document["userId"] == auth.currentUser?.uid && document["latitude"] == postMarker.position.latitude && document["longitude"] == postMarker.position.longitude) {
                            returnValue = true
                            break
                        }else{
                            returnValue = false
                        }
                    }
                }
            }
            .addOnFailureListener { e->
                Log.d(TAG, "Markers not fetched: $e")
            }
        return returnValue
    }

    private fun updateUserPost(documentReference:DocumentReference, walkerId: String){
        documentReference.update(
            "status", "IN_PROGRESS",
            "walkerId", walkerId
        ).addOnCompleteListener(OnCompleteListener<Void?> { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    context,
                    "Data successfully updated.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context,
                    "Error updating data.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun acceptUserPost(postMarker: Marker){
        val documentReference = docRef.collection("markers")
        documentReference.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        if (document["userId"] != auth.currentUser?.uid && document["latitude"] == postMarker.position.latitude && document["longitude"] == postMarker.position.longitude) {
                            CoroutineScope(GlobalScope.coroutineContext).launch(Dispatchers.IO) {

                                withContext(Dispatchers.Main) {
                                    updateUserPost(document.reference,auth.currentUser?.uid.toString())
                                }
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { e->
                Log.d(TAG, "Markers not fetched: $e")
            }
    }



    private fun displayAcceptNearPostDialog(userMarker:Marker,postMarker: Marker){
        if(checkIfUserIsCloseToPostMarker(userMarker,postMarker) && !checkIfPostMarkerBelongsToCurrentUser(postMarker)) {
            lateinit var dialog: AlertDialog
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Accept post: " + postMarker.title)
            builder.setMessage("Message: " + postMarker.snippet)
            builder.setNegativeButton("No") { _, _ ->
                Toast.makeText(requireContext(), "Declined", Toast.LENGTH_SHORT).show()
            }
            builder.setPositiveButton("OK") { _, _ ->
                acceptUserPost(postMarker)
                Toast.makeText(requireContext(), "Accepted", Toast.LENGTH_SHORT).show()
            }

            dialog = builder.create()
            dialog.show()
        }
    }

    fun getDistance(
        LAT1: Double,
        LONG1: Double,
        LAT2: Double,
        LONG2: Double
    ): Double {
        return 2 * 6371000 * asin(
            sqrt(
                sin((LAT2 * (3.14159 / 180) - LAT1 * (3.14159 / 180)) / 2).pow(2.0) + cos(LAT2 * (3.14159 / 180)) * cos(LAT1 * (3.14159 / 180)) * sin(
                    ((LONG2 * (3.14159 / 180) - LONG1 * (3.14159 / 180)) / 2).pow(2.0)
                )
            )
        )
    }

    private fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    private fun getCroppedBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawCircle(
            (bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat(),
            (bitmap.width / 2).toFloat(), paint
        )
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
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
                    listOfOtherUsers.removeAll(listOfOtherUsers)
                    for (document in task.result) {
                        if (document.id != auth.currentUser?.uid) {
                            val userFromDatabase = UserFromDatabase(
                                document.getString("email"),
                                document.get("latitude") as Number?,
                                document.get("longitude") as Number?,
                                document.getString("name"),
                                document.get("numberOfWalks").toString(),
                                document.getString("phone"),
                                document.getString("profileImageUrl"),
                                document.get("score").toString(),
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

        //todo not great solution :)
        val policy = ThreadPolicy.Builder()
            .permitAll().build()
        StrictMode.setThreadPolicy(policy)

        mapOptions = requireActivity().findViewById(R.id.map_options)
        mapOptions.visibility = View.VISIBLE

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.onResume()

        binding.mapView.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        Snackbar.make(requireView(), "Long press to add a marker", Snackbar.LENGTH_SHORT)
            .setAction("OK", {})
            .setActionTextColor(ContextCompat.getColor(requireContext(),android.R.color.white))
            .show()

        //START of part of code is for mapOptions button of main toolbar
        mapOptions.setOnClickListener{

            lateinit var dialog:AlertDialog
            lateinit var dialogFilter: AlertDialog

            val builder = AlertDialog.Builder(requireContext())
            val builderFilter = AlertDialog.Builder(requireContext())

            builder.setTitle("Map options")
                //.setCancelable(false)
            builder.setMultiChoiceItems(mapOptionsArray,arrayChecked) { dialog, which, isChecked ->
                arrayChecked[which] = isChecked
                val valueChecked = mapOptionsArray[which]
            }

            builder.setPositiveButton("OK") { _, _ ->
                // Do something when click positive button
                for (i in mapOptionsArray.indices) {
                    val checked = arrayChecked[i]
                    val showUsersString = "Show users"
                    if (checked) {
                        if(mapOptionsArray[i] == showUsersString){
                            showUsersFlag = true
                        }else{
                            dialogFilter.show()
                        }
                    }else{
                        if(mapOptionsArray[i] == showUsersString){
                            showUsersFlag = false
                        }else{
                            for(index in arrayCheckedFilterDialog.indices){
                                arrayCheckedFilterDialog[index] = false
                                scoreFilter = false
                                distanceFilter = false
                            }
                        }
                    }
                }
            }
            builder.setNeutralButton("Cancel"){ _,_-> }

            builderFilter.setTitle("Filter users")
            builderFilter.setMultiChoiceItems(mapOptionsArrayFilterDialog,arrayCheckedFilterDialog) { dialogFilter, which, isChecked ->
                arrayCheckedFilterDialog[which] = isChecked
                val valueChecked = mapOptionsArrayFilterDialog[which]
            }

            builderFilter.setPositiveButton("OK") { _, _ ->
                // Do something when click positive button
                for (i in mapOptionsArrayFilterDialog.indices) {
                    val checked = arrayCheckedFilterDialog[i]
                    val scoreString = "Score"
                    if (checked) {
                        if(mapOptionsArrayFilterDialog[i] == scoreString){
                            scoreFilter = true
                            Toast.makeText(requireContext(), "Score", Toast.LENGTH_SHORT).show()
                        }else{
                            distanceFilter = true
//                            Toast.makeText(requireContext(), "Distance", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        if(mapOptionsArrayFilterDialog[i] == scoreString){
                            scoreFilter = false
//                            Toast.makeText(requireContext(), "Remove score", Toast.LENGTH_SHORT).show()
                        }else{
                            distanceFilter = false
//                            Toast.makeText(requireContext(), "Remove distance", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            builderFilter.setNeutralButton("Cancel"){ _,_-> }

            dialogFilter = builderFilter.create()

            dialog = builder.create()
            dialog.show()

        }
        //END of mapOptionsPart

    }

    private fun markerOnInfoWindowClick(mMap: GoogleMap){
        mMap.setOnInfoWindowClickListener { markerToDelete ->
            Log.i("TAG", "onWindowClickListener - delete marker")

            val markersRef: CollectionReference = docRef.collection("markers")
            markersRef.get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            if (document["userId"] == auth.currentUser?.uid && document["latitude"] == markerToDelete.position.latitude && document["longitude"] == markerToDelete.position.longitude) {
                                val markersRefDelete: DocumentReference = docRef.collection("markers").document(document.id.toString())
                                markersRefDelete
                                    .delete()
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            docRef.collection("markers").document(document.id)
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
                                            postMarkers.remove(markerToDelete)
                                            markerToDelete.remove()
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
    }

    override fun onMapReady(googleMap: GoogleMap) {

        markerOnInfoWindowClick(googleMap)

        googleMap.setOnMapLongClickListener { latLng ->
            Log.i("TAG", "onMapLongClickListener")
            showAlertDialog(latLng)
        }
        googleMap.let {
            map = it
            getLocationAccess()
        }
    }

    private fun showAlertDialog(latLng: LatLng) {
        val placeFormView = LayoutInflater.from(context).inflate(R.layout.dialog_create_place, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add post")
            .setView(placeFormView)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Ok", null)
            .show()

        dogImage1 = placeFormView.findViewById<ImageView>(R.id.dogPictureOneCreatePlace)
        dogImage2 = placeFormView.findViewById<ImageView>(R.id.dogPictureTwoCreatePlace)
        pickTimeButton = placeFormView.findViewById<Button>(R.id.pickTimeButton)
        tvPostTime = placeFormView.findViewById<TextView>(R.id.postTimeTextView)
        pickDateButton = placeFormView.findViewById<Button>(R.id.pickDateButton)
        tvPostDate = placeFormView.findViewById(R.id.postDatePickerTextView)

        //Calendar
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        pickDateButton.setOnClickListener {
            val dpd = DatePickerDialog( requireContext(),DatePickerDialog.OnDateSetListener { view: DatePicker, mYear: Int, mMOnth: Int, mDay: Int ->
                val dateTextValue = "$mDay/${mMOnth+1}/$mYear"
                tvPostDate.text= dateTextValue

            },year,month,day)
            dpd.show()
        }

        pickTimeButton.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener{ _: TimePicker, hour:Int, minute:Int->
                cal.set(Calendar.HOUR_OF_DAY,hour)
                cal.set(Calendar.MINUTE,minute)

                tvPostTime.text = SimpleDateFormat("HH:mm").format(cal.time)
            }
            TimePickerDialog(requireContext(),timeSetListener,cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),true).show()
        }

        dogImage1.setOnClickListener{
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            resultLauncherImage1.launch(intent)
        }

        dogImage2.setOnClickListener{
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            resultLauncherImage2.launch(intent)
        }

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
            if(!TextUtils.isEmpty(imageUrl)){
                dataToSave["dogImage1Url"] = imageUrl!!
                imageUrl=""
                imageUri = Uri.EMPTY
            }
            if(!TextUtils.isEmpty(imageUrl2)){
                dataToSave["dogImage2Url"] = imageUrl2!!
                imageUrl2=""
                imageUri2= Uri.EMPTY
            }
            if(!TextUtils.isEmpty(tvPostTime.text.toString())) {
                dataToSave["time"] = tvPostTime.text.toString()
            }
            if(!TextUtils.isEmpty(tvPostDate.text.toString())) {
                dataToSave["date"] = tvPostDate.text.toString()
            }
            dataToSave["timeOfPosting"] = Calendar.getInstance().time
            dataToSave["status"] = "OPEN"
            dataToSave["walkerId"] = ""



            dataToSave["latitude"] = latLng.latitude
            dataToSave["longitude"] = latLng.longitude

            if (query != null) {
                dataToSave["userId"] = query
            }

            docRef.collection("markers").add(dataToSave).addOnSuccessListener {
                Log.d("TAG", "Marker is saved! ")
                val marker = map.addMarker(MarkerOptions().position(latLng).title(title).snippet(description))
                postMarkers.add(marker!!)
                dialog.dismiss()
            }.addOnFailureListener { e ->
                Log.w("TAG", "Marker is not saved in database! ", e)
            }

        }

    }
    var resultLauncherImage1 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            if (data != null) {
                imageUri = data.data!!
                dogImage1.setImageURI(imageUri)
                val bitmap = MediaStore.Images.Media.getBitmap(this@MapFragment.context?.contentResolver, imageUri)
                dogImage1.setImageBitmap(bitmap)
                uploadImage()
            }
        }
    }

    var resultLauncherImage2 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            if (data != null) {
                imageUri2 = data.data!!
                dogImage2.setImageURI(imageUri2)
                val bitmap = MediaStore.Images.Media.getBitmap(this@MapFragment.context?.contentResolver, imageUri2)
                dogImage2.setImageBitmap(bitmap)
                uploadImage2()
            }
        }
    }

    private fun uploadImage() {
        val ref = storage.child("postsPetImages1/" + System.currentTimeMillis())
        val uploadTask = ref.putFile(imageUri)
        uploadTask.addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { uri ->

                imageUrl = uri.toString()

            }
        }
    }

    private fun uploadImage2() {
        val ref = storage.child("postsPetImages2/" + System.currentTimeMillis())
        val uploadTask = ref.putFile(imageUri2)
        uploadTask.addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { uri ->

                imageUrl2 = uri.toString()

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
        mapOptions.visibility = View.GONE
        super.onDestroyView()
        _binding = null
    }



}
