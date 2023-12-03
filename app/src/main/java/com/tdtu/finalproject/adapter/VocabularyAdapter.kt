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
import com.tdtu.finalproject.model.topic.Vocabulary

class VocabularyAdapter(private var mContext:Context, private var vocabularies: MutableList<Vocabulary>, private var layout: Int): RecyclerView.Adapter<VocabularyAdapter.VocabularyViewHolder>() {

    class VocabularyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val englishWordTxt: EditText = itemView.findViewById(R.id.englishWordTxt)
        val vietnameseWordTxt: EditText = itemView.findViewById(R.id.vietnameseWordTxt)
        val englishMeaningTxt: EditText = itemView.findViewById(R.id.englishMeaningTxt)
        val vietnameseMeaningTxt: EditText = itemView.findViewById(R.id.vietnameseMeaningTxt)
        val removeVocabularyBtn : ImageButton = itemView.findViewById(R.id.removeVocabularyBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VocabularyViewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(layout, parent,false)
        return VocabularyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return vocabularies.size
    }

    fun addVocabulary(vocabulary: Vocabulary) {
        vocabularies.add(vocabulary)
        notifyItemInserted(vocabularies.size - 1)
    }

    fun removeAllVocabulary() {
        vocabularies.clear()
        notifyDataSetChanged()
    }

    fun getVocabularies(): MutableList<Vocabulary> {
        return vocabularies
    }

    override fun onBindViewHolder(holder: VocabularyViewHolder, position: Int) {
        val vocabulary: Vocabulary = vocabularies[position]
        holder.englishWordTxt.setText(vocabulary.englishWord)
        holder.vietnameseWordTxt.setText(vocabulary.vietnameseWord)
        holder.englishMeaningTxt.setText(vocabulary.englishMeaning)
        holder.vietnameseMeaningTxt.setText(vocabulary.vietnameseMeaning)
        holder.removeVocabularyBtn.setOnClickListener{
            vocabularies.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition);
        }

        holder.englishMeaningTxt.addTextChangedListener {
            vocabulary.englishMeaning = it.toString()
        }
        holder.vietnameseMeaningTxt.addTextChangedListener {
            vocabulary.vietnameseMeaning = it.toString()
        }
        holder.englishWordTxt.addTextChangedListener {
            vocabulary.englishWord = it.toString()
        }
        holder.vietnameseWordTxt.addTextChangedListener {
            vocabulary.vietnameseWord = it.toString()
        }
    }
}