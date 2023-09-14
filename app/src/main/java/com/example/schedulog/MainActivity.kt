package com.example.schedulog

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.schedulog.databinding.ActivityMainBinding



class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val drawerLayout = binding.drawerLayout
        val navigationView = binding.navigationView
        val btnHamburger = binding.btnHamburger

        //drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        // Open the navigation drawer when the hamburger icon is clicked
        btnHamburger.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Handle navigation item clicks
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Handle clicks on menu items here
            when (menuItem.itemId) {
                R.id.nav_item_1 -> {
                    val fragment = AccountProfileFragment()
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragmentContainer, fragment)
                    transaction.addToBackStack(null) // Optional, for back navigation
                    transaction.commit()
                }
                R.id.nav_item_2 -> {
                    val fragment = FeedFragment()
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragmentContainer, fragment)
                    transaction.addToBackStack(null) // Optional, for back navigation
                    transaction.commit()
                }
            }
            // Close the drawer
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        if (savedInstanceState == null) {
            // Only add the EntryFragment if it's not already added (prevents overlapping fragments)
            val fragmentManager: FragmentManager = supportFragmentManager
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, EntryFragment())
            transaction.addToBackStack(null) // Optional, for navigation
            transaction.commit()
        }


    }

    //fun handleDrawerLocking(isLoginSuccessful: Boolean) {
        //val drawerLayout = binding.drawerLayout
        //if (isLoginSuccessful) {
            //drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        //} else {
            //drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        //}
    //}

    override fun onBackPressed() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
