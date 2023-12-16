package com.tdtu.finalproject

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.PopupMenu
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.opencsv.CSVReader
import com.tdtu.finalproject.adapter.VocabularyAdapter
import com.tdtu.finalproject.databinding.ActivityAddTopicBinding
import com.tdtu.finalproject.model.topic.Topic
import com.tdtu.finalproject.model.user.User
import com.tdtu.finalproject.model.vocabulary.Vocabulary
import com.tdtu.finalproject.repository.DataRepository
import com.tdtu.finalproject.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.BufferedReader
import java.io.FileReader
import java.io.InputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date

class AddTopicActivity : BaseActivity() {
    private var downloadConditions = DownloadConditions.Builder().requireWifi().build()
    private var engToVietOptions = TranslatorOptions.Builder().setSourceLanguage(TranslateLanguage.ENGLISH).setTargetLanguage(
        TranslateLanguage.VIETNAMESE).build()
    private val engToVietTranslator = Translation.getClient(engToVietOptions)
    private lateinit var binding: ActivityAddTopicBinding
    private lateinit var vocabularyList: ArrayList<Vocabulary>
    private lateinit var vocabularyAdapter: VocabularyAdapter
    private val dataRepository: DataRepository = DataRepository()
    private lateinit var sharedPref: SharedPreferences
    private lateinit var user : User
    private var topic: Topic? = null
    private var isEdit = false
    private lateinit var importCSVFileStartForResult: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTopicBinding.inflate(layoutInflater)
        binding.returnBtn.setOnClickListener{
            finish()
        }

        sharedPref = this.getSharedPreferences(getString(R.string.shared_preferences_key), MODE_PRIVATE)
        user = Gson().fromJson(sharedPref.getString(getString(R.string.user_data_key), null), User::class.java)
        importCSVFileStartForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { returnIt ->
            if(returnIt.resultCode == RESULT_OK && returnIt.data != null){
                val scope = CoroutineScope(Dispatchers.Main)
                val uri = returnIt.data!!.data
                try{
                    val mimeType: String? = uri?.let { it1 -> contentResolver.getType(it1) }
                    if (mimeType != null && mimeType == "text/csv") {
                        scope.launch {
                            val inputStream: InputStream? = contentResolver.openInputStream(uri)
                            val reader = BufferedReader(InputStreamReader(inputStream))
                            val csvReader = CSVReader(reader)
                            var nextLine: Array<String>?

                            while (csvReader.readNext().also { nextLine = it } != null) {
                                if (nextLine!!.size != 2) {
                                    throw Exception(getString(R.string.invalid_file_type))
                                }
                                var englishWord = nextLine!![0]
                                var englishMeaning = nextLine!![1]
                                var vietnameseWord = ""
                                var vietnameseMeaning = ""
                                val job = async {
                                    engToVietTranslator.translate(englishWord)
                                        .addOnSuccessListener {
                                            vietnameseWord = it
                                        }
                                    engToVietTranslator.translate(englishMeaning)
                                        .addOnSuccessListener {
                                            vietnameseMeaning = it
                                        }
                                }
                                job.await().addOnSuccessListener {
                                    vocabularyList.add(
                                        vocabularyList.size,
                                        Vocabulary(
                                            null,
                                            englishWord,
                                            vietnameseWord,
                                            englishMeaning,
                                            vietnameseMeaning,
                                            null,
                                            ArrayList(),
                                            ArrayList()
                                        )
                                    )
                                    vocabularyAdapter.notifyItemInserted(vocabularyList.size - 1)
                                }
                            }
                            csvReader.close()
                            reader.close()
                            inputStream?.close()
                        }.invokeOnCompletion {
                            scope.cancel()
                        }
                    } else if (mimeType != null && mimeType == "application/vnd.ms-excel" || mimeType == "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") {
                        val inputStream: InputStream? = contentResolver.openInputStream(uri)
                        val workbook = WorkbookFactory.create(inputStream)
                        val sheet = workbook.getSheetAt(0)
                        val rowIterator = sheet.iterator()
                        while (rowIterator.hasNext()) {
                            val row = rowIterator.next()
                            val cellIterator = row.cellIterator()
                            var englishWord = ""
                            var englishMeaning = ""
                            while (cellIterator.hasNext()) {
                                val cell = cellIterator.next()
                                when (cell.columnIndex) {
                                    0 -> englishWord = cell.stringCellValue
                                    1 -> englishMeaning = cell.stringCellValue
                                }
                            }
                            if(englishWord.isEmpty() || englishMeaning.isEmpty()){
                                throw Exception(getString(R.string.invalid_file_type))
                            }
                            scope.launch {
                                var vietnameseWord = ""
                                var vietnameseMeaning = ""
                                val job = async{
                                    engToVietTranslator.translate(englishWord).addOnSuccessListener {
                                        vietnameseWord = it
                                    }
                                    engToVietTranslator.translate(englishMeaning).addOnSuccessListener {
                                        vietnameseMeaning = it
                                    }
                                }
                                job.await().addOnSuccessListener {
                                    vocabularyList.add( vocabularyList.size ,
                                        Vocabulary(null, englishWord, vietnameseWord, englishMeaning, vietnameseMeaning, null, ArrayList(), ArrayList())
                                    )
                                    vocabularyAdapter.notifyItemInserted(vocabularyList.size - 1)
                                }
                            }
                        }
                        inputStream?.close()
                    } else {
                        throw Exception(getString(R.string.invalid_file_type))
                    }
                }
                catch (e: Throwable){
                    runOnUiThread {
                        Utils.showDialog(Gravity.CENTER, e.message!!, this)
                    }
                }
            }
        }

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
                    binding.vocabularyListView.setHasFixedSize(false)
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
            binding.vocabularyListView.setHasFixedSize(false)
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
                val pickFileIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                pickFileIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                pickFileIntent.addCategory(Intent.CATEGORY_OPENABLE)
                val mimeTypes = arrayOf(
                    "text/csv",
                    "application/vnd.ms-excel",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                )
                pickFileIntent.type = "*/*"
                pickFileIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                importCSVFileStartForResult.launch(pickFileIntent)
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