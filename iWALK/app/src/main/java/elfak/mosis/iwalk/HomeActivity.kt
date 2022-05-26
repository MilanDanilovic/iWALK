package elfak.mosis.iwalk

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer : DrawerLayout
    private lateinit var register : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitivity_home)

        drawer = findViewById(R.id.drawer_layout);

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
            R.id.nav_home -> Toast.makeText(baseContext, "Morate se registrovati kako biste imali pristup! ", Toast.LENGTH_SHORT).show()
            R.id.nav_friends -> Toast.makeText(baseContext, "Morate se registrovati kako biste imali pristup! ", Toast.LENGTH_SHORT).show()
            R.id.nav_profile -> Toast.makeText(baseContext, "Morate se registrovati kako biste imali pristup! ", Toast.LENGTH_SHORT).show()
            R.id.nav_favourite_walkers -> Toast.makeText(baseContext, "Morate se registrovati kako biste imali pristup! ", Toast.LENGTH_SHORT).show()
            R.id.nav_active_walkers -> Toast.makeText(baseContext, "Morate se registrovati kako biste imali pristup! ", Toast.LENGTH_SHORT).show()
            R.id.nav_my_pets -> Toast.makeText(baseContext, "Morate se registrovati kako biste imali pristup! ", Toast.LENGTH_SHORT).show()
            R.id.nav_posts -> Toast.makeText(baseContext, "Morate se registrovati kako biste imali pristup! ", Toast.LENGTH_SHORT).show()
            R.id.nav_logout -> Toast.makeText(baseContext, "Morate se registrovati kako biste imali pristup! ", Toast.LENGTH_SHORT).show()
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}