package com.tdtu.finalproject

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.tdtu.finalproject.adapter.VocabularyAdapter
import com.tdtu.finalproject.databinding.ActivityAddTopicBinding
import com.tdtu.finalproject.model.vocabulary.Vocabulary
import com.tdtu.finalproject.model.user.User
import com.tdtu.finalproject.repository.DataRepository
import com.tdtu.finalproject.utils.Utils

class AddTopicActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTopicBinding
    private lateinit var vocabularyList: ArrayList<Vocabulary>
    private lateinit var vocabularyAdapter: VocabularyAdapter
    private val PICK_FILE_REQUEST_CODE = 1
    private val dataRepository: DataRepository = DataRepository()
    private lateinit var sharedPref: SharedPreferences
    private lateinit var user : User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTopicBinding.inflate(layoutInflater)
        binding.returnBtn.setOnClickListener{
            finish()
        }

        sharedPref = this.getSharedPreferences(getString(R.string.shared_preferences_key), MODE_PRIVATE)
        user = Gson().fromJson(sharedPref.getString(getString(R.string.user_data_key), null), User::class.java)

        vocabularyList = ArrayList()
        vocabularyList.add(Vocabulary(null, "Hello", "Xin chào", "Hello", "Xin chào", null, ArrayList(), ArrayList()))


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
        vocabularyAdapter = VocabularyAdapter(this, vocabularyList, R.layout.add_vocabulary_item)
        binding.vocabularyListView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.vocabularyListView.setHasFixedSize(true)
        binding.vocabularyListView.adapter = vocabularyAdapter
        binding.vocabularyListView.recycledViewPool.setMaxRecycledViews(0, 0)
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
            if(vocabularyList.size == 0) {
                Utils.showDialog(Gravity.CENTER, getString(R.string.please_add_vocabulary), this)
                return@setOnClickListener
            }
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
        setContentView(binding.root)
    }
}