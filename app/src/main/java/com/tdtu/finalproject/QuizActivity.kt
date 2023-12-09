package com.tdtu.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Gravity
import android.view.View
import com.tdtu.finalproject.databinding.ActivityQuizBinding
import com.tdtu.finalproject.model.quizzes.Quiz
import com.tdtu.finalproject.model.topic.Topic
import com.tdtu.finalproject.model.vocabulary.Vocabulary
import com.tdtu.finalproject.utils.Language
import com.tdtu.finalproject.utils.StudyMode
import com.tdtu.finalproject.utils.Utils
import java.util.Locale
import kotlin.random.Random

class QuizActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var binding: ActivityQuizBinding
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
    private var instantFeedback = false
    private var isClickable = true
    private lateinit var studyMode: StudyMode
    private var currentAnswerMode = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
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
        quizzesList = Utils.generateQuizzes(vocabulariesList, shuffled)
        answersCorrectness = MutableList(quizzesList.size){false}
        chosenAnswers = MutableList(quizzesList.size){""}

        initView()
        showQuestion()
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
        setContentView(binding.root)
    }

    private fun initView(){
        if(vocabulariesList.size == 2){
            binding.answer4Btn.visibility = View.GONE
            binding.answer3Btn.visibility = View.GONE
        }
        else if(vocabulariesList.size == 3){
            binding.answer4Btn.visibility = View.GONE
        }
        else if(vocabulariesList.size == 1){
            binding.answer4Btn.visibility = View.GONE
            binding.answer3Btn.visibility = View.GONE
            binding.answer2Btn.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ttsEnglish.stop()
        ttsEnglish.shutdown()
        ttsVietnamese.stop()
        ttsVietnamese.shutdown()
    }

    private fun showQuestion(){
        if(questionCount > totalQuestions){
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
            feedBackIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(feedBackIntent)
            return
        }
        val quiz = quizzesList[questionCount - 1]
        binding.quizProgressTxt.text = questionCount.toString() + "/" + totalQuestions.toString()
        val allAnswers = if(vocabulariesList.size >= 4) listOf(
            quiz.correctAnswer,
            quiz.wrongAnswer?.get(0),
            quiz.wrongAnswer?.get(1),
            quiz.wrongAnswer?.get(2)
        ) else if(vocabulariesList.size == 3) listOf(
            quiz.correctAnswer,
            quiz.wrongAnswer?.get(0),
            quiz.wrongAnswer?.get(1)
        ) else if(vocabulariesList.size == 2) listOf(
            quiz.correctAnswer,
            quiz.wrongAnswer?.get(0)
        ) else listOf(quiz.correctAnswer)

        val shuffledAnswers = allAnswers.shuffled()

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
                    binding.answer1Btn.text = shuffledAnswers[0]?.vietnameseWord
                    binding.answer2Btn.text = shuffledAnswers[1]?.vietnameseWord
                    if(vocabulariesList.size >= 3)
                        binding.answer3Btn.text = shuffledAnswers[2]?.vietnameseWord
                    if(vocabulariesList.size >= 4)
                        binding.answer4Btn.text = shuffledAnswers[3]?.vietnameseWord
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
                    binding.answer1Btn.text = shuffledAnswers[0]?.vietnameseMeaning
                    binding.answer2Btn.text = shuffledAnswers[1]?.vietnameseMeaning
                    if(vocabulariesList.size >= 3)
                        binding.answer3Btn.text = shuffledAnswers[2]?.vietnameseMeaning
                    if(vocabulariesList.size >= 4)
                        binding.answer4Btn.text = shuffledAnswers[3]?.vietnameseMeaning
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
                binding.answer1Btn.text = shuffledAnswers[0]?.vietnameseWord
                binding.answer2Btn.text = shuffledAnswers[1]?.vietnameseWord
                if(vocabulariesList.size >= 3)
                    binding.answer3Btn.text = shuffledAnswers[2]?.vietnameseWord
                if(vocabulariesList.size >= 4)
                    binding.answer4Btn.text = shuffledAnswers[3]?.vietnameseWord
            }else if(questionByVocabulary){
                binding.questionTxt.text = quiz.correctAnswer?.englishWord
                binding.questionTxt.setOnClickListener{
                    if(isClickable){
                        isClickable = false
                        binding.questionTxt.setTextColor(getColor(R.color.secondary_color))
                        ttsEnglish.speak(quiz.correctAnswer?.englishWord, TextToSpeech.QUEUE_FLUSH, null, "")
                    }
                }
                binding.answer1Btn.text = shuffledAnswers[0]?.vietnameseMeaning
                binding.answer2Btn.text = shuffledAnswers[1]?.vietnameseMeaning
                if(vocabulariesList.size >= 3)
                    binding.answer3Btn.text = shuffledAnswers[2]?.vietnameseMeaning
                if(vocabulariesList.size >= 4)
                    binding.answer4Btn.text = shuffledAnswers[3]?.vietnameseMeaning
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
                    binding.answer1Btn.text = shuffledAnswers[0]?.englishWord
                    binding.answer2Btn.text = shuffledAnswers[1]?.englishWord
                    if(vocabulariesList.size >= 3)
                        binding.answer3Btn.text = shuffledAnswers[2]?.englishWord
                    if(vocabulariesList.size >= 4)
                        binding.answer4Btn.text = shuffledAnswers[3]?.englishWord
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
                    binding.answer1Btn.text = shuffledAnswers[0]?.englishMeaning
                    binding.answer2Btn.text = shuffledAnswers[1]?.englishMeaning
                    if(vocabulariesList.size >= 3)
                        binding.answer3Btn.text = shuffledAnswers[2]?.englishMeaning
                    if(vocabulariesList.size >= 4)
                        binding.answer4Btn.text = shuffledAnswers[3]?.englishMeaning
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
                binding.answer1Btn.text = shuffledAnswers[0]?.englishWord
                binding.answer2Btn.text = shuffledAnswers[1]?.englishWord
                if(vocabulariesList.size >= 3)
                    binding.answer3Btn.text = shuffledAnswers[2]?.englishWord
                if(vocabulariesList.size >= 4)
                    binding.answer4Btn.text = shuffledAnswers[3]?.englishWord
            }else if(questionByVocabulary){
                binding.questionTxt.text = quiz.correctAnswer?.vietnameseWord
                binding.questionTxt.setOnClickListener{
                    if(isClickable){
                        isClickable = false
                        binding.questionTxt.setTextColor(getColor(R.color.secondary_color))
                        ttsVietnamese.speak(quiz.correctAnswer?.vietnameseWord, TextToSpeech.QUEUE_FLUSH, null, "")
                    }
                }
                binding.answer1Btn.text = shuffledAnswers[0]?.englishMeaning
                binding.answer2Btn.text = shuffledAnswers[1]?.englishMeaning
                if(vocabulariesList.size >= 3)
                    binding.answer3Btn.text = shuffledAnswers[2]?.englishMeaning
                if(vocabulariesList.size >= 4)
                    binding.answer4Btn.text = shuffledAnswers[3]?.englishMeaning
            }
        }

        val btnList = listOf(
            binding.answer1Btn,
            binding.answer2Btn,
            binding.answer3Btn,
            binding.answer4Btn
        )

        btnList.forEach{btn->
            btn.setOnClickListener{
                val correct = shuffledAnswers[shuffledAnswers.indexOf(quiz.correctAnswer)]
                chosenAnswers[questionCount - 1] = btn.text.toString()
                if(instantFeedback){
                    runOnUiThread {
                        if(studyLanguage == Language.ENGLISH){
                            if(questionByDefinition && questionByVocabulary){
                                if(currentAnswerMode){
                                    Utils.propWrongAnswer(this, correct?.vietnameseWord!!, btn.text.toString())
                                }else{
                                    Utils.propWrongAnswer(this, correct?.vietnameseMeaning!!, btn.text.toString())
                                }
                            }
                            else if(questionByDefinition){
                                Utils.propWrongAnswer(this, correct?.vietnameseWord!!, btn.text.toString())
                            }else if(questionByVocabulary){
                                Utils.propWrongAnswer(this, correct?.vietnameseMeaning!!, btn.text.toString())
                            }
                        }
                        else{
                            if (questionByDefinition && questionByVocabulary){
                                if(currentAnswerMode){
                                    Utils.propWrongAnswer(this, correct?.englishWord!!, btn.text.toString())
                                }else{
                                    Utils.propWrongAnswer(this, correct?.englishMeaning!!, btn.text.toString())
                                }
                            }
                            else if(questionByDefinition){
                                Utils.propWrongAnswer(this, correct?.englishWord!!, btn.text.toString())
                            }else if(questionByVocabulary){
                                Utils.propWrongAnswer(this, correct?.englishMeaning!!, btn.text.toString())
                            }
                        }
                        Handler().postDelayed({
                            runOnUiThread {
                                questionCount++
                                showQuestion()
                            }
                        }, 1000)
                    }
                }else{
                    runOnUiThread {
                        Handler().postDelayed({
                            runOnUiThread {
                                questionCount++
                                showQuestion()
                            }
                        }, 0)
                    }
                }
            }
        }

        btnList[shuffledAnswers.indexOf(quiz.correctAnswer)].setOnClickListener{
            chosenAnswers[questionCount - 1] = btnList[shuffledAnswers.indexOf(quiz.correctAnswer)].text.toString()
            runOnUiThread {
                answersCorrectness[questionCount - 1] = true
                if(instantFeedback){
                    Utils.propCorrectAnswer(this)
                }
                Handler().postDelayed({
                    runOnUiThread {
                        questionCount++
                        showQuestion()
                    }
                }, if (instantFeedback) 1000 else 0)
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