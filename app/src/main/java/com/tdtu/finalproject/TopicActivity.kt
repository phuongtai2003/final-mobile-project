package com.tdtu.finalproject

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.tdtu.finalproject.adapter.VocabularyFlashCardAdapter
import com.tdtu.finalproject.databinding.ActivityTopicBinding
import com.tdtu.finalproject.model.topic.Topic
import com.tdtu.finalproject.repository.DataRepository
import com.tdtu.finalproject.utils.Utils
import com.tdtu.finalproject.viewmodel.TopicViewModel
import java.util.Locale


class TopicActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var binding: ActivityTopicBinding
    private lateinit var topicViewModel: TopicViewModel
    private lateinit var dataRepository: DataRepository
    private lateinit var topic: Topic
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var vocabulariesAdapter: VocabularyFlashCardAdapter
    private var ttsEnglish: TextToSpeech? = null
    private var ttsVietnamese: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopicBinding.inflate(layoutInflater)


        val data = intent.getParcelableExtra<Topic>("topic")
        if(data == null)
        {
            finish()
        }
        else
        {
            topic = data
        }
        ttsEnglish = TextToSpeech(this, this)
        ttsVietnamese = TextToSpeech(this, this)
        initViewModel()
        binding.returnBtn.setOnClickListener {
            finish()
        }

        setContentView(binding.root)
    }

    private fun initViewModel(){
        topicViewModel = ViewModelProvider(this)[TopicViewModel::class.java]
        topicViewModel.setVocabulariesList(ArrayList())
        dataRepository = DataRepository.getInstance()
        sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_key), MODE_PRIVATE)
        dataRepository.getVocabulariesByTopic(topic.id!!, sharedPreferences.getString(getString(R.string.token_key), null)!!).thenAcceptAsync { it ->
            runOnUiThread {
                topicViewModel.setVocabulariesList(it)
                topicViewModel.getVocabularies().observe(this){items->
                    vocabulariesAdapter = VocabularyFlashCardAdapter(true, this, items, R.layout.vocabulary_flash_card_layout, ttsEnglish, ttsVietnamese)
                    binding.flashCardViewPager.adapter = vocabulariesAdapter
                    binding.flashCardViewPager.offscreenPageLimit = 3
                    binding.flashCardViewPager.clipToPadding = false
                    binding.scrollPagerIndicator.attachToPager(binding.flashCardViewPager)
                }
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
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Utils.showDialog(Gravity.CENTER, getString(R.string.language_not_supported), this)
            }
            val res = ttsVietnamese!!.setLanguage(Locale("vi"))
            if(res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED){
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
}