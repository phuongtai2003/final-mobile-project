package com.tdtu.finalproject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import com.tdtu.finalproject.R
import com.tdtu.finalproject.model.topic.Topic
import com.tdtu.finalproject.model.user.User

class TopicAdapter(private var mContext: Context, private var topics: MutableList<Topic>, private var layout:Int, private var user: User): RecyclerView.Adapter<TopicAdapter.TopicViewHolder>() {
    class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val topicNameTxt: TextView = itemView.findViewById(R.id.topicItemNameTxt)
        val topicTermsCount: TextView = itemView.findViewById(R.id.termsCount)
        val topicOwnerImg: ShapeableImageView = itemView.findViewById(R.id.topicOwnerImg)
        val topicOwnerNameTxt: TextView = itemView.findViewById(R.id.topicOwnerNameTxt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(layout, parent,false)
        return TopicViewHolder(view)
    }

    override fun getItemCount(): Int {
        return topics.size
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        val topic: Topic = topics[position]
        holder.topicNameTxt.text = topic.topicNameEnglish
        holder.topicTermsCount.text = topic.vocabularyCount.toString() + " " + mContext.getString(R.string.vocabulary)
        holder.topicOwnerNameTxt.text = user.username
        Picasso.get().load(user.profileImage).into(holder.topicOwnerImg)
    }
}