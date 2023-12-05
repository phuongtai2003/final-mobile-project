package com.tdtu.finalproject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.tdtu.finalproject.R
import com.tdtu.finalproject.model.vocabulary.Vocabulary

class VocabularyAdapter(private var mContext:Context, private var vocabularies: ArrayList<Vocabulary>, private var layout: Int): RecyclerView.Adapter<VocabularyAdapter.VocabularyViewHolder>() {
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

        holder.englishWord.addTextChangedListener {
            vocabularies[position].englishWord = it.toString()
        }
        holder.vietnameseWord.addTextChangedListener {
            vocabularies[position].vietnameseWord = it.toString()
        }
        holder.englishMeaning.addTextChangedListener {
            vocabularies[position].englishMeaning = it.toString()
        }
        holder.vietnameseMeaning.addTextChangedListener {
            vocabularies[position].vietnameseMeaning = it.toString()
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