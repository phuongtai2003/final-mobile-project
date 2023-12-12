package com.tdtu.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import com.tdtu.finalproject.constants.Constant
import com.tdtu.finalproject.databinding.ActivityStudyConfigurationBinding
import com.tdtu.finalproject.model.topic.Topic
import com.tdtu.finalproject.model.vocabulary.Vocabulary
import com.tdtu.finalproject.utils.Language
import com.tdtu.finalproject.utils.PromptOptionsListener
import com.tdtu.finalproject.utils.StudyMode

class StudyConfigurationActivity : AppCompatActivity(), PromptOptionsListener {
    private lateinit var binding: ActivityStudyConfigurationBinding
    private lateinit var topic: Topic
    private lateinit var vocabularies: List<Vocabulary>
    private lateinit var studyMode: StudyMode
    private var studyLanguage: Language = Language.ENGLISH
    private var answerByDefinition = true
    private var answerByVocabulary = true
    private var questionByDefinition = true
    private var questionByVocabulary = true
    private lateinit var bookmarkedVocabularies : List<Vocabulary>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudyConfigurationBinding.inflate(layoutInflater)
        val data = intent.getParcelableExtra<Topic>("topic")
        if(data != null) {
            studyMode = intent.getSerializableExtra("studyMode") as StudyMode
            vocabularies = intent.getParcelableArrayListExtra<Vocabulary>("vocabularies")!!
            bookmarkedVocabularies = intent.getParcelableArrayListExtra<Vocabulary>("bookmarkedVocabularies")!!
            topic = data
        }
        else{
            finish()
        }
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, R.layout.drop_down_item, listOf(getString(R.string.english), getString(R.string.vietnamese)))
        binding.languageSpinner.adapter = adapter
        binding.questionCountTxt.text = getString(R.string.question_count) + " (" + vocabularies.size.toString()+" max)"
        binding.topicNameEnglishTxt.text = topic.topicNameEnglish
        binding.topicNameVietnameseTxt.text = topic.topicNameVietnamese
        binding.questionCountEdt.addTextChangedListener {
            val value = it.toString()
            if(value.isNotEmpty()){
                try{
                    val count = value.toInt()
                    if(count > topic.vocabularyCount){
                        binding.questionCountEdt.setText(vocabularies.size.toString())
                    }
                }catch (e: Exception){
                    binding.questionCountEdt.setText("0")
                }
            }
        }

        binding.languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ){
                when(position){
                    0 -> studyLanguage = Language.ENGLISH
                    1 -> studyLanguage = Language.VIETNAMESE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                studyLanguage = Language.ENGLISH
            }
        }

        binding.promptOptionBtn.setOnClickListener {
            val promptTypeBottomSheet = PromptTypeBottomSheet()
            val bundle = Bundle()
            bundle.putBoolean("answerByDefinition", answerByDefinition)
            bundle.putBoolean("answerByVocabulary", answerByVocabulary)
            bundle.putBoolean("questionByDefinition", questionByDefinition)
            bundle.putBoolean("questionByVocabulary", questionByVocabulary)
            promptTypeBottomSheet.arguments = bundle
            promptTypeBottomSheet.show(supportFragmentManager, "PromptTypeBottomSheet")
        }
        binding.closeBtn.setOnClickListener {
            finish()
        }

        binding.startBtn.setOnClickListener {
            if(studyMode == StudyMode.Quiz){
                val quizIntent = Intent(this, QuizActivity::class.java)
                val shuffleQuestion = binding.shuffleQuestionSwitch.isChecked
                quizIntent.putExtra("topic", topic)
                quizIntent.putExtra("studyLanguage", studyLanguage)
                quizIntent.putExtra("questionCount", if(binding.questionCountEdt.text.isEmpty()) vocabularies.size else binding.questionCountEdt.text.toString().toInt() )
                quizIntent.putExtra("shuffleQuestion", shuffleQuestion)
                quizIntent.putExtra("answerByDefinition", answerByDefinition)
                quizIntent.putExtra("answerByVocabulary", answerByVocabulary)
                quizIntent.putExtra("questionByDefinition", questionByDefinition)
                quizIntent.putExtra("questionByVocabulary", questionByVocabulary)
                quizIntent.putParcelableArrayListExtra("vocabularies", ArrayList(vocabularies))
                quizIntent.putExtra("instantFeedBack", binding.instantFeedBackSwitch.isChecked)
                quizIntent.putExtra("studyMode", studyMode)
                quizIntent.putParcelableArrayListExtra("bookmarkedVocabularies", ArrayList(bookmarkedVocabularies))
                startActivity(quizIntent)
            }
            else if(studyMode == StudyMode.Typing){
                val typingIntent = Intent(this, TypingActivity::class.java)
                val shuffleQuestion = binding.shuffleQuestionSwitch.isChecked
                typingIntent.putExtra("topic", topic)
                typingIntent.putExtra("studyLanguage", studyLanguage)
                typingIntent.putExtra("questionCount", if(binding.questionCountEdt.text.isEmpty()) vocabularies.size else binding.questionCountEdt.text.toString().toInt() )
                typingIntent.putExtra("shuffleQuestion", shuffleQuestion)
                typingIntent.putExtra("answerByDefinition", answerByDefinition)
                typingIntent.putExtra("answerByVocabulary", answerByVocabulary)
                typingIntent.putExtra("questionByDefinition", questionByDefinition)
                typingIntent.putExtra("questionByVocabulary", questionByVocabulary)
                typingIntent.putParcelableArrayListExtra("vocabularies", ArrayList(vocabularies))
                typingIntent.putExtra("instantFeedBack", binding.instantFeedBackSwitch.isChecked)
                typingIntent.putExtra("studyMode", studyMode)
                typingIntent.putParcelableArrayListExtra("bookmarkedVocabularies", ArrayList(bookmarkedVocabularies))
                startActivity(typingIntent)
            }
        }

        setContentView(binding.root)
    }

    override fun onChoosingOption(
        answerByDefinition: Boolean,
        answerByVocabulary: Boolean,
        questionByDefinition: Boolean,
        questionByVocabulary: Boolean,
    ) {
        this.answerByDefinition = answerByDefinition
        this.answerByVocabulary = answerByVocabulary
        this.questionByDefinition = questionByDefinition
        this.questionByVocabulary = questionByVocabulary
        if(!answerByDefinition && !answerByVocabulary){
            binding.startBtn.isEnabled = false
            binding.startBtn.background = getDrawable(R.drawable.login_btn)
        }
        else if(!questionByDefinition && !questionByVocabulary){
            binding.startBtn.isEnabled = false
            binding.startBtn.background = getDrawable(R.drawable.login_btn)
        }
        else if(answerByDefinition && questionByDefinition && !answerByVocabulary && !questionByVocabulary){
            binding.startBtn.isEnabled = false
            binding.startBtn.background = getDrawable(R.drawable.login_btn)
        }
        else if(answerByVocabulary && questionByVocabulary && !answerByDefinition && !questionByDefinition){
            binding.startBtn.isEnabled = false
            binding.startBtn.background = getDrawable(R.drawable.login_btn)
        }
        else{
            binding.startBtn.isEnabled = true
            binding.startBtn.background = getDrawable(R.drawable.register_btn)
        }
    }
}