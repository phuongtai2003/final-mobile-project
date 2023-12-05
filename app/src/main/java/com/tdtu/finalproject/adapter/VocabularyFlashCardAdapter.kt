package com.tdtu.finalproject.adapter

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import androidx.recyclerview.widget.RecyclerView
import com.tdtu.finalproject.R
import com.tdtu.finalproject.model.vocabulary.Vocabulary

class VocabularyFlashCardAdapter(private var studyEnglishMode: Boolean, private var mContext: Context, private var vocabularies: List<Vocabulary>, private var layout: Int, private var ttsEnglish: TextToSpeech?, private var ttsVietnamese: TextToSpeech?): RecyclerView.Adapter<VocabularyFlashCardAdapter.ViewHolder>(){
    private var frontAnim: AnimatorSet = AnimatorInflater.loadAnimator(mContext, R.animator.front_animator) as AnimatorSet
    private var backAnim: AnimatorSet = AnimatorInflater.loadAnimator(mContext, R.animator.back_animator) as AnimatorSet
    private var scale = mContext.resources.displayMetrics.density
    private var isFlipping = false

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardViewFront: CardView = itemView.findViewById(R.id.cardFrontWordView)
        val cardViewBack: CardView = itemView.findViewById(R.id.cardBackWordView)
        val backView: TextView = itemView.findViewById(R.id.cardBackTextView)
        val frontView : TextView = itemView.findViewById(R.id.cardFrontTextView)
        val ttsFrontBtn : ImageButton = itemView.findViewById(R.id.cardFrontTextToSpeechBtn)
        val ttsBackBtn : ImageButton = itemView.findViewById(R.id.cardBackTextToSpeechBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return vocabularies.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vocabulary = vocabularies[position]
        holder.backView.text = if (studyEnglishMode) vocabulary.vietnameseWord else vocabulary.englishWord
        holder.frontView.text = if (studyEnglishMode) vocabulary.englishWord else vocabulary.vietnameseWord

        if(ttsVietnamese == null || ttsEnglish == null){
            holder.ttsBackBtn.visibility = View.GONE
            holder.ttsFrontBtn.visibility = View.GONE
        }
        else{
            holder.ttsBackBtn.setOnClickListener {
                if(studyEnglishMode){
                    ttsVietnamese?.speak(vocabulary.vietnameseWord, TextToSpeech.QUEUE_FLUSH, null, null)
                }
                else{
                    ttsEnglish?.speak(vocabulary.englishWord, TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
            holder.ttsFrontBtn.setOnClickListener {
                if(studyEnglishMode){
                    ttsEnglish?.speak(vocabulary.englishWord, TextToSpeech.QUEUE_FLUSH, null, null)
                }
                else{
                    ttsVietnamese?.speak(vocabulary.vietnameseWord, TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
        }

        holder.cardViewFront.cameraDistance = 8000 * scale
        holder.cardViewBack.cameraDistance = 8000 * scale
        holder.itemView.setOnClickListener {
            if(!isFlipping){
                if (vocabulary.isFront) {
                    frontAnim.setTarget(holder.cardViewFront)
                    backAnim.setTarget(holder.cardViewBack)
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
                    vocabulary.isFront = false
                } else {
                    frontAnim.setTarget(holder.cardViewBack)
                    backAnim.setTarget(holder.cardViewFront)
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
                    vocabulary.isFront = true
                }
            }
        }
    }
}