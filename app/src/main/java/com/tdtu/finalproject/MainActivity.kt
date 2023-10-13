package com.tdtu.finalproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.tdtu.finalproject.adapter.IntroAdapter
import com.tdtu.finalproject.databinding.ActivityMainBinding
import com.tdtu.finalproject.model.PageViewItem
import com.tdtu.finalproject.model.UserInfo

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var introList: ArrayList<PageViewItem>
    private lateinit var adapter: IntroAdapter
    private lateinit var handler: Handler
    private lateinit var sharedPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initPageViewer()

        binding.loginBtn.setOnClickListener{
            val loginIntent: Intent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
        binding.registerBtn.setOnClickListener{
            val registerIntent: Intent = Intent(this, RegisterActivity::class.java)
            startActivity(registerIntent)
        }

        sharedPref = getSharedPreferences(getString(R.string.shared_preferences_key), Context.MODE_PRIVATE)
        val userJson : String? = sharedPref.getString(getString(R.string.user_data_key), null)
        if(userJson != null && Gson().fromJson(userJson, UserInfo::class.java) != null){
            val homeScreen: Intent = Intent(this, HomeActivity::class.java)
            homeScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(homeScreen)
        }
    }

    private fun initPageViewer(){
        handler = Handler(Looper.myLooper()!!)
        introList = ArrayList()
        introList.add(PageViewItem(R.drawable.intro_image, "Quizlet is a web tool and a mobile app that boosts students learning through several study tools that include flashcards and game-based quizzes"))
        introList.add(PageViewItem(R.drawable.intro_image1, "Quizlet is a web tool and a mobile app that boosts students learning through several study tools that include flashcards and game-based quizzes"))
        introList.add(PageViewItem(R.drawable.intro_image2, "Quizlet is a web tool and a mobile app that boosts students learning through several study tools that include flashcards and game-based quizzes"))
        introList.add(PageViewItem(R.drawable.intro_image3, "Quizlet is a web tool and a mobile app that boosts students learning through several study tools that include flashcards and game-based quizzes"))

        adapter = IntroAdapter(this, R.layout.page_view_item, introList)

        binding.introViewPager.adapter = adapter
        binding.introViewPager.offscreenPageLimit = 3
        binding.introViewPager.clipToPadding = false
        binding.introViewPager.clipChildren = false
        binding.introViewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        binding.introViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                changeColor()
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                changeColor()
            }
        })

    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
    }

    private fun changeColor(){
        when(binding.introViewPager.currentItem){
            0->{
                binding.indicator1.setImageResource(R.color.white)
                binding.indicator2.setImageResource(R.color.primary_color)
                binding.indicator3.setImageResource(R.color.primary_color)
                binding.indicator4.setImageResource(R.color.primary_color)
            }
            1->{
                binding.indicator1.setImageResource(R.color.primary_color)
                binding.indicator2.setImageResource(R.color.white)
                binding.indicator3.setImageResource((R.color.primary_color))
                binding.indicator4.setImageResource((R.color.primary_color))
            }
            2->{
                binding.indicator1.setImageResource((R.color.primary_color))
                binding.indicator2.setImageResource((R.color.primary_color))
                binding.indicator3.setImageResource((R.color.white))
                binding.indicator4.setImageResource((R.color.primary_color))
            }
            3->{
                binding.indicator1.setImageResource((R.color.primary_color))
                binding.indicator2.setImageResource((R.color.primary_color))
                binding.indicator3.setImageResource((R.color.primary_color))
                binding.indicator4.setImageResource((R.color.white))
            }

        }
    }
}