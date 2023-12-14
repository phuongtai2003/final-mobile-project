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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Locale


class TopicActivity : AppCompatActivity(), TextToSpeech.OnInitListener, OnTopicDialogListener {
    private lateinit var binding: ActivityTopicBinding
    private lateinit var topicViewModel: TopicViewModel
    private lateinit var dataRepository: DataRepository
    private lateinit var topic: Topic
    private lateinit var vocabulariesList: List<Vocabulary>
    private lateinit var originalVocabulariesList: List<Vocabulary>
    private lateinit var bookmarkedVocabulariesList: List<Vocabulary>
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
        dataRepository = DataRepository.getInstance()
        sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_key), MODE_PRIVATE)
        binding.optionMenuBtn.setOnClickListener {
            val bottomSheet = TopicBottomSheet()
            val bundle = Bundle()
            bundle.putParcelable("topic", topic)
            val user = Utils.getUserFromSharedPreferences(this, sharedPreferences)
            val isYourTopic = topic.ownerId!!.id == user.id
            bundle.putBoolean("isYourTopic", isYourTopic)
            bottomSheet.arguments = bundle
            bottomSheet.show(supportFragmentManager, "topicBottomSheet")
        }
        binding.vocabularyCountTxt.text = topic.vocabularyCount.toString() + " " + getString(R.string.vocabulary)
        binding.topicNameEnglishTxt.text = topic.topicNameEnglish
        binding.topicNameVietnameseTxt.text = topic.topicNameVietnamese
        topicViewModel = ViewModelProvider(this)[TopicViewModel::class.java]
        topicViewModel.setVocabulariesList(ArrayList())
        initViewModel()
        binding.returnBtn.setOnClickListener {
            finish()
        }
        binding.rankingBtn.setOnClickListener {
            val intent = Intent(this, TopicRankingActivity::class.java)
            intent.putExtra("topic", topic)
            startActivity(intent)
        }
        binding.learnByQuizBtn.setOnClickListener {
            val intent = Intent(this, StudyConfigurationActivity::class.java)
            intent.putExtra("topic", topic)
            intent.putExtra("studyMode", StudyMode.Quiz)
            intent.putParcelableArrayListExtra("vocabularies", ArrayList(vocabulariesList))
            intent.putParcelableArrayListExtra("bookmarkedVocabularies", ArrayList(bookmarkedVocabulariesList))
            startActivity(intent)
        }
        binding.learnByTypingBtn.setOnClickListener {
            val intent = Intent(this, StudyConfigurationActivity::class.java)
            intent.putExtra("topic", topic)
            intent.putExtra("studyMode", StudyMode.Typing)
            intent.putParcelableArrayListExtra("vocabularies", ArrayList(vocabulariesList))
            intent.putParcelableArrayListExtra("bookmarkedVocabularies", ArrayList(bookmarkedVocabulariesList))
            startActivity(intent)
        }
        binding.learnByFlashCardBtn.setOnClickListener {
            val intent = Intent(this, FlashCardActivity::class.java)
            intent.putExtra("topic", topic)
            intent.putExtra("bookmarkedVocabularies", ArrayList(bookmarkedVocabulariesList))
            intent.putParcelableArrayListExtra("vocabularies", ArrayList(vocabulariesList))
            startActivity(intent)
        }
        editTopicVocabulariesResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK && data != null){
                val currentVocabList = ArrayList(vocabulariesList)
                val returnResult = it.data?.getParcelableArrayListExtra<Vocabulary>("vocabularies")
                val titleEnglishResult = it.data?.getStringExtra("titleEnglish")
                val titleVietnameseResult = it.data?.getStringExtra("titleVietnamese")
                val topicDescriptionEnglishResult = it.data?.getStringExtra("topicDescriptionEnglish")
                val topicDescriptionVietnameseResult = it.data?.getStringExtra("topicDescriptionVietnamese")
                if(returnResult != null){
                    binding.fullScreenProgressBar.visibility = View.VISIBLE
                    val newVocabulariesList = returnResult - currentVocabList
                    val deletedVocabulariesList = currentVocabList - returnResult
                    val matchingVocabulariesList = returnResult.intersect(currentVocabList)

                    val scope = CoroutineScope(Dispatchers.Main)
                    scope.launch{
                        val addVocabJobs = newVocabulariesList.map { vocab ->
                            scope.async {
                                dataRepository.addVocabularyToTopic(topic.id!!, sharedPreferences.getString(getString(R.string.token_key), null)!!, vocab)
                                    .exceptionally { error ->
                                        runOnUiThread {
                                            Utils.showDialog(Gravity.CENTER, error.message!!.toString(), this@TopicActivity)
                                        }
                                        null
                                    }
                            }
                        }
                        val addVocabResults = addVocabJobs.awaitAll()
                        val deleteVocabJobs = deletedVocabulariesList.map { vocab ->
                            async {
                                dataRepository.deleteVocabularyFromTopic(topic.id!!, sharedPreferences.getString(getString(R.string.token_key), null)!!, vocab.id!!)
                                    .exceptionally { error ->
                                        runOnUiThread {
                                            Utils.showDialog(Gravity.CENTER, error.message!!.toString(), this@TopicActivity)
                                        }
                                        null
                                    }
                            }
                        }
                        val deleteVocabResults = deleteVocabJobs.awaitAll()

                        val editVocabJobs = matchingVocabulariesList.map { vocab ->
                            async {
                                dataRepository.editVocabularyInTopic(topic.id!!, sharedPreferences.getString(getString(R.string.token_key), null)!!, vocab)
                                    .exceptionally { error ->
                                        runOnUiThread {
                                            Utils.showDialog(Gravity.CENTER, error.message!!.toString(), this@TopicActivity)
                                        }
                                        null
                                    }
                            }
                        }

                        val editVocabResults = editVocabJobs.awaitAll()

                        val updateJob = async {
                            dataRepository.updateTopic(topic.id!!, sharedPreferences.getString(getString(R.string.token_key), null)!!, Topic(null, titleEnglishResult, titleVietnameseResult, 0, false, 0, 0, topicDescriptionEnglishResult, topicDescriptionVietnameseResult, null, null, null, null, null, false)).thenAcceptAsync {res->
                                if(res == null){
                                    runOnUiThread {
                                        Utils.showDialog(Gravity.CENTER, getString(R.string.update_topic_failed), this@TopicActivity)
                                    }
                                    return@thenAcceptAsync
                                }
                            }.exceptionally {error->
                                runOnUiThread {
                                    Utils.showDialog(Gravity.CENTER, error.message!!.toString(), this@TopicActivity)
                                }
                                null
                            }
                        }
                        updateJob.await()
                        runOnUiThread {
                            initViewModel()
                            binding.fullScreenProgressBar.visibility = View.GONE
                            binding.topicNameEnglishTxt.text = titleEnglishResult
                            binding.topicNameVietnameseTxt.text = titleVietnameseResult
                        }
                    }.invokeOnCompletion {
                        scope.cancel()
                    }
                }
            }
        }
        addTopicToFolderResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ it ->
            if(it.resultCode == RESULT_OK && data != null){
                val folders = it.data?.getParcelableArrayListExtra<Folder>("folders")
                if(!folders.isNullOrEmpty()){
                    binding.fullScreenProgressBar.visibility = View.VISIBLE
                    val scope = CoroutineScope(Dispatchers.Main)
                    scope.launch {
                        val job = scope.async {
                            runOnUiThread {
                                for (folder in folders){
                                    dataRepository.addTopicToFolder(folder.id!!, topic.id!!, sharedPreferences.getString(getString(R.string.token_key), null)!!).exceptionally {error->
                                        Utils.showDialog(Gravity.CENTER, error.message!!.toString(), this@TopicActivity)
                                        binding.fullScreenProgressBar.visibility = View.GONE
                                        null
                                    }
                                }
                            }
                        }
                        job.await()
                        binding.fullScreenProgressBar.visibility = View.GONE
                    }.invokeOnCompletion {
                        scope.cancel()
                    }
                }
            }
        }
        binding.studyAllOption.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                topicViewModel.setVocabulariesList(originalVocabulariesList)
            }
            else{
                topicViewModel.setVocabulariesList(bookmarkedVocabulariesList)
            }
        }
        topicViewModel.getVocabularies().observe(this){items->
            vocabulariesList = items
            vocabulariesAdapter = VocabularyFlashCardAdapter(true, this, items, R.layout.vocabulary_flash_card_layout, ttsEnglish, ttsVietnamese)
            binding.flashCardViewPager.adapter = vocabulariesAdapter
            binding.flashCardViewPager.offscreenPageLimit = 3
            binding.flashCardViewPager.clipToPadding = false
            binding.scrollPagerIndicator.attachToPager(binding.flashCardViewPager)
            if(items.size < 2){
                binding.learnByTypingBtn.visibility = View.GONE
                binding.learnByQuizBtn.visibility = View.GONE
            }
            else{
                binding.learnByTypingBtn.visibility = View.VISIBLE
                binding.learnByQuizBtn.visibility = View.VISIBLE
            }
        }
        setContentView(binding.root)
    }

    private fun initViewModel(){
        binding.progressBar.visibility = View.VISIBLE
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            val getVocabJob = async {
                dataRepository.getVocabulariesByTopic(
                    topic.id!!,
                    sharedPreferences.getString(getString(R.string.token_key), null)!!
                ).thenAcceptAsync { it ->
                    runOnUiThread {
                        binding.vocabularyCountTxt.text =
                            it.size.toString() + " " + getString(R.string.vocabulary)
                        topicViewModel.setVocabulariesList(it)
                        originalVocabulariesList = ArrayList(it)
                    }
                }.exceptionally {
                    runOnUiThread {
                        Utils.showDialog(Gravity.CENTER, it.message!!.toString(), this@TopicActivity)
                    }
                    null
                }
            }
            getVocabJob.await()
            val getBookmarkedJob = async {
                dataRepository.getBookmarkVocabulariesInTopic(sharedPreferences.getString(getString(R.string.token_key), null)!!, topic.id!!).thenAcceptAsync {
                    runOnUiThread {
                        bookmarkedVocabulariesList = it
                        if(bookmarkedVocabulariesList.isNotEmpty()){
                            binding.studyByOptions.visibility = View.VISIBLE
                        }
                        else{
                            binding.studyByOptions.visibility = View.GONE
                        }
                    }
                }.exceptionally {
                    runOnUiThread{
                        Utils.showDialog(Gravity.CENTER, it.message!!.toString(), this@TopicActivity)
                    }
                    null
                }
            }
            getBookmarkedJob.await()

            val getUserDataJob = async {
                dataRepository.getUserById(topic.ownerId!!.id, sharedPreferences.getString(getString(R.string.token_key), null)!!).thenAcceptAsync {
                    runOnUiThread {
                        Picasso.get().load(it.profileImage).into(binding.topicOwnerImg)
                        binding.topicOwnerNameTxt.text = it.username
                    }
                }.exceptionally {
                    runOnUiThread{
                        Utils.showDialog(Gravity.CENTER, it.message!!.toString(), this@TopicActivity)
                    }
                    null
                }
            }
            getUserDataJob.await()
            Utils.getUserFromSharedPreferences(this@TopicActivity, sharedPreferences).let {
                user->
                val job = async {
                    if (topic.userId?.contains(user.id) == true) {
                        dataRepository.getTopicStatisticsByUser(sharedPreferences.getString(getString(R.string.token_key), null)!!, topic.id!!, user.id).thenAcceptAsync {
                            runOnUiThread {
                                binding.userStatisticsLayout.visibility = View.VISIBLE
                                binding.topicLearningCountTxt.text = "${getString(R.string.learning_count)}: ${it.learningCount}"
                                binding.vocabulariesLearnedTxt.text = "${getString(R.string.vocabularies_learned)}: ${it.vocabLearned}"
                                val hours = it.learningTime / 3600
                                val minutes = (it.learningTime % 3600) / 60
                                val remainingSeconds = it.learningTime % 60
                                binding.timeLearnedTxt.text = "${getString(R.string.time_learned)}: ${String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)}"
                                val percentage = (it.learningPercentage * 100).toInt()
                                binding.progressTxt.text = "$percentage%"
                                binding.progressIndicator.progress = percentage
                            }
                        }.exceptionally {
                            runOnUiThread {
                                Utils.showDialog(Gravity.CENTER, it.message!!.toString(), this@TopicActivity)
                            }
                            null
                        }
                    } else {
                        runOnUiThread {
                            binding.userStatisticsLayout.visibility = View.GONE
                        }
                    }
                }
                job.await()
            }
        }.invokeOnCompletion {
            scope.cancel()
            runOnUiThread {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        initViewModel()
        binding.studyAllOption.isChecked = true
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