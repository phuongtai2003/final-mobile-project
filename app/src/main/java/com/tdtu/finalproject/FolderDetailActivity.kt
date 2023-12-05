package com.tdtu.finalproject

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.tdtu.finalproject.adapter.TopicAdapter
import com.tdtu.finalproject.databinding.ActivityFolderDetailBinding
import com.tdtu.finalproject.model.folder.Folder
import com.tdtu.finalproject.model.topic.Topic
import com.tdtu.finalproject.model.user.User
import com.tdtu.finalproject.repository.DataRepository
import com.tdtu.finalproject.utils.CustomOnItemClickListener
import com.tdtu.finalproject.utils.OnDialogConfirmListener
import com.tdtu.finalproject.utils.Utils
import com.tdtu.finalproject.viewmodel.FolderDetailViewModel
import com.tdtu.finalproject.viewmodel.HomeDataViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class FolderDetailActivity : AppCompatActivity() , CustomOnItemClickListener, OnDialogConfirmListener{
    private lateinit var binding: ActivityFolderDetailBinding
    private var folder: Folder? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var user: User
    private lateinit var addTopicResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var adapter: TopicAdapter
    private var topicList: MutableList<Topic> = mutableListOf()
    private lateinit var dataRepository: DataRepository
    private lateinit var folderDataViewModel: FolderDetailViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFolderDetailBinding.inflate(layoutInflater)
        folder = intent.getParcelableExtra("folder")
        folderDataViewModel = ViewModelProvider(this)[FolderDetailViewModel::class.java]
        if(folder == null){
            finish()
        }
        binding.returnBtn.setOnClickListener{
            finish()
        }
        sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_key), MODE_PRIVATE)
        if(sharedPreferences.getString(getString(R.string.user_data_key), null) == null){
            finish()
        }
        else{
            user = Gson().fromJson(sharedPreferences.getString(getString(R.string.user_data_key), null), User::class.java)
        }

        binding.optionMenuBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("folder", folder)
            val bottomSheet = FolderDetailBottomSheet()
            bottomSheet.arguments = bundle
            bottomSheet.show(supportFragmentManager, "folder detail sheet")
        }

        dataRepository = DataRepository.getInstance()
        Picasso.get().load(user.profileImage).into(binding.folderOwnerImg)
        binding.folderOwnerNameTxt.text = user.username
        binding.folderTopicCount.text = folder!!.topicCount.toString() + " " + getString(R.string.topic)
        binding.folderTitleEnglish.text = folder!!.folderNameEnglish

        if(folder!!.topicCount == 0){
            binding.folderDetailRecyclerView.visibility = android.view.View.GONE
            binding.noTopicsInFolderLayout.visibility = android.view.View.VISIBLE
        }
        else{
            binding.folderDetailRecyclerView.visibility = android.view.View.VISIBLE
            binding.noTopicsInFolderLayout.visibility = android.view.View.GONE
            dataRepository.getTopicByFolderId(folder!!.id!!, sharedPreferences.getString(getString(R.string.token_key), null)!!).thenAcceptAsync {
                runOnUiThread {
                    val mutableList = it.toMutableList()
                    topicList = MutableList(mutableList.size){i -> mutableList[i]}
                    adapter = TopicAdapter(this, mutableList, R.layout.topic_library_item, user, this)
                    binding.folderDetailRecyclerView.setHasFixedSize(true)
                    binding.folderDetailRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    binding.folderDetailRecyclerView.adapter = adapter
                }
            }.exceptionally {
                runOnUiThread {
                    Utils.showSnackBar(binding.root, it.message.toString())
                }
                null
            }
        }

        addTopicResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK && it.data != null){
                val topics = it.data!!.getParcelableArrayListExtra<Topic>("topics")
                if(topics != null){
                    val currentList = topicList
                    val addedTopic = topics - currentList
                    val removedTopic = currentList - topics

                    folderDataViewModel.updateTopicForFolder(dataRepository,binding, this, addedTopic, removedTopic, folder!!, sharedPreferences)
                    runOnUiThread {
                        topicList = topics
                        if(topics.isEmpty()){
                            binding.folderTopicCount.text = "0 " + getString(R.string.topic)
                            binding.folderDetailRecyclerView.visibility = android.view.View.GONE
                            binding.noTopicsInFolderLayout.visibility = android.view.View.VISIBLE
                        }
                        else{
                            binding.folderTopicCount.text = topics.size.toString() + " " + getString(R.string.topic)
                            binding.folderDetailRecyclerView.visibility = android.view.View.VISIBLE
                            binding.noTopicsInFolderLayout.visibility = android.view.View.GONE
                            adapter = TopicAdapter(this, topics, R.layout.topic_library_item, user, this)
                            binding.folderDetailRecyclerView.setHasFixedSize(true)
                            binding.folderDetailRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                            binding.folderDetailRecyclerView.adapter = adapter
                        }
                    }
                }
            }
        }


        binding.addTopicToFolderBtn.setOnClickListener {
            val intent = Intent(this, AddTopicToFolderActivity::class.java)
            addTopicResultLauncher.launch(intent)
        }

        setContentView(binding.root)
    }

    override fun onTopicClick(topic: Topic) {
        val intent = Intent(this, TopicActivity::class.java)
        intent.putExtra("topic", topic)
        startActivity(intent)
    }

    override fun onFolderClick(folder: Folder) {

    }

    override fun onCreateFolderDialogConfirm(
        folderNameEnglish: String,
        folderNameVietnamese: String
    ) {
        dataRepository.updateFolder(folder!!.id!!, folderNameEnglish, folderNameVietnamese, sharedPreferences.getString(getString(R.string.token_key), null)!!).thenAcceptAsync {
            runOnUiThread {
                Utils.showDialog(Gravity.CENTER, getString(R.string.update_folder_successfully), this)
                binding.folderTitleEnglish.text = it.folderNameEnglish
            }
        }.exceptionally {
            runOnUiThread{
                Utils.showDialog(Gravity.CENTER, getString(R.string.update_folder_failed), this)
            }
            null
        }
    }

    override fun onAddTopicToFolderDialogConfirm() {
        val intent = Intent(this, AddTopicToFolderActivity::class.java)
        intent.putParcelableArrayListExtra("currentTopics", ArrayList(adapter.getTopics()))
        addTopicResultLauncher.launch(intent)
    }

    override fun onDeleteFolderDialogConfirm() {
        dataRepository.deleteFolder(folder!!.id!!, sharedPreferences.getString(getString(R.string.token_key), null)!!).thenAcceptAsync {
            runOnUiThread {
                finish()
            }
        }.exceptionally {
            runOnUiThread {
                Utils.showDialog(Gravity.CENTER, getString(R.string.remove_folder_failed), this)
            }
            null
        }
    }
}