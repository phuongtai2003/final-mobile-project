package com.tdtu.finalproject

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.tdtu.finalproject.databinding.ActivityTypingBinding
import com.tdtu.finalproject.model.quizzes.Quiz
import com.tdtu.finalproject.model.topic.Topic
import com.tdtu.finalproject.model.vocabulary.StudyVocabulary
import com.tdtu.finalproject.model.vocabulary.StudyVocabularyRequest
import com.tdtu.finalproject.model.vocabulary.Vocabulary
import com.tdtu.finalproject.repository.DataRepository
import com.tdtu.finalproject.utils.Language
import com.tdtu.finalproject.utils.StudyMode
import com.tdtu.finalproject.utils.Utils
import com.tdtu.finalproject.viewmodel.StudyViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.random.Random

class TypingActivity : BaseActivity(), TextToSpeech.OnInitListener {
    private lateinit var binding: ActivityTypingBinding
    private lateinit var topic: Topic
    private var questionCount = 1
    private var totalQuestions = 0
    private lateinit var vocabulariesList: List<Vocabulary>
    private var shuffled = false
    private var answerByDefinition = false
    private var answerByVocabulary = false
    private var questionByDefinition = false
    private var questionByVocabulary = false
    private var studyLanguage = Language.ENGLISH
    private lateinit var ttsVietnamese: TextToSpeech
    private lateinit var ttsEnglish: TextToSpeech
    private lateinit var quizzesList: List<Quiz>
    private lateinit var answersCorrectness: MutableList<Boolean>
    private lateinit var chosenAnswers: MutableList<String>
    private lateinit var bookmarkedVocabularies : List<Vocabulary>
    private var instantFeedback = false
    private var isClickable = true
    private lateinit var studyMode: StudyMode
    private var currentAnswerMode = false
    private lateinit var dataRepository: DataRepository
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var studyViewModel: StudyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTypingBinding.inflate(layoutInflater)
        studyViewModel = ViewModelProvider(this)[StudyViewModel::class.java]
        studyViewModel.startTimer()
        ttsEnglish = TextToSpeech(this, this)
        ttsVietnamese = TextToSpeech(this, this)
        studyLanguage = intent.getSerializableExtra("studyLanguage") as Language
        answerByDefinition = intent.getBooleanExtra("answerByDefinition", false)
        answerByVocabulary = intent.getBooleanExtra("answerByVocabulary", false)
        questionByDefinition = intent.getBooleanExtra("questionByDefinition", false)
        questionByVocabulary = intent.getBooleanExtra("questionByVocabulary", false)
        vocabulariesList = intent.getParcelableArrayListExtra<Vocabulary>("vocabularies")!!
        totalQuestions = intent.getIntExtra("questionCount", 0)
        shuffled = intent.getBooleanExtra("shuffleQuestion", false)
        topic = intent.getParcelableExtra("topic")!!
        instantFeedback = intent.getBooleanExtra("instantFeedBack", false)
        studyMode = intent.getSerializableExtra("studyMode") as StudyMode
        bookmarkedVocabularies = intent.getParcelableArrayListExtra<Vocabulary>("bookmarkedVocabularies")!!
        quizzesList = Utils.generateQuizzes(vocabulariesList, shuffled).subList(0, totalQuestions)
        answersCorrectness = MutableList(quizzesList.size){false}
        chosenAnswers = MutableList(quizzesList.size){""}
        dataRepository = DataRepository.getInstance()
        sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_key), MODE_PRIVATE)

        generateQuestionView()

        binding.closeBtn.setOnClickListener{
            finish()
        }
        ttsEnglish.setOnUtteranceCompletedListener {
            isClickable = true
            runOnUiThread {
                binding.questionTxt.setTextColor(getColor(R.color.white))
            }
        }
        ttsVietnamese.setOnUtteranceCompletedListener {
            isClickable = true
            runOnUiThread {
                binding.questionTxt.setTextColor(getColor(R.color.white))
            }
        }
        binding.skipBtn.setOnClickListener{
            if(questionCount <= totalQuestions){
                answersCorrectness[questionCount - 1] = false
                chosenAnswers[questionCount - 1] = ""
                questionCount++
                generateQuestionView()
            }
        }
        binding.answerTxt.addTextChangedListener {
            if(!it.isNullOrEmpty()){
                binding.skipBtn.visibility = View.GONE
            }
            else{
                binding.skipBtn.visibility = View.VISIBLE
            }
        }
        setContentView(binding.root)
    }

    private fun generateQuestionView(){
        if(questionCount > totalQuestions){
            val seconds = studyViewModel.seconds.value!!
            val scope = CoroutineScope(Dispatchers.Main)
            scope.launch {
                val isOwnedTopic = topic.ownerId?.id == Utils.getUserFromSharedPreferences(this@TypingActivity,sharedPreferences).id
                if(isOwnedTopic){
                    val studyJob = async {
                        dataRepository.studyTopicForUser(sharedPreferences.getString(getString(R.string.token_key), null)!!, topic.id!!).exceptionally {
                            runOnUiThread {
                                Utils.showSnackBar(binding.root, it.message!!)
                            }
                            null
                        }
                    }
                    studyJob.await()
                    val updateLearningJob = async {
                        dataRepository.updateLearningStatisticTopic(sharedPreferences.getString(getString(R.string.token_key), null)!!, topic.id!!,seconds ).exceptionally {
                            runOnUiThread {
                                Utils.showSnackBar(binding.root, it.message!!)
                            }
                            null
                        }
                    }
                    updateLearningJob.await()
                }
                val studyVocabularies = mutableListOf<StudyVocabulary>()
                for(i in quizzesList.indices){
                    if(answersCorrectness[i]){
                        val vocabulary = quizzesList[i].correctAnswer!!
                        val studyVocabularyRequest = StudyVocabulary(vocabulary.id!!, 1)
                        studyVocabularies.add(studyVocabularyRequest)
                    }
                }
                val job = async {
                    dataRepository.studyVocabulary(
                        sharedPreferences.getString(
                            getString(R.string.token_key),
                            ""
                        )!!, StudyVocabularyRequest(studyVocabularies)
                    ).exceptionally {
                        runOnUiThread {
                            Utils.showSnackBar(binding.root, it.message!!)
                        }
                        null
                    }
                }
                job.await()
            }.invokeOnCompletion {
                val feedBackIntent = Intent(this, FeedbackActivity::class.java)
                feedBackIntent.putExtra("studyLanguage", studyLanguage)
                feedBackIntent.putExtra("answerByDefinition", answerByDefinition)
                feedBackIntent.putExtra("answerByVocabulary", answerByVocabulary)
                feedBackIntent.putExtra("questionByDefinition", questionByDefinition)
                feedBackIntent.putExtra("questionByVocabulary", questionByVocabulary)
                feedBackIntent.putExtra("vocabularies", ArrayList(vocabulariesList))
                feedBackIntent.putExtra("answersCorrectness", ArrayList(answersCorrectness))
                feedBackIntent.putExtra("chosenAnswers", ArrayList(chosenAnswers))
                feedBackIntent.putExtra("quizzes", ArrayList(quizzesList))
                feedBackIntent.putExtra("topic", topic)
                feedBackIntent.putExtra("studyMode", studyMode)
                feedBackIntent.putExtra("bookmarkedVocabularies", ArrayList(bookmarkedVocabularies))
                startActivity(feedBackIntent)
            }
            return
        }
        binding.answerTxt.setText("")
        val quiz = quizzesList[questionCount - 1]
        binding.quizProgressTxt.text = questionCount.toString() + "/" + totalQuestions.toString()

        if(studyLanguage == Language.ENGLISH){
            if(questionByVocabulary && questionByDefinition){
                if(Random.nextBoolean()){
                    currentAnswerMode = true
                    binding.questionTxt.text = quiz.correctAnswer?.englishMeaning
                    binding.questionTxt.setOnClickListener{
                        if(isClickable){
                            isClickable = false
                            binding.questionTxt.setTextColor(getColor(R.color.secondary_color))
                            ttsEnglish.speak(quiz.correctAnswer?.englishMeaning, TextToSpeech.QUEUE_FLUSH, null, "")
                        }
                    }
                    binding.answerTxt.hint = getString(R.string.answer_by_term)
                    binding.answerTxt.setOnEditorActionListener { _, actionId, keyEvent ->
                        if(actionId == EditorInfo.IME_ACTION_GO){
                            val answer = binding.answerTxt.text.toString()
                            if(answer.lowercase() == quiz.correctAnswer?.vietnameseWord?.lowercase()){
                                answersCorrectness[questionCount - 1] = true
                                chosenAnswers[questionCount - 1] = answer
                                runOnUiThread {
                                    Utils.propCorrectAnswer(this)
                                }
                            }
                            else{
                                answersCorrectness[questionCount - 1] = false
                                chosenAnswers[questionCount - 1] = answer
                                runOnUiThread {
                                    Utils.propWrongAnswer(this, quiz.correctAnswer?.vietnameseWord!!, answer)
                                }
                            }
                            Handler().postDelayed({
                                runOnUiThread {
                                    questionCount++
                                    generateQuestionView()
                                }
                            }, if(instantFeedback) 1000 else 0)
                            true
                        }
                        else{
                            false
                        }
                    }
                }
                else{
                    currentAnswerMode = false
                    binding.questionTxt.text = quiz.correctAnswer?.englishWord
                    binding.questionTxt.setOnClickListener{
                        if(isClickable){
                            isClickable = false
                            binding.questionTxt.setTextColor(getColor(R.color.secondary_color))
                            ttsEnglish.speak(quiz.correctAnswer?.englishWord, TextToSpeech.QUEUE_FLUSH, null, "")
                        }
                    }
                    binding.answerTxt.hint = getString(R.string.answer_by_definition)
                    binding.answerTxt.setOnEditorActionListener { _, actionId, keyEvent ->
                        if(actionId == EditorInfo.IME_ACTION_GO){
                            val answer = binding.answerTxt.text.toString()
                            if(answer.lowercase() == quiz.correctAnswer?.vietnameseMeaning?.lowercase()){
                                answersCorrectness[questionCount - 1] = true
                                chosenAnswers[questionCount - 1] = answer
                                runOnUiThread {
                                    Utils.propCorrectAnswer(this)
                                }
                            }
                            else{
                                answersCorrectness[questionCount - 1] = false
                                chosenAnswers[questionCount - 1] = answer
                                runOnUiThread {
                                    Utils.propWrongAnswer(this, quiz.correctAnswer?.vietnameseMeaning!!, answer)
                                }
                            }
                            Handler().postDelayed({
                                runOnUiThread {
                                    questionCount++
                                    generateQuestionView()
                                }
                            }, if(instantFeedback) 1000 else 0)
                            true
                        }
                        else{
                            false
                        }
                    }
                }
            }
            else if(questionByDefinition){
                binding.questionTxt.text = quiz.correctAnswer?.englishMeaning
                binding.questionTxt.setOnClickListener{
                    if(isClickable){
                        isClickable = false
                        binding.questionTxt.setTextColor(getColor(R.color.secondary_color))
                        ttsEnglish.speak(quiz.correctAnswer?.englishMeaning, TextToSpeech.QUEUE_FLUSH, null, "")
                    }
                }
                binding.answerTxt.hint = getString(R.string.answer_by_term)
                binding.answerTxt.setOnEditorActionListener { _, actionId, keyEvent ->
                    if(actionId == EditorInfo.IME_ACTION_GO){
                        val answer = binding.answerTxt.text.toString()
                        if(answer.lowercase() == quiz.correctAnswer?.vietnameseWord?.lowercase()){
                            answersCorrectness[questionCount - 1] = true
                            chosenAnswers[questionCount - 1] = answer
                            runOnUiThread {
                                Utils.propCorrectAnswer(this)
                            }
                        }
                        else{
                            answersCorrectness[questionCount - 1] = false
                            chosenAnswers[questionCount - 1] = answer
                            runOnUiThread {
                                Utils.propWrongAnswer(this, quiz.correctAnswer?.vietnameseWord!!, answer)
                            }
                        }
                        Handler().postDelayed({
                            runOnUiThread {
                                questionCount++
                                generateQuestionView()
                            }
                        }, if(instantFeedback) 1000 else 0)
                        true
                    }
                    else{
                        false
                    }
                }
            }else if(questionByVocabulary){
                binding.questionTxt.text = quiz.correctAnswer?.englishWord
                binding.questionTxt.setOnClickListener{
                    if(isClickable){
                        isClickable = false
                        binding.questionTxt.setTextColor(getColor(R.color.secondary_color))
                        ttsEnglish.speak(quiz.correctAnswer?.englishWord, TextToSpeech.QUEUE_FLUSH, null, "")
                    }
                }
                binding.answerTxt.hint = getString(R.string.answer_by_definition)
                binding.answerTxt.setOnEditorActionListener { _, actionId, keyEvent ->
                    if(actionId == EditorInfo.IME_ACTION_GO){
                        val answer = binding.answerTxt.text.toString()
                        if(answer.lowercase() == quiz.correctAnswer?.vietnameseMeaning?.lowercase()){
                            answersCorrectness[questionCount - 1] = true
                            chosenAnswers[questionCount - 1] = answer
                            runOnUiThread {
                                Utils.propCorrectAnswer(this)
                            }
                        }
                        else{
                            answersCorrectness[questionCount - 1] = false
                            chosenAnswers[questionCount - 1] = answer
                            runOnUiThread {
                                Utils.propWrongAnswer(this, quiz.correctAnswer?.vietnameseMeaning!!, answer)
                            }
                        }
                        Handler().postDelayed({
                            runOnUiThread {
                                questionCount++
                                generateQuestionView()
                            }
                        }, if(instantFeedback) 1000 else 0)
                        true
                    }
                    else{
                        false
                    }
                }
            }
        }
        else{
            if(questionByDefinition && questionByVocabulary){
                if(Random.nextBoolean()){
                    currentAnswerMode = true
                    binding.questionTxt.text = quiz.correctAnswer?.vietnameseMeaning
                    binding.questionTxt.setOnClickListener{
                        if(isClickable){
                            isClickable = false
                            binding.questionTxt.setTextColor(getColor(R.color.secondary_color))
                            ttsVietnamese.speak(quiz.correctAnswer?.vietnameseMeaning, TextToSpeech.QUEUE_FLUSH, null, "")
                        }
                    }
                    binding.answerTxt.hint = getString(R.string.answer_by_term)
                    binding.answerTxt.setOnEditorActionListener { _, actionId, keyEvent ->
                        if(actionId == EditorInfo.IME_ACTION_GO){
                            val answer = binding.answerTxt.text.toString()
                            if(answer.lowercase() == quiz.correctAnswer?.englishWord?.lowercase()){
                                answersCorrectness[questionCount - 1] = true
                                chosenAnswers[questionCount - 1] = answer
                                runOnUiThread {
                                    Utils.propCorrectAnswer(this)
                                }
                            }
                            else{
                                answersCorrectness[questionCount - 1] = false
                                chosenAnswers[questionCount - 1] = answer
                                runOnUiThread {
                                    Utils.propWrongAnswer(this, quiz.correctAnswer?.englishWord!!, answer)
                                }
                            }
                            Handler().postDelayed({
                                runOnUiThread {
                                    questionCount++
                                    generateQuestionView()
                                }
                            }, if(instantFeedback) 1000 else 0)
                            true
                        }
                        else{
                            false
                        }
                    }
                }
                else{
                    currentAnswerMode = false
                    binding.questionTxt.text = quiz.correctAnswer?.vietnameseWord
                    binding.questionTxt.setOnClickListener{
                        if(isClickable){
                            isClickable = false
                            binding.questionTxt.setTextColor(getColor(R.color.secondary_color))
                            ttsVietnamese.speak(quiz.correctAnswer?.vietnameseWord, TextToSpeech.QUEUE_FLUSH, null, "")
                        }
                    }
                    binding.answerTxt.hint = getString(R.string.answer_by_definition)
                    binding.answerTxt.setOnEditorActionListener { _, actionId, keyEvent ->
                        if(actionId == EditorInfo.IME_ACTION_GO){
                            val answer = binding.answerTxt.text.toString()
                            if(answer.lowercase() == quiz.correctAnswer?.englishMeaning?.lowercase()){
                                answersCorrectness[questionCount - 1] = true
                                chosenAnswers[questionCount - 1] = answer
                                runOnUiThread {
                                    Utils.propCorrectAnswer(this)
                                }
                            }
                            else{
                                answersCorrectness[questionCount - 1] = false
                                chosenAnswers[questionCount - 1] = answer
                                runOnUiThread {
                                    Utils.propWrongAnswer(this, quiz.correctAnswer?.englishMeaning!!, answer)
                                }
                            }
                            Handler().postDelayed({
                                runOnUiThread {
                                    questionCount++
                                    generateQuestionView()
                                }
                            }, if(instantFeedback) 1000 else 0)
                            true
                        }
                        else{
                            false
                        }
                    }
                }
            }
            else if(questionByDefinition){
                binding.questionTxt.text = quiz.correctAnswer?.vietnameseMeaning
                binding.questionTxt.setOnClickListener{
                    if(isClickable){
                        isClickable = false
                        binding.questionTxt.setTextColor(getColor(R.color.secondary_color))
                        ttsVietnamese.speak(quiz.correctAnswer?.vietnameseMeaning, TextToSpeech.QUEUE_FLUSH, null, "")
                    }
                }
                binding.answerTxt.hint = getString(R.string.answer_by_term)
                binding.answerTxt.setOnEditorActionListener { _, actionId, keyEvent ->
                    if(actionId == EditorInfo.IME_ACTION_GO){
                        val answer = binding.answerTxt.text.toString()
                        if(answer.lowercase() == quiz.correctAnswer?.englishWord?.lowercase()){
                            answersCorrectness[questionCount - 1] = true
                            chosenAnswers[questionCount - 1] = answer
                            runOnUiThread {
                                Utils.propCorrectAnswer(this)
                            }
                        }
                        else{
                            answersCorrectness[questionCount - 1] = false
                            chosenAnswers[questionCount - 1] = answer
                            runOnUiThread {
                                Utils.propWrongAnswer(this, quiz.correctAnswer?.englishWord!!, answer)
                            }
                        }
                        Handler().postDelayed({
                            runOnUiThread {
                                questionCount++
                                generateQuestionView()
                            }
                        }, if(instantFeedback) 1000 else 0)
                        true
                    }
                    else{
                        false
                    }
                }
            }else if(questionByVocabulary){
                binding.questionTxt.text = quiz.correctAnswer?.vietnameseWord
                binding.questionTxt.setOnClickListener{
                    if(isClickable){
                        isClickable = false
                        binding.questionTxt.setTextColor(getColor(R.color.secondary_color))
                        ttsVietnamese.speak(quiz.correctAnswer?.vietnameseWord, TextToSpeech.QUEUE_FLUSH, null, "")
                    }
                }
                binding.answerTxt.hint = getString(R.string.answer_by_definition)
                binding.answerTxt.setOnEditorActionListener { _, actionId, keyEvent ->
                    if(actionId == EditorInfo.IME_ACTION_GO){
                        val answer = binding.answerTxt.text.toString()
                        if(answer.lowercase() == quiz.correctAnswer?.englishMeaning?.lowercase()){
                            answersCorrectness[questionCount - 1] = true
                            chosenAnswers[questionCount - 1] = answer
                            runOnUiThread {
                                Utils.propCorrectAnswer(this)
                            }
                        }
                        else{
                            answersCorrectness[questionCount - 1] = false
                            chosenAnswers[questionCount - 1] = answer
                            runOnUiThread {
                                Utils.propWrongAnswer(this, quiz.correctAnswer?.englishMeaning!!, answer)
                            }
                        }
                        Handler().postDelayed({
                            runOnUiThread {
                                questionCount++
                                generateQuestionView()
                            }
                        }, if(instantFeedback) 1000 else 0)
                        true
                    }
                    else{
                        false
                    }
                }
            }
        }

    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){
            val result = ttsEnglish.setLanguage(Locale.US)
            if(result == TextToSpeech.LANG_MISSING_DATA){
                Utils.showDialog(Gravity.CENTER, getString(R.string.language_not_supported), this)
            }
            val res = ttsVietnamese.setLanguage(Locale("vi"))
            if(res == TextToSpeech.LANG_MISSING_DATA){
                Utils.showDialog(Gravity.CENTER, getString(R.string.language_not_supported), this)
            }
        }
        else{
            Utils.showDialog(Gravity.CENTER, getString(R.string.tts_init_failed), this)
        }
    }
}