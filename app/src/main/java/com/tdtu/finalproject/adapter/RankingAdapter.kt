package com.tdtu.finalproject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import com.tdtu.finalproject.R
import com.tdtu.finalproject.model.learning_statistics.LearningStatistic

class RankingAdapter(private var mContext: Context, private var statisticsList: List<LearningStatistic>, private var layout: Int) : RecyclerView.Adapter<RankingAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userImageView: ShapeableImageView = itemView.findViewById(R.id.userImageView)
        val usernameTextView : TextView = itemView.findViewById(R.id.usernameTextView)
        val topicLearningCountTxt: TextView = itemView.findViewById(R.id.topicLearningCountTxt)
        val vocabulariesLearnedTxt: TextView = itemView.findViewById(R.id.vocabulariesLearnedTxt)
        val timeLearnedTxt: TextView = itemView.findViewById(R.id.timeLearnedTxt)
        val progressIndicator: ProgressBar = itemView.findViewById(R.id.progressIndicator)
        val progressTxt : TextView = itemView.findViewById(R.id.progressTxt)
        val rankingTxt: TextView = itemView.findViewById(R.id.rankingTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return statisticsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val statistic = statisticsList[position]
        Picasso.get().load(statistic.userId.profileImage).into(holder.userImageView)
        holder.usernameTextView.text = statistic.userId.username
        holder.rankingTxt.text = "${mContext.getString(R.string.rank)}: ${(position + 1)}"
        holder.topicLearningCountTxt.text = "${mContext.getString(R.string.learning_count)}: ${statistic.learningCount}"
        holder.vocabulariesLearnedTxt.text = "${mContext.getString(R.string.vocabularies_learned)}: ${statistic.vocabLearned}"
        val hours = statistic.learningTime / 3600
        val minutes = (statistic.learningTime % 3600) / 60
        val remainingSeconds = statistic.learningTime % 60
        holder.timeLearnedTxt.text = "${mContext.getString(R.string.time_learned)}: ${String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)}"
        val percentage = (statistic.learningPercentage * 100).toInt()
        holder.progressTxt.text = "$percentage%"
        holder.progressIndicator.progress = percentage
    }

}