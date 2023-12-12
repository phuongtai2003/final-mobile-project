package com.tdtu.finalproject.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.tdtu.finalproject.R
import com.tdtu.finalproject.model.vocabulary.Vocabulary

class VocabularyAdapter(private var mContext:Context, private var vocabularies: ArrayList<Vocabulary>, private var layout: Int): RecyclerView.Adapter<VocabularyAdapter.VocabularyViewHolder>() {
    private var downloadConditions = DownloadConditions.Builder().requireWifi().build()
    private var engToVietOptions = TranslatorOptions.Builder().setSourceLanguage(TranslateLanguage.ENGLISH).setTargetLanguage(TranslateLanguage.VIETNAMESE).build()
    private var vietToEngOptions = TranslatorOptions.Builder().setSourceLanguage(TranslateLanguage.ENGLISH).setTargetLanguage(TranslateLanguage.VIETNAMESE).build()
    private val engToVietTranslator = Translation.getClient(engToVietOptions)
    private val vietToEngTranslator = Translation.getClient(vietToEngOptions)
    class VocabularyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var englishWord: EditText = itemView.findViewById(R.id.englishWordTxt)
        var vietnameseWord: EditText = itemView.findViewById(R.id.vietnameseWordTxt)
        var englishMeaning: EditText = itemView.findViewById(R.id.englishMeaningTxt)
        var vietnameseMeaning: EditText = itemView.findViewById(R.id.vietnameseMeaningTxt)
        var removeVocabularyBtn: ImageButton = itemView.findViewById(R.id.removeVocabularyBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VocabularyViewHolder {
        val view = LayoutInflater.from(mContext).inflate(layout, parent, false)
        return VocabularyViewHolder(view)
    }

    override fun onBindViewHolder(holder: VocabularyViewHolder, position: Int) {
        val vocabulary = vocabularies[position]
        holder.englishWord.setText(vocabulary.englishWord)
        holder.vietnameseWord.setText(vocabulary.vietnameseWord)
        holder.englishMeaning.setText(vocabulary.englishMeaning)
        holder.vietnameseMeaning.setText(vocabulary.vietnameseMeaning)

        holder.englishWord.addTextChangedListener { text ->
            engToVietTranslator.downloadModelIfNeeded(downloadConditions).addOnSuccessListener {
                engToVietTranslator.translate(text.toString()).addOnSuccessListener {translatedText->
                    holder.vietnameseWord.setText(translatedText)
                }.addOnFailureListener{
                    holder.vietnameseWord.setText("")
                }
            }
            vocabulary.englishWord = text.toString()
        }
        holder.vietnameseWord.addTextChangedListener { editable ->
            vocabulary.vietnameseWord = editable.toString()
        }
        holder.englishMeaning.addTextChangedListener { editable ->
            engToVietTranslator.downloadModelIfNeeded(downloadConditions).addOnSuccessListener {
                engToVietTranslator.translate(editable.toString()).addOnSuccessListener {translatedText->
                    holder.vietnameseMeaning.setText(translatedText)
                }.addOnFailureListener{
                    holder.vietnameseMeaning.setText("")
                }
            }
            vocabulary.englishMeaning = editable.toString()
        }
        holder.vietnameseMeaning.addTextChangedListener {
            vocabulary.vietnameseMeaning = it.toString()
        }

        holder.removeVocabularyBtn.setOnClickListener{
            vocabularies.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
            notifyItemRangeChanged(holder.adapterPosition, vocabularies.size)
        }
    }

    override fun getItemCount(): Int {
        return vocabularies.size
    }
}