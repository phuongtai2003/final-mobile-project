package com.tdtu.finalproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.tdtu.finalproject.databinding.ActivityHomeBinding
import com.tdtu.finalproject.utils.OnBottomNavigationChangeListener
import com.tdtu.finalproject.utils.OnDrawerNavigationPressedListener

class HomeActivity : AppCompatActivity(), OnBottomNavigationChangeListener, OnDrawerNavigationPressedListener {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var sharedPref : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = getSharedPreferences(getString(R.string.shared_preferences_key), Context.MODE_PRIVATE)
        loadFragment(HomePageFragment())
        binding.bottomNavbar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeActionItem -> {
                    loadFragment(HomePageFragment())
                    true
                }
                R.id.profileActionItem -> {
                    loadFragment(ProfileFragment())
                    true
                }

                else -> {
                    false
                }
            }
        }

        binding.drawerNavigation.setNavigationItemSelectedListener{
            when(it.itemId){
                R.id.logoutBtn ->{
                    with(sharedPref.edit()){
                        remove(getString(R.string.user_data_key))
                        apply()
                    }
                    val intro = Intent(this, MainActivity::class.java)
                    intro.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intro)
                    true
                }
                else -> {
                    false
                }
            }
        }

    }
    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.tempContainer,fragment)
        transaction.commit()
    }

    override fun changeBottomNavigationItem(itemIndex: Int) {
        binding.bottomNavbar.selectedItemId = when(itemIndex){
            R.id.homePageFragment -> R.id.homeActionItem
            R.id.profileFragment -> R.id.profileActionItem
            else -> R.id.homeActionItem
        }
    }

    override fun openDrawerFromFragment() {
        binding.homeDrawerLayout.openDrawer(GravityCompat.START)
    }
}