package com.tdtu.finalproject

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import com.tdtu.finalproject.databinding.ActivityFlashCardBinding
import com.tdtu.finalproject.model.topic.Topic
import com.tdtu.finalproject.model.vocabulary.Vocabulary
import com.tdtu.finalproject.repository.DataRepository
import com.tdtu.finalproject.utils.Language
import com.tdtu.finalproject.utils.Utils
import java.util.Locale

class FlashCardActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var binding: ActivityFlashCardBinding
    private lateinit var ttsEnglish: TextToSpeech
    private lateinit var ttsVietnamese: TextToSpeech
    private lateinit var vocabularies: List<Vocabulary>
    private lateinit var topic: Topic
    private var index = 0
    private var totalVocabularies = 0
    private var language = Language.ENGLISH
    private lateinit var frontAnim: AnimatorSet
    private lateinit var backAnim: AnimatorSet
    private var isFlipping = false
    private var isFront = false
    private lateinit var dataRepository: DataRepository
    private lateinit var sharedPreferences: SharedPreferences
    private var isBookmarked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlashCardBinding.inflate(layoutInflater)
        dataRepository = DataRepository.getInstance()
        sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_key), MODE_PRIVATE)
        ttsEnglish = TextToSpeech(this, this)
        ttsVietnamese = TextToSpeech(this, this)
        vocabularies = intent.getParcelableArrayListExtra<Vocabulary>("vocabularies")!!
        topic = intent.getParcelableExtra("topic")!!
        totalVocabularies = vocabularies.size
        frontAnim = AnimatorInflater.loadAnimator(this, R.animator.front_animator) as AnimatorSet
        backAnim = AnimatorInflater.loadAnimator(this, R.animator.back_animator) as AnimatorSet
        binding.closeBtn.setOnClickListener {
            finish()
        }

        setVocabulary()

        setContentView(binding.root)
    }

    private fun setVocabulary(){
        if(index == totalVocabularies){
            finish()
            return
        }

        binding.quizProgressTxt.text = "${index + 1}/$totalVocabularies"
        binding.prevVocabularyBtn.isEnabled = index != 0
        binding.prevVocabularyBtn.setColorFilter(resources.getColor(if(index == 0) R.color.grey else R.color.white))
        binding.prevVocabularyBtn.setOnClickListener {
            index--
            setVocabulary()
        }
        binding.bookmarkVocabularyBtn.setOnClickListener {
            if(!isBookmarked){
                bookmarkVocabulary(vocabularies[index])
                binding.bookmarkVocabularyBtn.setImageResource(R.drawable.baseline_star_24)
            }
        }
        binding.bookmarkVocabBtn.setOnClickListener {
            if(!isBookmarked){
                bookmarkVocabulary(vocabularies[index])
                binding.bookmarkVocabBtn.setImageResource(R.drawable.baseline_star_24)
            }
        }

        binding.autoPlayBtn.setOnClickListener {
            Handler().postDelayed({
                runOnUiThread {
                    ttsEnglish.stop()
                    ttsVietnamese.stop()
                    if (isFront) {
                        frontAnim.setTarget(binding.cardFrontWordView)
                        backAnim.setTarget(binding.cardBackWordView)
                        frontAnim.addListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(p0: Animator) {
                                isFlipping = true
                            }

                            override fun onAnimationEnd(p0: Animator) {
                                isFlipping = false
                            }

                            override fun onAnimationCancel(p0: Animator) {
                            }

                            override fun onAnimationRepeat(p0: Animator) {
                            }

                        })
                        backAnim.addListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(p0: Animator) {
                                isFlipping = true
                            }

                            override fun onAnimationEnd(p0: Animator) {
                                isFlipping = false
                            }

                            override fun onAnimationCancel(p0: Animator) {
                            }

                            override fun onAnimationRepeat(p0: Animator) {
                            }

                        })
                        frontAnim.start()
                        backAnim.start()
                        isFront = false
                    }
                    index++
                    setVocabulary()
                }
            }, if (isFront) 500 else 0)
        }

        vocabularies[index].apply {
            binding.cardBackTextToSpeechBtn.setOnClickListener {
                if(language == Language.ENGLISH){
                    ttsEnglish.speak(englishWord, TextToSpeech.QUEUE_FLUSH, null, "")
                }
                else{
                    ttsVietnamese.speak(vietnameseWord, TextToSpeech.QUEUE_FLUSH, null, "")
                }
            }
            binding.cardFrontTextToSpeechBtn.setOnClickListener {
                if(language == Language.ENGLISH){
                    ttsEnglish.speak(englishMeaning, TextToSpeech.QUEUE_FLUSH, null, "")
                }
                else{
                    ttsVietnamese.speak(vietnameseMeaning, TextToSpeech.QUEUE_FLUSH, null, "")
                }
            }
            binding.cardBackTextView.text = if(language == Language.ENGLISH) englishWord else vietnameseWord
            binding.cardFrontTextView.text = if(language == Language.ENGLISH) englishMeaning else vietnameseMeaning
        }
        val scale = this.resources.displayMetrics.density
        binding.cardFrontWordView.cameraDistance = 8000 * scale
        binding.cardBackWordView.cameraDistance = 8000 * scale
        binding.flashCardLayout.setOnClickListener {
            if(!isFlipping){
                if (isFront) {
                    frontAnim.setTarget(binding.cardFrontWordView)
                    backAnim.setTarget(binding.cardBackWordView)
                    frontAnim.addListener(object : Animator.AnimatorListener{
                        override fun onAnimationStart(p0: Animator) {
                            isFlipping = true
                        }
                        override fun onAnimationEnd(p0: Animator) {
                            isFlipping = false
                        }
                        override fun onAnimationCancel(p0: Animator) {
                        }

                        override fun onAnimationRepeat(p0: Animator) {
                        }

                    })
                    backAnim.addListener(object : Animator.AnimatorListener{
                        override fun onAnimationStart(p0: Animator) {
                            isFlipping = true
                        }

                        override fun onAnimationEnd(p0: Animator) {
                            isFlipping = false
                        }

                        override fun onAnimationCancel(p0: Animator) {
                        }

                        override fun onAnimationRepeat(p0: Animator) {
                        }

                    })
                    frontAnim.start()
                    backAnim.start()
                    isFront = false
                } else {
                    frontAnim.setTarget(binding.cardBackWordView)
                    backAnim.setTarget(binding.cardFrontWordView)
                    frontAnim.addListener(object : Animator.AnimatorListener{
                        override fun onAnimationStart(p0: Animator) {
                            isFlipping = true
                        }

                        override fun onAnimationEnd(p0: Animator) {
                            isFlipping = false
                        }

                        override fun onAnimationCancel(p0: Animator) {
                        }

                        override fun onAnimationRepeat(p0: Animator) {
                        }

                    })
                    backAnim.addListener(object : Animator.AnimatorListener{
                        override fun onAnimationStart(p0: Animator) {
                            isFlipping = true
                        }

                        override fun onAnimationEnd(p0: Animator) {
                            isFlipping = false
                        }

                        override fun onAnimationCancel(p0: Animator) {
                        }

                        override fun onAnimationRepeat(p0: Animator) {
                        }

                    })
                    frontAnim.start()
                    backAnim.start()
                    isFront = true
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

    private fun bookmarkVocabulary(vocabulary: Vocabulary){
        dataRepository.createBookmarkVocabularies(listOf(vocabulary), sharedPreferences.getString(getString(R.string.token_key), null)!!).exceptionally {
            runOnUiThread {
                Log.d("USER TAG", "createBookmarkVocabularies: " + it.message)
                Utils.showSnackBar(binding.root, getString(R.string.bookmark_vocabulary_failed))
            }
            null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ttsEnglish.stop()
        ttsEnglish.shutdown()
        ttsVietnamese.stop()
        ttsVietnamese.shutdown()
    }
}