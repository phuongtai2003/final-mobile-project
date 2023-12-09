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
import androidx.lifecycle.lifecycleScope
import com.tdtu.finalproject.databinding.ActivityFlashCardBinding
import com.tdtu.finalproject.model.topic.Topic
import com.tdtu.finalproject.model.vocabulary.Vocabulary
import com.tdtu.finalproject.repository.DataRepository
import com.tdtu.finalproject.utils.FlashCardOptionsListener
import com.tdtu.finalproject.utils.Language
import com.tdtu.finalproject.utils.OnSwipeTouchListener
import com.tdtu.finalproject.utils.Utils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class FlashCardActivity : AppCompatActivity(), TextToSpeech.OnInitListener, FlashCardOptionsListener {
    private lateinit var binding: ActivityFlashCardBinding
    private lateinit var ttsEnglish: TextToSpeech
    private lateinit var ttsVietnamese: TextToSpeech
    private lateinit var vocabularies: List<Vocabulary>
    private var originalVocabularies: List<Vocabulary>? = null
    private lateinit var currentBookmarkedVocabularies: MutableList<Vocabulary>
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
    private var isShuffled = false
    private var isAutoPlayAudio = false
    private var isFrontFirst = false
    private var isAutoPlayCard = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlashCardBinding.inflate(layoutInflater)
        dataRepository = DataRepository.getInstance()
        sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_key), MODE_PRIVATE)
        ttsEnglish = TextToSpeech(this, this)
        ttsVietnamese = TextToSpeech(this, this)
        vocabularies = intent.getParcelableArrayListExtra<Vocabulary>("vocabularies")!!
        topic = intent.getParcelableExtra("topic")!!
        currentBookmarkedVocabularies = intent.getParcelableArrayListExtra("bookmarkedVocabularies")!!
        totalVocabularies = vocabularies.size
        frontAnim = AnimatorInflater.loadAnimator(this, R.animator.front_animator) as AnimatorSet
        backAnim = AnimatorInflater.loadAnimator(this, R.animator.back_animator) as AnimatorSet
        binding.closeBtn.setOnClickListener {
            finish()
        }
        binding.flashCardOptionBtn.setOnClickListener {
            isAutoPlayCard = false
            setVocabulary()
            val dialog = FlashCardOptionSheet()
            val bundle = Bundle()
            bundle.putBoolean("isShuffled", isShuffled)
            bundle.putBoolean("isAutoPlayAudio", isAutoPlayAudio)
            bundle.putSerializable("language", language)
            bundle.putBoolean("isFrontFirst", isFrontFirst)
            dialog.arguments = bundle
            dialog.show(supportFragmentManager, "FlashCardOptionSheet")
        }
        setVocabulary()
        binding.autoBtn.setOnClickListener {
            if(!isAutoPlayCard){
                isAutoPlayCard = true
                isAutoPlayAudio = true
                setVocabulary()
                binding.autoBtn.setImageResource(R.drawable.outline_pause_circle_24)
            }
            else{
                isAutoPlayCard = false
                isAutoPlayAudio = false
                setVocabulary()
                binding.autoBtn.setImageResource(R.drawable.baseline_play_arrow_24)
            }
        }
        setContentView(binding.root)
    }

    private fun setVocabulary(){
        if(index == totalVocabularies){
            finish()
            return
        }

        if(index == 0 && isAutoPlayCard){
            if(language == Language.ENGLISH){
                if(isFrontFirst){
                    if(isFront){
                        ttsEnglish.speak(vocabularies[index].englishWord, TextToSpeech.QUEUE_FLUSH, null, "")
                    }
                    else{
                        ttsEnglish.speak(vocabularies[index].englishMeaning, TextToSpeech.QUEUE_FLUSH, null, "")
                    }
                }
                else{
                    if(isFront){
                        ttsEnglish.speak(vocabularies[index].englishMeaning, TextToSpeech.QUEUE_FLUSH, null, "")
                    }else{
                        ttsEnglish.speak(vocabularies[index].englishWord, TextToSpeech.QUEUE_FLUSH, null, "")
                    }
                }
            }
            else{
                if(isFrontFirst){
                    if(isFront){
                        ttsVietnamese.speak(vocabularies[index].vietnameseWord, TextToSpeech.QUEUE_FLUSH, null, "")
                    }
                    else{
                        ttsVietnamese.speak(vocabularies[index].vietnameseMeaning, TextToSpeech.QUEUE_FLUSH, null, "")
                    }
                }
                else{
                    if(isFront){
                        ttsVietnamese.speak(vocabularies[index].vietnameseMeaning, TextToSpeech.QUEUE_FLUSH, null, "")
                    }else{
                        ttsVietnamese.speak(vocabularies[index].vietnameseWord, TextToSpeech.QUEUE_FLUSH, null, "")
                    }
                }
            }
        }

        frontAnim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
                isFlipping = true
            }
            override fun onAnimationEnd(p0: Animator) {
                isFlipping = false
                if(isAutoPlayAudio){
                    if(language == Language.ENGLISH){
                        if(isFrontFirst){
                            if(isFront){
                                ttsEnglish.speak(vocabularies[index].englishWord, TextToSpeech.QUEUE_FLUSH, null, "")
                            }
                            else{
                                ttsEnglish.speak(vocabularies[index].englishMeaning, TextToSpeech.QUEUE_FLUSH, null, "")
                            }
                        }
                        else{
                            if(isFront){
                                ttsEnglish.speak(vocabularies[index].englishMeaning, TextToSpeech.QUEUE_FLUSH, null, "")
                            }else{
                                ttsEnglish.speak(vocabularies[index].englishWord, TextToSpeech.QUEUE_FLUSH, null, "")
                            }
                        }
                    }
                    else{
                        if(isFrontFirst){
                            if(isFront){
                                ttsVietnamese.speak(vocabularies[index].vietnameseWord, TextToSpeech.QUEUE_FLUSH, null, "")
                            }
                            else{
                                ttsVietnamese.speak(vocabularies[index].vietnameseMeaning, TextToSpeech.QUEUE_FLUSH, null, "")
                            }
                        }
                        else{
                            if(isFront){
                                ttsVietnamese.speak(vocabularies[index].vietnameseMeaning, TextToSpeech.QUEUE_FLUSH, null, "")
                            }else{
                                ttsVietnamese.speak(vocabularies[index].vietnameseWord, TextToSpeech.QUEUE_FLUSH, null, "")
                            }
                        }
                    }
                }
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

        if(!isAutoPlayCard){
            if (isFront) {
                frontAnim.setTarget(binding.cardFrontWordView)
                backAnim.setTarget(binding.cardBackWordView)
                frontAnim.start()
                backAnim.start()
                isFront = false
            }else{
                if(isAutoPlayAudio){
                    if(language == Language.ENGLISH){
                        if(isFrontFirst){
                            ttsEnglish.speak(vocabularies[index].englishMeaning, TextToSpeech.QUEUE_FLUSH, null, "")
                        }
                        else{
                            ttsEnglish.speak(vocabularies[index].englishWord, TextToSpeech.QUEUE_FLUSH, null, "")
                        }
                    }
                    else{
                        if(isFrontFirst){
                            ttsVietnamese.speak(vocabularies[index].vietnameseMeaning, TextToSpeech.QUEUE_FLUSH, null, "")
                        }
                        else{
                            ttsVietnamese.speak(vocabularies[index].vietnameseWord, TextToSpeech.QUEUE_FLUSH, null, "")
                        }
                    }
                }
            }
        }

        isBookmarked = currentBookmarkedVocabularies.map {
            it.id
        }.contains(vocabularies[index].id)

        if(isBookmarked){
            binding.bookmarkVocabularyBtn.setImageResource(R.drawable.baseline_star_24)
            binding.bookmarkVocabBtn.setImageResource(R.drawable.baseline_star_24)
        }
        else{
            binding.bookmarkVocabularyBtn.setImageResource(R.drawable.outline_star_outline_24)
            binding.bookmarkVocabBtn.setImageResource(R.drawable.outline_star_outline_24)
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
                binding.bookmarkVocabBtn.setImageResource(R.drawable.baseline_star_24)
                isBookmarked = true
            }
            else{
                removeBookmarkVocabulary(vocabularies[index])
                binding.bookmarkVocabularyBtn.setImageResource(R.drawable.outline_star_outline_24)
                binding.bookmarkVocabBtn.setImageResource(R.drawable.outline_star_outline_24)
                isBookmarked = false
            }
        }
        binding.bookmarkVocabBtn.setOnClickListener {
            if(!isBookmarked){
                bookmarkVocabulary(vocabularies[index])
                binding.bookmarkVocabBtn.setImageResource(R.drawable.baseline_star_24)
                binding.bookmarkVocabularyBtn.setImageResource(R.drawable.baseline_star_24)
                isBookmarked = true
            }
            else{
                removeBookmarkVocabulary(vocabularies[index])
                binding.bookmarkVocabularyBtn.setImageResource(R.drawable.outline_star_outline_24)
                binding.bookmarkVocabBtn.setImageResource(R.drawable.outline_star_outline_24)
                isBookmarked = false
            }
        }

        binding.nextVocabularyBtn.setOnClickListener {
            Handler().postDelayed({
                runOnUiThread {
                    ttsEnglish.stop()
                    ttsVietnamese.stop()
                    if (isFront) {
                        frontAnim.setTarget(binding.cardFrontWordView)
                        backAnim.setTarget(binding.cardBackWordView)
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
                    if(isFrontFirst){
                        ttsEnglish.speak(englishMeaning, TextToSpeech.QUEUE_FLUSH, null, "")
                    }
                    else{
                        ttsEnglish.speak(englishWord, TextToSpeech.QUEUE_FLUSH, null, "")
                    }
                }
                else{
                    if(isFrontFirst){
                        ttsVietnamese.speak(vietnameseMeaning, TextToSpeech.QUEUE_FLUSH, null, "")
                    }else{
                        ttsVietnamese.speak(vietnameseWord, TextToSpeech.QUEUE_FLUSH, null, "")
                    }
                }
            }
            binding.cardFrontTextToSpeechBtn.setOnClickListener {
                if(language == Language.ENGLISH){
                    if(isFrontFirst){
                        ttsEnglish.speak(englishWord, TextToSpeech.QUEUE_FLUSH, null, "")
                    }
                    else{
                        ttsEnglish.speak(englishMeaning, TextToSpeech.QUEUE_FLUSH, null, "")
                    }
                }
                else{
                    if(isFrontFirst){
                        ttsVietnamese.speak(vietnameseWord, TextToSpeech.QUEUE_FLUSH, null, "")
                    }
                    else{
                        ttsVietnamese.speak(vietnameseMeaning, TextToSpeech.QUEUE_FLUSH, null, "")
                    }
                }
            }
            if(isFrontFirst){
                binding.cardBackTextView.text = if(language == Language.ENGLISH) englishMeaning else vietnameseMeaning
                binding.cardFrontTextView.text = if(language == Language.ENGLISH) englishWord else vietnameseWord
            }
            else{
                binding.cardBackTextView.text = if(language == Language.ENGLISH) englishWord else vietnameseWord
                binding.cardFrontTextView.text = if(language == Language.ENGLISH) englishMeaning else vietnameseMeaning
            }
        }
        val scale = this.resources.displayMetrics.density
        binding.cardFrontWordView.cameraDistance = 8000 * scale
        binding.cardBackWordView.cameraDistance = 8000 * scale
        binding.flashCardLayout.setOnClickListener {
            if(!isFlipping){
                ttsEnglish.stop()
                ttsVietnamese.stop()
                if (isFront) {
                    frontAnim.setTarget(binding.cardFrontWordView)
                    backAnim.setTarget(binding.cardBackWordView)
                    frontAnim.start()
                    backAnim.start()
                    isFront = false
                } else {
                    frontAnim.setTarget(binding.cardBackWordView)
                    backAnim.setTarget(binding.cardFrontWordView)
                    frontAnim.start()
                    backAnim.start()
                    isFront = true
                }
            }
        }
        binding.flashCardActivityLayout.setOnTouchListener(object : OnSwipeTouchListener(){
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                if(index < totalVocabularies){
                    index++
                    setVocabulary()
                }
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                if(index > 0){
                    index--
                    setVocabulary()
                }
            }
        })
        if(isAutoPlayCard){
            lifecycleScope.launch {
                if(index != 0){
                    ttsEnglish.stop()
                    ttsVietnamese.stop()
                    if (isFront) {
                        frontAnim.setTarget(binding.cardFrontWordView)
                        backAnim.setTarget(binding.cardBackWordView)
                        frontAnim.start()
                        backAnim.start()
                        isFront = false
                    }
                    else{
                        backAnim.setTarget(binding.cardFrontWordView)
                        frontAnim.setTarget(binding.cardBackWordView)
                        backAnim.start()
                        frontAnim.start()
                        isFront = true
                    }
                }
                delay(3000)
                if (isFront) {
                    frontAnim.setTarget(binding.cardFrontWordView)
                    backAnim.setTarget(binding.cardBackWordView)
                    frontAnim.start()
                    backAnim.start()
                    isFront = false
                }
                else{
                    backAnim.setTarget(binding.cardFrontWordView)
                    frontAnim.setTarget(binding.cardBackWordView)
                    backAnim.start()
                    frontAnim.start()
                    isFront = true
                }
                delay(3000)
                index++
                setVocabulary()
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
        dataRepository.createBookmarkVocabularies(listOf(vocabulary), sharedPreferences.getString(getString(R.string.token_key), null)!!).thenAcceptAsync {
            runOnUiThread {
                Utils.showSnackBar(binding.root, getString(R.string.bookmark_vocabulary_success))
            }
            currentBookmarkedVocabularies.add(vocabulary)
        }.exceptionally {
            runOnUiThread {
                Utils.showSnackBar(binding.root, getString(R.string.bookmark_vocabulary_failed))
            }
            null
        }
    }
    private fun removeBookmarkVocabulary(vocabulary: Vocabulary){
        dataRepository.removeBookmarkedVocabularies(sharedPreferences.getString(getString(R.string.token_key), null)!!, vocabulary.id!!).thenAcceptAsync {
            runOnUiThread {
                Utils.showSnackBar(binding.root, getString(R.string.remove_bookmark_vocabulary_success))
            }
            currentBookmarkedVocabularies.removeAt(currentBookmarkedVocabularies.indexOfFirst { it.id == vocabulary.id })
        }.exceptionally {
            runOnUiThread {
                Utils.showSnackBar(binding.root, getString(R.string.remove_bookmark_vocabulary_failed))
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

    override fun onApply(
        isShuffle: Boolean,
        isAutoPlaySound: Boolean,
        isFrontFirst: Boolean,
        studyLanguage: Language
    ) {
        this.isShuffled = isShuffle
        this.isAutoPlayAudio = isAutoPlaySound
        this.isFrontFirst = isFrontFirst
        this.language = studyLanguage
        index = 0
        if(isShuffle){
            originalVocabularies = ArrayList(vocabularies)
            vocabularies = vocabularies.shuffled()
        }
        else{
            if(originalVocabularies != null){
                vocabularies = ArrayList(originalVocabularies!!)
            }
        }
        setVocabulary()
    }
}