package com.tdtu.finalproject

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import com.tdtu.finalproject.adapter.VocabularyFlashCardAdapter
import com.tdtu.finalproject.databinding.ActivityTopicBinding
import com.tdtu.finalproject.model.folder.Folder
import com.tdtu.finalproject.model.topic.Topic
import com.tdtu.finalproject.model.vocabulary.Vocabulary
import com.tdtu.finalproject.repository.DataRepository
import com.tdtu.finalproject.utils.OnTopicDialogListener
import com.tdtu.finalproject.utils.StudyMode
import com.tdtu.finalproject.utils.Utils
import com.tdtu.finalproject.viewmodel.TopicViewModel
import java.util.Locale


class TopicActivity : AppCompatActivity(), TextToSpeech.OnInitListener, OnTopicDialogListener {
    private lateinit var binding: ActivityTopicBinding
    private lateinit var topicViewModel: TopicViewModel
    private lateinit var dataRepository: DataRepository
    private lateinit var topic: Topic
    private lateinit var vocabulariesList: List<Vocabulary>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var vocabulariesAdapter: VocabularyFlashCardAdapter
    private var ttsEnglish: TextToSpeech? = null
    private var ttsVietnamese: TextToSpeech? = null
    private lateinit var addTopicToFolderResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var editTopicVocabulariesResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopicBinding.inflate(layoutInflater)
        val data = intent.getParcelableExtra<Topic>("topic")
        ttsEnglish = TextToSpeech(this, this)
        ttsVietnamese = TextToSpeech(this, this)
        if(data == null)
        {
            finish()
        }
        else
        {
            topic = data
        }
        binding.optionMenuBtn.setOnClickListener {
            val bottomSheet = TopicBottomSheet()
            val bundle = Bundle()
            bundle.putParcelable("topic", topic)
            bottomSheet.arguments = bundle
            bottomSheet.show(supportFragmentManager, "topicBottomSheet")
        }
        binding.vocabularyCountTxt.text = topic.vocabularyCount.toString() + " " + getString(R.string.vocabulary)
        binding.topicNameEnglishTxt.text = topic.topicNameEnglish
        binding.topicNameVietnameseTxt.text = topic.topicNameVietnamese
        initViewModel()
        binding.returnBtn.setOnClickListener {
            finish()
        }
        binding.learnByQuizBtn.setOnClickListener {
            val intent = Intent(this, StudyConfigurationActivity::class.java)
            intent.putExtra("topic", topic)
            intent.putExtra("studyMode", StudyMode.Quiz)
            startActivity(intent)
        }
        editTopicVocabulariesResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK && data != null){
                val currentVocabList = vocabulariesList
                val returnResult = it.data?.getParcelableArrayListExtra<Vocabulary>("vocabularies")
                val titleEnglishResult = it.data?.getStringExtra("titleEnglish")
                val titleVietnameseResult = it.data?.getStringExtra("titleVietnamese")
                val topicDescriptionEnglishResult = it.data?.getStringExtra("topicDescriptionEnglish")
                val topicDescriptionVietnameseResult = it.data?.getStringExtra("topicDescriptionVietnamese")
                if(returnResult != null){
                    binding.fullScreenProgressBar.visibility = View.VISIBLE
                    val newVocabulariesList = returnResult - currentVocabList
                    val deletedVocabulariesList = currentVocabList - returnResult
                    val matchingVocabulariesList = currentVocabList.intersect(returnResult)
                    for (vocab in newVocabulariesList){
                        dataRepository.addVocabularyToTopic(topic.id!!, sharedPreferences.getString(getString(R.string.token_key), null)!!, vocab).exceptionally {error->
                            runOnUiThread {
                                Utils.showDialog(Gravity.CENTER, error.message!!.toString(), this)
                            }
                            null
                        }
                    }
                    for(vocab in deletedVocabulariesList){
                        dataRepository.deleteVocabularyFromTopic(topic.id!!, sharedPreferences.getString(getString(R.string.token_key), null)!!, vocab.id!!).exceptionally {error->
                            runOnUiThread {
                                Utils.showDialog(Gravity.CENTER, error.message!!.toString(), this)
                            }
                            null
                        }
                    }
                    for (vocab in matchingVocabulariesList){
                        dataRepository.editVocabularyInTopic(topic.id!!, sharedPreferences.getString(getString(R.string.token_key), null)!!, vocab).exceptionally {error->
                            runOnUiThread {
                                Utils.showDialog(Gravity.CENTER, error.message!!.toString(), this)
                            }
                            null
                        }
                    }
                    dataRepository.updateTopic(topic.id!!, sharedPreferences.getString(getString(R.string.token_key), null)!!, Topic(null, titleEnglishResult, titleVietnameseResult, 0, false, 0, 0, topicDescriptionEnglishResult, topicDescriptionVietnameseResult, null, null, null, null, null, false)).thenAcceptAsync {res->
                        if(res == null){
                            runOnUiThread {
                                Utils.showDialog(Gravity.CENTER, getString(R.string.update_topic_failed), this)
                            }
                            return@thenAcceptAsync
                        }
                    }.exceptionally {error->
                        runOnUiThread {
                            Utils.showDialog(Gravity.CENTER, error.message!!.toString(), this)
                        }
                        null
                    }
                    runOnUiThread {
                        binding.fullScreenProgressBar.visibility = View.GONE
                        topicViewModel.setVocabulariesList(returnResult)
                        binding.vocabularyCountTxt.text = returnResult.size.toString() + " " + getString(R.string.vocabulary)
                        binding.topicNameEnglishTxt.text = titleEnglishResult
                        binding.topicNameVietnameseTxt.text = titleVietnameseResult
                    }
                }
            }
        }
        addTopicToFolderResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ it ->
            if(it.resultCode == RESULT_OK && data != null){
                val folders = it.data?.getParcelableArrayListExtra<Folder>("folders")
                if(!folders.isNullOrEmpty()){
                    binding.fullScreenProgressBar.visibility = View.VISIBLE
                    runOnUiThread {
                        for (folder in folders){
                            dataRepository.addTopicToFolder(folder.id!!, topic.id!!, sharedPreferences.getString(getString(R.string.token_key), null)!!).exceptionally {error->
                                Utils.showDialog(Gravity.CENTER, error.message!!.toString(), this)
                                null
                            }
                        }
                        binding.fullScreenProgressBar.visibility = View.GONE
                    }
                }
            }
        }
        setContentView(binding.root)
    }

    private fun initViewModel(){
        topicViewModel = ViewModelProvider(this)[TopicViewModel::class.java]
        topicViewModel.setVocabulariesList(ArrayList())
        dataRepository = DataRepository.getInstance()
        sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_key), MODE_PRIVATE)
        binding.progressBar.visibility = View.VISIBLE
        dataRepository.getVocabulariesByTopic(topic.id!!, sharedPreferences.getString(getString(R.string.token_key), null)!!).thenAcceptAsync { it ->
            runOnUiThread {
                topicViewModel.setVocabulariesList(it)
                topicViewModel.getVocabularies().observe(this){items->
                    vocabulariesList = items
                    vocabulariesAdapter = VocabularyFlashCardAdapter(true, this, items, R.layout.vocabulary_flash_card_layout, ttsEnglish, ttsVietnamese)
                    binding.flashCardViewPager.adapter = vocabulariesAdapter
                    binding.flashCardViewPager.offscreenPageLimit = 3
                    binding.flashCardViewPager.clipToPadding = false
                    binding.scrollPagerIndicator.attachToPager(binding.flashCardViewPager)
                }
                binding.progressBar.visibility = View.GONE
            }
        }.exceptionally {
            runOnUiThread{
                Utils.showDialog(Gravity.CENTER, it.message!!.toString(), this)
                binding.progressBar.visibility = View.GONE
            }
            null
        }
        dataRepository.getUserById(topic.ownerId!!, sharedPreferences.getString(getString(R.string.token_key), null)!!).thenAcceptAsync {
            runOnUiThread {
                Picasso.get().load(it.profileImage).into(binding.topicOwnerImg)
                binding.topicOwnerNameTxt.text = it.username
            }
        }.exceptionally {
            runOnUiThread{
                Utils.showDialog(Gravity.CENTER, it.message!!.toString(), this)
            }
            null
        }
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){
            val result = ttsEnglish!!.setLanguage(Locale.US)
            if(result == TextToSpeech.LANG_MISSING_DATA){
                Utils.showDialog(Gravity.CENTER, getString(R.string.language_not_supported), this)
            }
            val res = ttsVietnamese!!.setLanguage(Locale("vi"))
            if(res == TextToSpeech.LANG_MISSING_DATA){
                Utils.showDialog(Gravity.CENTER, getString(R.string.language_not_supported), this)
            }
        }
        else{
            Utils.showDialog(Gravity.CENTER, getString(R.string.tts_init_failed), this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(ttsEnglish != null){
            ttsEnglish!!.stop()
            ttsEnglish!!.shutdown()
        }
        if(ttsVietnamese != null){
            ttsVietnamese!!.stop()
            ttsVietnamese!!.shutdown()
        }
    }

    override fun onSaveToFolder() {
        val intent = Intent(this, AddTopicFolderActivity::class.java)
        intent.putExtra("topic", topic)
        addTopicToFolderResultLauncher.launch(intent)
    }

    override fun onDeleteTopic() {
        binding.fullScreenProgressBar.visibility = View.VISIBLE
        dataRepository.deleteTopic(topic.id!!, sharedPreferences.getString(getString(R.string.token_key), null)!!).thenAcceptAsync {
            runOnUiThread {
                Utils.showDialog(Gravity.CENTER, getString(R.string.delete_topic_success), this)
                binding.fullScreenProgressBar.visibility = View.GONE
                finish()
            }
        }.exceptionally {
            runOnUiThread{
                Utils.showDialog(Gravity.CENTER, it.message!!.toString(), this)
                binding.fullScreenProgressBar.visibility = View.GONE
            }
            null
        }
    }

    override fun onEditTopic() {
        val intent  = Intent(this, AddTopicActivity::class.java)
        intent.putExtra("topic", topic)
        intent.putExtra("isEdit", true)
        editTopicVocabulariesResultLauncher.launch(intent)
    }
}