package com.tdtu.finalproject

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tdtu.finalproject.adapter.AchievementAdapter
import com.tdtu.finalproject.databinding.ActivityAchievementBinding
import com.tdtu.finalproject.repository.DataRepository
import com.tdtu.finalproject.utils.Utils
import com.tdtu.finalproject.viewmodel.AchievementViewModel

class AchievementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAchievementBinding
    private lateinit var dataRepository: DataRepository
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var achievementAdapter: AchievementAdapter
    private lateinit var achievementViewModel: AchievementViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAchievementBinding.inflate(layoutInflater)
        dataRepository = DataRepository.getInstance()
        sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_key), MODE_PRIVATE)
        binding.returnBtn.setOnClickListener {
            finish()
        }
        achievementViewModel = ViewModelProvider(this)[AchievementViewModel::class.java]
        achievementViewModel.getAchievementList().observe(this){
            if(it != null){
                achievementAdapter = AchievementAdapter(this, it, R.layout.achievement_item_layout)
                binding.achievementList.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
                binding.achievementList.setHasFixedSize(true)
                binding.achievementList.adapter = achievementAdapter
            }
        }
        dataRepository.getAllAchievements(sharedPreferences.getString(getString(R.string.token_key), "")!!).thenAcceptAsync {
            runOnUiThread {
                achievementViewModel.setAchievementList(it)
            }
        }.exceptionally {
            runOnUiThread {
                Utils.showDialog(Gravity.CENTER, it.message!!, this)
            }
            null
        }
        setContentView(binding.root)
    }
}