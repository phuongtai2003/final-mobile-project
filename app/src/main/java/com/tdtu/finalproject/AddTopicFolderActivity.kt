package com.tdtu.finalproject

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.tdtu.finalproject.adapter.ChooseFolderAdapter
import com.tdtu.finalproject.adapter.FolderAdapter
import com.tdtu.finalproject.databinding.ActivityAddTopicFolderBinding
import com.tdtu.finalproject.model.folder.Folder
import com.tdtu.finalproject.model.topic.Topic
import com.tdtu.finalproject.model.user.User
import com.tdtu.finalproject.repository.DataRepository
import com.tdtu.finalproject.utils.OnDialogConfirmListener
import com.tdtu.finalproject.utils.Utils

class AddTopicFolderActivity : AppCompatActivity(), OnDialogConfirmListener {
    private lateinit var binding: ActivityAddTopicFolderBinding
    private lateinit var topic: Topic
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dataRepository: DataRepository
    private lateinit var adapter: ChooseFolderAdapter
    private lateinit var user: User
    private lateinit var folderList: MutableList<Folder>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTopicFolderBinding.inflate(layoutInflater)
        val data = intent.getParcelableExtra<Topic>("topic")
        if(data == null)
        {
            finish()
        }
        else
        {
            topic = data
        }
        binding.addFolderBtn.setOnClickListener {
            Utils.showCreateFolderDialog(Gravity.CENTER, this, this)
        }
        binding.returnBtn.setOnClickListener {
            finish()
        }
        initViewModel()
        binding.acceptTopicBtn.setOnClickListener {
            val resultIntent = Intent()
            val arrayList: ArrayList<Folder> = ArrayList(folderList.filter { it.isChosen })
            resultIntent.putParcelableArrayListExtra("folders", arrayList)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
        setContentView(binding.root)
    }

    private fun initViewModel()
    {
        dataRepository = DataRepository.getInstance()
        sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_key), MODE_PRIVATE)
        user = Gson().fromJson(sharedPreferences.getString(getString(R.string.user_data_key), ""), User::class.java)
        dataRepository.getFolderByUser(
            user.id,
            sharedPreferences.getString(getString(R.string.token_key), "")!!
        ).thenAcceptAsync {
            folderList = it.toMutableList()
            dataRepository.getFoldersByTopic(
                topic.id!!,
                sharedPreferences.getString(getString(R.string.token_key), null)!!
            ).thenAcceptAsync { usersFolders ->
                if (usersFolders.isNotEmpty()) {
                    for (folder in folderList) {
                        for (userFolder in usersFolders) {
                            if (folder.id == userFolder.id) {
                                folder.isChosen = true
                            }
                        }
                    }
                }
                runOnUiThread {
                    adapter =
                        ChooseFolderAdapter(this, folderList, R.layout.folder_library_item, user)
                    binding.folderRecyclerView.setHasFixedSize(true)
                    binding.folderRecyclerView.layoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    binding.folderRecyclerView.adapter = adapter
                }
            }.exceptionally {
                runOnUiThread {
                    Utils.showDialog(Gravity.CENTER, it.message!!.toString(), this)
                }
                null
            }
        }.exceptionally {
            runOnUiThread {
                Utils.showDialog(Gravity.CENTER, it.message!!.toString(), this)
            }
            null
        }
    }

    override fun onCreateFolderDialogConfirm(
        folderNameEnglish: String,
        folderNameVietnamese: String
    ) {
        dataRepository.createFolder(folderNameEnglish, folderNameVietnamese, sharedPreferences.getString(getString(R.string.token_key), null)!!).thenAcceptAsync {
            dataRepository.getFolderByUser(
                user.id,
                sharedPreferences.getString(getString(R.string.token_key), "")!!
            ).thenAcceptAsync {
                folderList = it.toMutableList()
                dataRepository.getFoldersByTopic(
                    topic.id!!,
                    sharedPreferences.getString(getString(R.string.token_key), null)!!
                ).thenAcceptAsync { usersFolders ->
                    if (usersFolders.isNotEmpty()) {
                        for (folder in folderList) {
                            for (userFolder in usersFolders) {
                                if (folder.id == userFolder.id) {
                                    folder.isChosen = true
                                }
                            }
                        }
                    }
                    runOnUiThread {
                        adapter =
                            ChooseFolderAdapter(this, folderList, R.layout.folder_library_item, user)
                        binding.folderRecyclerView.setHasFixedSize(true)
                        binding.folderRecyclerView.layoutManager =
                            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                        binding.folderRecyclerView.adapter = adapter
                    }
                }.exceptionally {
                    runOnUiThread {
                        Utils.showDialog(Gravity.CENTER, it.message!!.toString(), this)
                    }
                    null
                }
            }.exceptionally {
                runOnUiThread {
                    Utils.showDialog(Gravity.CENTER, it.message!!.toString(), this)
                }
                null
            }
        }.exceptionally {
            runOnUiThread {
                Utils.showDialog(Gravity.CENTER, it.message!!.toString(), this)
            }
            null
        }
    }

    override fun onAddTopicToFolderDialogConfirm() {
        TODO("Not yet implemented")
    }

    override fun onDeleteFolderDialogConfirm() {
        TODO("Not yet implemented")
    }

    override fun onUpgradePremiumDialogConfirm() {
        TODO("Not yet implemented")
    }
}