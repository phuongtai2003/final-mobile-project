package com.tdtu.finalproject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tdtu.finalproject.R
import com.tdtu.finalproject.model.results.ResultData

class FeedbackResultAdapter(private var mContext: Context, private var resultList: List<ResultData>, private var layout: Int)
    : RecyclerView.Adapter<FeedbackResultAdapter.FeedbackResultViewHolder>() {
    class FeedbackResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val resultQuestionTxt: TextView = itemView.findViewById(R.id.resultQuestionTxt)
        val resultCorrectTxt: TextView = itemView.findViewById(R.id.resultCorrectTxt)
        val resultIncorrectTxt: TextView = itemView.findViewById(R.id.resultIncorrectTxt)
        val resultTxt : TextView = itemView.findViewById(R.id.resultTxt)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FeedbackResultViewHolder {
        val view = LayoutInflater.from(mContext).inflate(layout, parent, false)
        return FeedbackResultViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: FeedbackResultViewHolder,
        position: Int
    ) {
        val result = resultList[position]
        holder.resultQuestionTxt.text = result.question
        holder.resultCorrectTxt.text = result.answer
        holder.resultIncorrectTxt.text = result.chosenAnswer
        holder.resultTxt.text = if(result.correct) mContext.getString(R.string.correct) else mContext.getString(R.string.incorrect)
        holder.resultTxt.setBackgroundColor(if(result.correct) mContext.getColor(R.color.dark_green_color) else mContext.getColor(R.color.red_color))
        if(result.correct){
            holder.resultCorrectTxt.visibility = View.VISIBLE
            holder.resultIncorrectTxt.visibility = View.GONE
        }else{
            holder.resultCorrectTxt.visibility = View.VISIBLE
            holder.resultIncorrectTxt.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return resultList.size
    }
}