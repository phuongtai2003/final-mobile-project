package com.tdtu.finalproject

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.PopupMenu
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.tdtu.finalproject.adapter.VocabularyAdapter
import com.tdtu.finalproject.databinding.ActivityAddTopicBinding
import com.tdtu.finalproject.model.Vocabulary
import com.tdtu.finalproject.repository.DataRepository
import com.tdtu.finalproject.utils.Utils

class AddTopicActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTopicBinding
    private lateinit var vocabularyList: MutableList<Vocabulary>
    private lateinit var vocabularyAdapter: VocabularyAdapter
    private final val PICK_FILE_REQUEST_CODE = 1
    private final val dataRepository: DataRepository = DataRepository()
    private lateinit var sharedPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTopicBinding.inflate(layoutInflater)
        binding.returnBtn.setOnClickListener{
            finish()
        }

        sharedPref = this.getSharedPreferences(getString(R.string.shared_preferences_key), MODE_PRIVATE)

        vocabularyList = ArrayList<Vocabulary>()
        vocabularyList.add(Vocabulary(null, "Hello", "Xin chào", "Hello", "Xin chào", null, null, null))


        binding.popupBtn.setOnClickListener{
            val popupMenu = PopupMenu(this, binding.popupBtn)
            popupMenu.setOnMenuItemClickListener {
                it-> when(it.itemId) {
                    R.id.addVocabularyItem -> {
                        addVocabulary(Vocabulary(null, "", "", "", "", null, null, null))
                        true
                    }
                    R.id.removeAllVocabularyItem -> {
                        vocabularyAdapter.removeAllVocabulary()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.inflate(R.menu.add_topic_popup)
            popupMenu.show()
        }
        vocabularyAdapter = VocabularyAdapter(this, vocabularyList, R.layout.add_vocabulary_item)
        binding.vocabularyListView.setHasFixedSize(true)
        binding.vocabularyListView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.vocabularyListView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        binding.vocabularyListView.adapter = vocabularyAdapter

        binding.addVocabularyBtn.setOnClickListener{
            addVocabulary(Vocabulary(null, "", "", "", "", null, null, null))
        }
        binding.importDocumentBtn.setOnClickListener {
            val pickFileIntent = Intent(Intent.ACTION_PICK)
            pickFileIntent.type = "text/plain"
            pickFileIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(pickFileIntent, PICK_FILE_REQUEST_CODE)
        }

        binding.confirmBtn.setOnClickListener{
            val englishTitle= binding.englishTopicTitleEdt.text.toString()
            val vietnameseTitle = binding.vietnameseTopicTitleEdt.text.toString()
            val englishDescription = binding.topicDescriptionEnglishEdt.text.toString()
            val vietnameseDescription = binding.topicDescriptionVietnameseEdt.text.toString()
            val isPublic = binding.publicTopicSwitch.isChecked
            val vocabularyList = vocabularyAdapter.getVocabularies()
            val token = sharedPref.getString(getString(R.string.token_key), null)
            dataRepository.createTopic(englishTitle, vietnameseTitle, englishDescription, vietnameseDescription, vocabularyList, isPublic, token!!).thenAccept {
                finish()
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

    private fun addVocabulary(vocabulary: Vocabulary) {
        vocabularyAdapter.addVocabulary(vocabulary)
    }
}