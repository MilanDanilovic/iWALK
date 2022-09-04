package elfak.mosis.iwalk.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.telephony.RadioAccessSpecifier
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import elfak.mosis.iwalk.R
import elfak.mosis.iwalk.map.MapFragment
import elfak.mosis.iwalk.services.helpers.NotificationHelpers
import org.imperiumlabs.geofirestore.GeoFirestore
import org.imperiumlabs.geofirestore.GeoLocation
import org.imperiumlabs.geofirestore.GeoQuery
import org.imperiumlabs.geofirestore.extension.setLocation
import org.imperiumlabs.geofirestore.listeners.GeoQueryDataEventListener

class LocationService : Service() {

    private val MIN_TIME_MS: Long = 1000
    private val MIN_DISTANCE: Float = Float.MIN_VALUE
    private val RADIUS: Double = 50 * 0.001

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    private val usersRef = Firebase.firestore.collection("users")
    private val postsRef = Firebase.firestore.collection("markers")
    private val geoFirestoreUser = GeoFirestore(usersRef)
    private val geoFirestorePost = GeoFirestore(postsRef)

    private lateinit var usersGeoQuery: GeoQuery
    private lateinit var postsGeoQuery: GeoQuery

    private var isConfiguredAndRunning: Boolean = false

    companion object {
        var isServiceStarted: Boolean = false
    }

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                configureLocationListener(location)
            }

            override fun onProviderEnabled(provider: String) {
                if(!isConfiguredAndRunning)
                    createGeoQueries()
            }
            override fun onProviderDisabled(provider: String) { Log.i("providerDisabled", provider) }
        }

        createGeoQueries()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.i("Service", "Location service is started...")
        isServiceStarted = true

        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            MIN_TIME_MS, MIN_DISTANCE, locationListener)

        val notification = createServiceNotification()
        startForeground(3, notification)

        return START_STICKY

    }


    private fun createServiceNotification() : Notification {

        var notificationManager : NotificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel: NotificationChannel = NotificationChannel(
            "foreground", "serviceChannel", NotificationManager.IMPORTANCE_HIGH)

        notificationManager.createNotificationChannel(channel)

        Log.d(TAG, "Location service started")
        return  NotificationCompat.Builder(this, "foreground")
            .setContentTitle("Location service is finding users and posts...")
            .setColor(ContextCompat.getColor(this, R.color.purple_500))
            .setSmallIcon(R.drawable.ic_radar)
            .build()

    }


    private fun configureLocationListener(location: Location) {

        Log.i("Service", "Location read...")
        val loggedUserId: String = Firebase.auth.currentUser!!.uid
        val userLocation = GeoPoint(location.latitude, location.longitude)
        //sets location of current user
        geoFirestoreUser.setLocation(loggedUserId, userLocation) { err ->
            if(err == null) {
                if(isConfiguredAndRunning) {
                    usersGeoQuery.center = userLocation
                    postsGeoQuery.center = userLocation
                }
                Log.i("locResult", "location added")
            }
            else
                Log.i("locResult", "location not added")
        }
        updateLocationOfTheUsersAndPosts(loggedUserId)

    }

    private fun updateLocationOfTheUsersAndPosts(loggedUserId:String){

        //set location of all users
        usersRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        if (document.id != loggedUserId) {
                            if( document.get("latitude") != null) {
                                val someUserLocation = GeoPoint(
                                    document.get("latitude") as Double,
                                    document.get("longitude") as Double
                                )
                                geoFirestoreUser.setLocation(document.id, someUserLocation) { err ->
                                    if (err == null) {
                                        Log.i("locResult", "location added")
                                    } else
                                        Log.i("locResult", "location not added")
                                }
                            }
                        }
                    }
                }
            }
        //end

        //set location of all posts
        postsRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        if (document.getString("userId") != loggedUserId) {
                            if( document.get("latitude") != null) {
                                val somePostLocation = GeoPoint(
                                    document.get("latitude") as Double,
                                    document.get("longitude") as Double
                                )
                                geoFirestorePost.setLocation(document.id, somePostLocation) { err ->
                                    if (err == null) {
                                        Log.i("locResult", "location added")
                                    } else
                                        Log.i("locResult", "location not added")
                                }
                            }
                        }
                    }
                }
            }
        //end
    }

    @SuppressLint("MissingPermission")
    private fun createGeoQueries() {

        usersRef.document(Firebase.auth.currentUser!!.uid).get()

            .addOnSuccessListener { userDoc ->
                val userLocation = userDoc.getGeoPoint("l")

                if(userLocation != null) {
                    configureUsersGeoQuery(userLocation)
                    configurePostsGeoQuery(userLocation)
                    isConfiguredAndRunning = true
                }
                else {
                    val lastKnown = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    if(lastKnown != null) {
                        configureUsersGeoQuery(GeoPoint(lastKnown.latitude, lastKnown.longitude))
                        configurePostsGeoQuery(GeoPoint(lastKnown.latitude, lastKnown.longitude))
                        isConfiguredAndRunning = true
                    }
                }
            }

    }


    private fun configureUsersGeoQuery(userLocation: GeoPoint) {

        val context : Context = this
        usersGeoQuery = geoFirestoreUser.queryAtLocation(userLocation, RADIUS)
        usersGeoQuery.addGeoQueryDataEventListener(object: GeoQueryDataEventListener {
            override fun onDocumentChanged(documentSnapshot: DocumentSnapshot, location: GeoPoint) {
                val userId : String = documentSnapshot.id
                if(userId != Firebase.auth.currentUser!!.uid){
                    val firstName = documentSnapshot.data?.get("name")
                    val lastName = documentSnapshot.data?.get("surname")
                    val username = documentSnapshot.data?.get("username")
                    Log.i("notify", "user $username just entered your area")
                    NotificationHelpers.pushNotification(context, "Another user is close to you",
                        "${firstName} ${lastName} with username: ${username} is nearby. Meet up and talk about your pets!")
                }
            }

            override fun onDocumentEntered(documentSnapshot: DocumentSnapshot, location: GeoPoint) {

                val userId : String = documentSnapshot.id
                if(userId != Firebase.auth.currentUser!!.uid){
                    val firstName = documentSnapshot.data?.get("name")
                    val lastName = documentSnapshot.data?.get("surname")
                    val username = documentSnapshot.data?.get("username")
                    Log.i("notify", "user $username just entered your area")
                    NotificationHelpers.pushNotification(context, "Another user is close to you",
                        "${firstName} ${lastName} with username: ${username} is nearby. Meet up and talk about your pets!")
                }

            }
            override fun onDocumentExited(documentSnapshot: DocumentSnapshot) {}
            override fun onDocumentMoved(documentSnapshot: DocumentSnapshot, location: GeoPoint) {
                val userId : String = documentSnapshot.id
                if(userId != Firebase.auth.currentUser!!.uid){
                    val firstName = documentSnapshot.data?.get("name")
                    val lastName = documentSnapshot.data?.get("surname")
                    val username = documentSnapshot.data?.get("username")
                    Log.i("notify", "user $username just entered your area")
                    NotificationHelpers.pushNotification(context, "Another user is close to you",
                        "${firstName} ${lastName} with username: ${username} is nearby. Meet up and talk about your pets!")
                }
            }

            override fun onGeoQueryError(exception: Exception) { Log.i("GeoQueryUser", "error ${exception.message}")}
            override fun onGeoQueryReady() { Log.i("GeoQueryUser", "GeoQueryReady") }
        })

    }

    private fun configurePostsGeoQuery(userLocation: GeoPoint) {

        val context: Context = this
        postsGeoQuery = geoFirestorePost.queryAtLocation(userLocation, RADIUS)
        postsGeoQuery.addGeoQueryDataEventListener(object : GeoQueryDataEventListener {
            override fun onDocumentChanged(documentSnapshot: DocumentSnapshot, location: GeoPoint) {
                val title = documentSnapshot.data?.get("title")
                if(documentSnapshot.data?.get("status")=="OPEN"&&documentSnapshot.data?.get("userId")!=Firebase.auth.currentUser!!.uid) {
                    NotificationHelpers.pushNotificationForPost(
                        context, "Pet post detected nearby",
                        "A pet post, ${title}, was detected near your location. Check it!"
                    )
                }
            }

            override fun onDocumentEntered(documentSnapshot: DocumentSnapshot, location: GeoPoint) {
                val title = documentSnapshot.data?.get("title")
                if(documentSnapshot.data?.get("status")=="OPEN"&&documentSnapshot.data?.get("userId")!=Firebase.auth.currentUser!!.uid) {
                    NotificationHelpers.pushNotificationForPost(
                        context, "Pet post detected nearby",
                        "A pet post, ${title}, was detected near your location. Check it!"
                    )
                }
            }

            override fun onDocumentExited(documentSnapshot: DocumentSnapshot) {}
            override fun onDocumentMoved(documentSnapshot: DocumentSnapshot, location: GeoPoint) {
                val title = documentSnapshot.data?.get("title")
                if(documentSnapshot.data?.get("status")=="OPEN"&&documentSnapshot.data?.get("userId")!=Firebase.auth.currentUser!!.uid) {
                    NotificationHelpers.pushNotificationForPost(
                        context, "Pet post detected nearby",
                        "A pet post, ${title}, was detected near your location. Check it!"
                    )
                }
            }

            override fun onGeoQueryError(exception: Exception) { Log.i("GeoQueryUser", "error ${exception.message}")}
            override fun onGeoQueryReady() { Log.i("GeoQueryUser", "GeoQueryReady") }

        })
    }



    override fun onDestroy() {
        locationManager.removeUpdates(locationListener)
        usersGeoQuery.removeAllListeners()
        postsGeoQuery.removeAllListeners()
        Log.i("ServiceStopped", "Stopping location service...")
        isServiceStarted = false
        super.onDestroy()
    }
}
