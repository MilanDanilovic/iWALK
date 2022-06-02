package elfak.mosis.iwalk

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer : DrawerLayout
    private lateinit var addPost : ImageView
    private lateinit var notifications : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitivity_home)

        drawer = findViewById(R.id.drawer_layout);
        addPost = findViewById(R.id.addposts)
        notifications = findViewById(R.id.notifications)

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
            R.id.nav_active_walkers -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ActiveUsersFragment()).commit()
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