package com.tdtu.finalproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.tdtu.finalproject.databinding.ActivityHomeBinding
import com.tdtu.finalproject.model.User
import com.tdtu.finalproject.utils.OnBottomNavigationChangeListener
import com.tdtu.finalproject.utils.OnDrawerNavigationPressedListener
import com.tdtu.finalproject.viewmodel.HomeDataViewModel

class HomeActivity : AppCompatActivity(), OnBottomNavigationChangeListener, OnDrawerNavigationPressedListener {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var sharedPref : SharedPreferences
    private lateinit var userViewModel: HomeDataViewModel
    private val profileFragment : Fragment = ProfileFragment()
    private val homeFragment: Fragment = HomePageFragment()
    private val libraryFragment: Fragment = LibraryFragment()
    private val searchFragment: Fragment = SearchFragment()
    private var activeFragment: Fragment = homeFragment
    private val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = getSharedPreferences(getString(R.string.shared_preferences_key), Context.MODE_PRIVATE)
        if(sharedPref.getString(getString(R.string.user_data_key), null) == null){
            backtoIntro()
            return
        }
        else{
            userViewModel = ViewModelProvider(this)[HomeDataViewModel::class.java]
            userViewModel.setUser(Gson().fromJson(sharedPref.getString(getString(R.string.user_data_key), null), User::class.java));
        }
        supportFragmentManager.beginTransaction().apply {
            add(R.id.tempContainer, homeFragment)
            add(R.id.tempContainer, searchFragment).hide(searchFragment)
            add(R.id.tempContainer, libraryFragment).hide(libraryFragment)
            add(R.id.tempContainer, profileFragment).hide(profileFragment)
        }.commit()

        binding.bottomNavbar.menu[2].isEnabled = false

        binding.bottomNavbar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeActionItem -> {
                    fragmentManager.beginTransaction().hide(activeFragment).show(homeFragment).commit()
                    activeFragment = homeFragment
                    true
                }
                R.id.profileActionItem -> {
                    fragmentManager.beginTransaction().hide(activeFragment).show(profileFragment).commit()
                    activeFragment = profileFragment
                    true
                }
                R.id.libraryActionItem ->{
                    fragmentManager.beginTransaction().hide(activeFragment).show(libraryFragment).commit()
                    activeFragment = libraryFragment
                    true
                }
                R.id.searchActionItem ->{
                    fragmentManager.beginTransaction().hide(activeFragment).show(searchFragment).commit()
                    activeFragment = searchFragment
                    true
                }
                else -> {
                    false
                }
            }
        }

        binding.fab.setOnClickListener {
            val bottomSheet = MainBottomSheetFragment()
            bottomSheet.show(supportFragmentManager, "bottomSheet")
        }

        binding.drawerNavigation.setNavigationItemSelectedListener{
            when(it.itemId){
                R.id.logoutBtn ->{
                    with(sharedPref.edit()){
                        remove(getString(R.string.user_data_key))
                        remove(getString(R.string.token_key))
                        apply()
                    }
                    backtoIntro()
                    true
                }
                else -> {
                    false
                }
            }
        }

    }
    private fun backtoIntro(){
        val intro = Intent(this, MainActivity::class.java)
        intro.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intro)
    }
    override fun changeBottomNavigationItem(itemIndex: Int) {
        binding.bottomNavbar.selectedItemId = when(itemIndex){
            R.id.homePageFragment -> R.id.homeActionItem
            R.id.profileFragment -> R.id.profileActionItem
            R.id.libraryFragment -> R.id.libraryActionItem
            R.id.searchFragment -> R.id.searchActionItem
            else -> R.id.homeActionItem
        }
    }

    override fun openDrawerFromFragment() {
        binding.homeDrawerLayout.openDrawer(GravityCompat.START)
    }
}