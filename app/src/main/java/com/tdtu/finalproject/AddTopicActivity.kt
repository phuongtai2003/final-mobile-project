package com.tdtu.finalproject

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.PopupMenu
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.tdtu.finalproject.adapter.VocabularyAdapter
import com.tdtu.finalproject.databinding.ActivityAddTopicBinding
import com.tdtu.finalproject.model.topic.Topic
import com.tdtu.finalproject.model.vocabulary.Vocabulary
import com.tdtu.finalproject.model.user.User
import com.tdtu.finalproject.repository.DataRepository
import com.tdtu.finalproject.utils.Utils

class AddTopicActivity : BaseActivity() {
    private var downloadConditions = DownloadConditions.Builder().requireWifi().build()
    private var engToVietOptions = TranslatorOptions.Builder().setSourceLanguage(TranslateLanguage.ENGLISH).setTargetLanguage(
        TranslateLanguage.VIETNAMESE).build()
    private val engToVietTranslator = Translation.getClient(engToVietOptions)
    private lateinit var binding: ActivityAddTopicBinding
    private lateinit var vocabularyList: ArrayList<Vocabulary>
    private lateinit var vocabularyAdapter: VocabularyAdapter
    private val PICK_FILE_REQUEST_CODE = 1
    private val dataRepository: DataRepository = DataRepository()
    private lateinit var sharedPref: SharedPreferences
    private lateinit var user : User
    private var topic: Topic? = null
    private var isEdit = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTopicBinding.inflate(layoutInflater)
        binding.returnBtn.setOnClickListener{
            finish()
        }

        sharedPref = this.getSharedPreferences(getString(R.string.shared_preferences_key), MODE_PRIVATE)
        user = Gson().fromJson(sharedPref.getString(getString(R.string.user_data_key), null), User::class.java)

        topic = intent.getParcelableExtra<Topic>("topic")
        isEdit = intent.getBooleanExtra("isEdit", false)
        if(topic != null){
            binding.englishTopicTitleEdt.setText(topic!!.topicNameEnglish)
            binding.vietnameseTopicTitleEdt.setText(topic!!.topicNameVietnamese)
            binding.topicDescriptionEnglishEdt.setText(topic!!.descriptionEnglish)
            binding.topicDescriptionVietnameseEdt.setText(topic!!.descriptionVietnamese)
            dataRepository.getVocabulariesByTopic(topic!!.id!!, sharedPref.getString(getString(R.string.token_key), null)!!).thenAccept {
                vocabularyList = ArrayList(it)
                runOnUiThread{
                    vocabularyAdapter = VocabularyAdapter(this, vocabularyList, R.layout.add_vocabulary_item)
                    binding.vocabularyListView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    binding.vocabularyListView.setHasFixedSize(true)
                    binding.vocabularyListView.adapter = vocabularyAdapter
                    binding.vocabularyListView.recycledViewPool.setMaxRecycledViews(0, 0)
                }
            }.exceptionally {
                it->
                runOnUiThread{
                    Utils.showDialog(Gravity.CENTER, it.message!!, this)
                }
                null
            }
        }
        else{
            vocabularyList = ArrayList()
            vocabularyList.add(Vocabulary(null, "Hello", "Xin chào", "Hello", "Xin chào", null, ArrayList(), ArrayList()))
            vocabularyAdapter = VocabularyAdapter(this, vocabularyList, R.layout.add_vocabulary_item)
            binding.vocabularyListView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.vocabularyListView.setHasFixedSize(true)
            binding.vocabularyListView.adapter = vocabularyAdapter
            binding.vocabularyListView.recycledViewPool.setMaxRecycledViews(0, 0)
        }
        binding.englishTopicTitleEdt.addTextChangedListener {text->
            engToVietTranslator.downloadModelIfNeeded(downloadConditions).addOnSuccessListener {
                engToVietTranslator.translate(text.toString()).addOnSuccessListener {translatedText->
                    binding.vietnameseTopicTitleEdt.setText(translatedText)
                }.addOnFailureListener{
                    binding.vietnameseTopicTitleEdt.setText("")
                }
            }
        }
        binding.topicDescriptionEnglishEdt.addTextChangedListener {text->
            engToVietTranslator.downloadModelIfNeeded(downloadConditions).addOnSuccessListener {
                engToVietTranslator.translate(text.toString()).addOnSuccessListener {translatedText->
                    binding.topicDescriptionVietnameseEdt.setText(translatedText)
                }.addOnFailureListener{
                    binding.topicDescriptionVietnameseEdt.setText("")
                }
            }
        }

