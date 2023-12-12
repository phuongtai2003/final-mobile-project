package com.tdtu.finalproject

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.tdtu.finalproject.adapter.FeedbackResultAdapter
import com.tdtu.finalproject.adapter.FolderAdapter
import com.tdtu.finalproject.databinding.ActivityFeedbackBinding
import com.tdtu.finalproject.model.quizzes.Quiz
import com.tdtu.finalproject.model.results.ResultData
import com.tdtu.finalproject.model.topic.Topic
import com.tdtu.finalproject.model.vocabulary.Vocabulary
import com.tdtu.finalproject.repository.DataRepository
import com.tdtu.finalproject.utils.Language
import com.tdtu.finalproject.utils.StudyMode
import com.tdtu.finalproject.utils.Utils

class FeedbackActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedbackBinding
    private lateinit var studyLanguage: Language
    private var answerByDefinition = true
    private var answerByVocabulary = true
    private var questionByDefinition = true
    private var questionByVocabulary = true
    private lateinit var vocabularies: List<Vocabulary>
    private lateinit var answerCorrectness : List<Boolean>
    private lateinit var chosenAnswers : List<String>
    private lateinit var quizzesList: List<Quiz>
    private lateinit var topic: Topic
    private lateinit var resultDataList: List<ResultData>
    private lateinit var resultDataAdapter: FeedbackResultAdapter
    private lateinit var bookmarkedVocabulariesList : List<Vocabulary>
    private lateinit var studyMode: StudyMode
    private lateinit var dataRepository: DataRepository
    private lateinit var sharedPreference: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        dataRepository = DataRepository.getInstance()
        sharedPreference = getSharedPreferences(getString(R.string.shared_preferences_key), MODE_PRIVATE)
        studyLanguage = intent.getSerializableExtra("studyLanguage") as Language
        answerByDefinition = intent.getBooleanExtra("answerByDefinition", true)
        answerByVocabulary = intent.getBooleanExtra("answerByVocabulary", true)
        questionByDefinition = intent.getBooleanExtra("questionByDefinition", true)
        questionByVocabulary = intent.getBooleanExtra("questionByVocabulary", true)
        vocabularies = intent.getParcelableArrayListExtra<Vocabulary>("vocabularies")!!
        chosenAnswers = intent.getStringArrayListExtra("chosenAnswers")!!
        quizzesList = intent.getParcelableArrayListExtra<Quiz>("quizzes")!!
        bookmarkedVocabulariesList = intent.getParcelableArrayListExtra<Vocabulary>("bookmarkedVocabularies")!!
        answerCorrectness = intent.getSerializableExtra("answersCorrectness") as List<Boolean>
        studyMode = intent.getSerializableExtra("studyMode") as StudyMode
        topic = intent.getParcelableExtra<Topic>("topic")!!

        var percentage = 0
        var correctCount = 0
        for (answerCorrect in answerCorrectness) {
            if (answerCorrect) {
                correctCount++
            }
        }
        percentage = (correctCount * 100) / answerCorrectness.size
        var inCorrectCount = answerCorrectness.size - correctCount
        binding.correctProgressBar.progress = percentage
        binding.progressTxt.text = percentage.toString() + "%"
        binding.correctCountTxt.text = getString(R.string.correct) + " " + correctCount
        binding.wrongCountTxt.text = getString(R.string.incorrect) + " " + inCorrectCount
        binding.resultFeedbackTxt.text = if(percentage >= 50) getString(R.string.you_know_your_stuff) else getString(R.string.you_need_to_study_more)
        binding.feedBackTxt.text = if(percentage >= 50) getString(R.string.very_good) else getString(R.string.practice_again)


        resultDataList = mutableListOf()
        for (i in quizzesList.indices) {
            val quiz = quizzesList[i]
            val resultData = ResultData(
                answerCorrectness[i],
                chosenAnswers[i],
                "",
                ""
            )
            if(studyLanguage == Language.ENGLISH){
                if(questionByDefinition){
                    resultData.question = quiz.correctAnswer?.englishMeaning!!
                    resultData.answer = quiz.correctAnswer?.vietnameseWord!!
                }
                else if(questionByVocabulary){
                    resultData.question = quiz.correctAnswer?.englishWord!!
                    resultData.answer = quiz.correctAnswer?.vietnameseMeaning!!
                }
            }
            else{
                if(questionByDefinition){
                    resultData.question = quiz.correctAnswer?.vietnameseMeaning!!
                    resultData.answer = quiz.correctAnswer?.englishWord!!
                }
                else if(questionByVocabulary){
                    resultData.question = quiz.correctAnswer?.vietnameseWord!!
                    resultData.answer = quiz.correctAnswer?.englishMeaning!!
                }
            }
            resultDataList += resultData
        }
        resultDataAdapter = FeedbackResultAdapter(this, resultDataList, R.layout.result_data_layout)
        binding.answerRecyclerView.setHasFixedSize(true)
        binding.answerRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.answerRecyclerView.adapter = resultDataAdapter

        dataRepository.getBookmarkVocabulariesInTopic(sharedPreference.getString(getString(R.string.token_key),null)!!, topic.id!!).thenAcceptAsync {
            if(it != null){
                bookmarkedVocabulariesList = it
            }
        }.exceptionally {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            Utils.showSnackBar(binding.root, getString(R.string.something_went_wrong))
            null
        }

        binding.closeBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        binding.tryAgainBtn.setOnClickListener {
            val intent = Intent(this, StudyConfigurationActivity::class.java)
            intent.putExtra("topic", topic)
            intent.putExtra("studyMode", studyMode)
            intent.putParcelableArrayListExtra("vocabularies", ArrayList(vocabularies))
            startActivity(intent)
        }
        binding.flashcardBtn.setOnClickListener {
            val intent = Intent(this, FlashCardActivity::class.java)
            intent.putExtra("topic", topic)
            intent.putExtra("bookmarkedVocabularies", ArrayList(bookmarkedVocabulariesList))
            intent.putParcelableArrayListExtra("vocabularies", ArrayList(vocabularies))
            startActivity(intent)
        }
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        dataRepository.getBookmarkVocabulariesInTopic(sharedPreference.getString(getString(R.string.token_key),null)!!, topic.id!!).thenAcceptAsync {
            if(it != null){
                bookmarkedVocabulariesList = it
            }
        }.exceptionally {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            Utils.showSnackBar(binding.root, getString(R.string.something_went_wrong))
            null
        }
    }
}