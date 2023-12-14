package com.tdtu.finalproject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tdtu.finalproject.R
import com.tdtu.finalproject.model.achievement.Achievement

class AchievementAdapter(private var mContext: Context, private var achievementList: List<Achievement>, private var layout: Int) : RecyclerView.Adapter<AchievementAdapter.ViewHolder>(){
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val achievementImage: ImageView = itemView.findViewById(R.id.achievementImage)
        val achievementName: TextView = itemView.findViewById(R.id.achievementName)
        val achievementObtained: TextView = itemView.findViewById(R.id.achievementObtained)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return achievementList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val achievement = achievementList[position]
        Picasso.get().load(achievement.image).into(holder.achievementImage)
        holder.achievementName.text = achievement.name
        holder.achievementObtained.text = mContext.getString(R.string.obtained)
    }

}