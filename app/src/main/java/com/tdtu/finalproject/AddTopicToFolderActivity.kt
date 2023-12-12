package com.tdtu.finalproject

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.tdtu.finalproject.adapter.ChooseTopicAdapter
import com.tdtu.finalproject.adapter.TopicAdapter
import com.tdtu.finalproject.databinding.ActivityAddTopicToFolderBinding
import com.tdtu.finalproject.model.folder.Folder
import com.tdtu.finalproject.model.topic.Topic
import com.tdtu.finalproject.model.user.User
import com.tdtu.finalproject.repository.DataRepository
import com.tdtu.finalproject.utils.CustomOnItemClickListener
import com.tdtu.finalproject.utils.Utils
import com.tdtu.finalproject.viewmodel.HomeDataViewModel
import kotlin.math.log

class AddTopicToFolderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTopicToFolderBinding
    private lateinit var dataRepository: DataRepository
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var currentUser: User
    private lateinit var adapter: ChooseTopicAdapter
    private lateinit var currentTopics: MutableList<Topic>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTopicToFolderBinding.inflate(layoutInflater)
        binding.returnBtn.setOnClickListener {
            finish()
        }
        initData()
        binding.acceptTopicBtn.setOnClickListener {
            val intent = Intent()
            val arrayList: ArrayList<Topic> = ArrayList(adapter.getTopics().filter { it.chosen })
            intent.putParcelableArrayListExtra("topics", arrayList)
            setResult(RESULT_OK, intent)
            finish()
        }
        setContentView(binding.root)
    }
    private fun initData(){
        dataRepository = DataRepository.getInstance()
        sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_key), MODE_PRIVATE)
        if(sharedPreferences.getString(getString(R.string.user_data_key), null) == null){
            finish()
        }
        else{
            currentUser = Gson().fromJson(sharedPreferences.getString(getString(R.string.user_data_key), null), User::class.java)
        }
        currentTopics = intent.getParcelableArrayListExtra<Topic>("currentTopics")?.toMutableList()!!
        Log.d("USER TAG", "initData: ${currentTopics}")

        dataRepository.getTopicsByUserId(currentUser.id, sharedPreferences.getString(getString(R.string.token_key), null)!!).thenAcceptAsync {
            runOnUiThread {
                val mutableList = it.toMutableList()
                currentTopics.forEach { topic ->
                    topic.chosen = true
                }
                val currentTopicIds = currentTopics.map { it.id }
                mutableList.forEach { topic ->
                    if(currentTopicIds.contains(topic.id)){
                        topic.chosen = true
                    }
                }
                for(topic in mutableList){
                    if(!currentTopics.contains(topic)){
                        currentTopics.add(topic)
                    }
                }
                adapter  = ChooseTopicAdapter(this, currentTopics, R.layout.topic_library_item)
                binding.chosenTopicRecyclerView.setHasFixedSize(true)
                binding.chosenTopicRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.chosenTopicRecyclerView.adapter = adapter
            }
        }.exceptionally {
            Utils.showSnackBar(binding.root, it.message.toString())
            null
        }
    }
}