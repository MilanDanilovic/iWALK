package elfak.mosis.iwalk

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import elfak.mosis.iwalk.map.MapFragment

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer : DrawerLayout
    private lateinit var addPost : ImageView
    private lateinit var notifications : ImageView
    private lateinit var mapPin : ImageView
    private lateinit var mapOptions : ImageView

    private val db = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth =  Firebase.auth

    private lateinit var profileImageSideBar : CircleImageView
    private lateinit var usernameSideBar : TextView
    private lateinit var emailSideBar : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitivity_home)

        drawer = findViewById(R.id.drawer_layout);
        addPost = findViewById(R.id.addposts)
        notifications = findViewById(R.id.notifications)
        mapPin = findViewById(R.id.map)
        mapOptions = findViewById(R.id.map_options)
        mapOptions.visibility = View.GONE

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val ctx: Context = this

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }

        addPost.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddPostFragment()).commit()
        }

        notifications.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NotifiactionsFragment()).commit()
        }

        mapPin.setOnClickListener{
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MapFragment()).commit()
        }

        val headerView = navigationView.getHeaderView(0)
        usernameSideBar = headerView.findViewById<TextView>(R.id.usrnameNav)
        emailSideBar = headerView.findViewById<TextView>(R.id.emailNav)
        profileImageSideBar = headerView.findViewById<CircleImageView>(R.id.profilePicture)

        val usersReference = db.collection("users").document(auth.currentUser?.uid!!)
        usersReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    usernameSideBar.setText(document["username"].toString())
                    emailSideBar.setText(document["email"].toString())
                    if (document["profileImageUrl"].toString() != "default") Picasso.get().load(
                        document["profileImageUrl"].toString()
                    ).into(profileImageSideBar)
                    Log.d("TAG:", "DocumentSnapshot data: " + document.data)
                } else {
                    Log.d("TAG:", "No such document")
                }
            } else {
                Log.d("TAG", "get failed with ", task.exception)
            }
        }

    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.nav_home -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
            R.id.nav_friends -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AllFriendsFragment()).commit()
            R.id.nav_profile -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment()).commit()
            R.id.nav_favourite_walkers -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FavouriteWalkersFragment()).commit()
            R.id.nav_my_pets -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MyPetsFragment()).commit()
            R.id.nav_posts -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PostsFragment()).commit()
            R.id.nav_walks -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, WalksFragment()).commit()
            R.id.nav_logout -> {
                Firebase.auth.signOut()
                val i: Intent = Intent(this, MainActivity::class.java)
                startActivity(i)
            }
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

}