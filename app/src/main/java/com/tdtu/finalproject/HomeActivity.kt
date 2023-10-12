package com.tdtu.finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.tdtu.finalproject.databinding.ActivityHomeBinding
import com.tdtu.finalproject.utils.OnBottomNavigationChangeListener

class HomeActivity : AppCompatActivity(), OnBottomNavigationChangeListener {
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
}