        binding.popupBtn.setOnClickListener{
            val popupMenu = PopupMenu(this, binding.popupBtn)
            popupMenu.setOnMenuItemClickListener {
                it-> when(it.itemId) {
                    R.id.addVocabularyItem -> {
                        vocabularyList.add( vocabularyList.size ,
                            Vocabulary(null, "", "", "", "", null, ArrayList(), ArrayList())
                        )
                        vocabularyAdapter.notifyItemInserted(vocabularyList.size - 1)
                        binding.vocabularyListView.scrollToPosition(vocabularyList.size - 1)
                        true
                    }
                    R.id.removeAllVocabularyItem -> {
                        vocabularyList.clear()
                        vocabularyAdapter.notifyDataSetChanged()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.inflate(R.menu.add_topic_popup)
            popupMenu.show()
        }
        binding.addVocabularyBtn.setOnClickListener{
            vocabularyList.add( vocabularyList.size ,
                Vocabulary(null, "", "", "", "", null, ArrayList(), ArrayList())
            )
            vocabularyAdapter.notifyItemInserted(vocabularyList.size - 1)
            binding.vocabularyListView.scrollToPosition(vocabularyList.size - 1)
        }
        binding.importDocumentBtn.setOnClickListener {
            if(user.isPremiumAccount){
                val pickFileIntent = Intent(Intent.ACTION_PICK)
                pickFileIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                startActivityForResult(pickFileIntent, PICK_FILE_REQUEST_CODE)
            }
            else{
                runOnUiThread {
                    Utils.showDialog(Gravity.CENTER, getString(R.string.premium_account_only), this)
                }
            }
        }

        binding.confirmBtn.setOnClickListener{
            val englishTitle= binding.englishTopicTitleEdt.text.toString()
            val vietnameseTitle = binding.vietnameseTopicTitleEdt.text.toString()
            val englishDescription = binding.topicDescriptionEnglishEdt.text.toString()
            val vietnameseDescription = binding.topicDescriptionVietnameseEdt.text.toString()
            val isPublic = binding.publicTopicSwitch.isChecked
            val token = sharedPref.getString(getString(R.string.token_key), null)
            if(englishTitle.isEmpty() || vietnameseTitle.isEmpty() || englishDescription.isEmpty() || vietnameseDescription.isEmpty()) {
                Utils.showDialog(Gravity.CENTER, getString(R.string.please_fill), this)
                return@setOnClickListener
            }
            if(vocabularyList.size < 2) {
                Utils.showDialog(Gravity.CENTER, getString(R.string.please_add_vocabulary), this)
                return@setOnClickListener
            }
            if(isEdit){
                val returnIntent = Intent()
                returnIntent.putParcelableArrayListExtra("vocabularies", vocabularyList)
                returnIntent.putExtra("titleEnglish", englishTitle)
                returnIntent.putExtra("titleVietnamese", vietnameseTitle)
                returnIntent.putExtra("topicDescriptionEnglish", englishDescription)
                returnIntent.putExtra("topicDescriptionVietnamese", vietnameseDescription)
                setResult(RESULT_OK, returnIntent)
                finish()
            }
            else{
                dataRepository.createTopic(englishTitle, vietnameseTitle, englishDescription, vietnameseDescription, vocabularyList, isPublic, token!!).thenAccept {
                    runOnUiThread{
                        Utils.showDialog(Gravity.CENTER, getString(R.string.create_topic_success), this)
                    }
                }.exceptionally {
                        it->
                    runOnUiThread{
                        Utils.showDialog(Gravity.CENTER, it.message!!, this)
                    }
                    null
                }
            }
        }
        setContentView(binding.root)
    }
}