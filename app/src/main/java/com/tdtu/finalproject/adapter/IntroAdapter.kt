package com.tdtu.finalproject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tdtu.finalproject.R
import com.tdtu.finalproject.model.PageViewItem

class IntroAdapter(private var mContext:Context, private var layout: Int, private var introList:ArrayList<PageViewItem>) :
    RecyclerView.Adapter<IntroAdapter.IntroHolder>() {

    class IntroHolder(private var itemView: View): RecyclerView.ViewHolder(itemView) {
        val image:ImageView = itemView.findViewById<ImageView>(R.id.introImg)
        val description: TextView = itemView.findViewById<TextView>(R.id.descriptionTxt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroHolder {
        val view: View = LayoutInflater.from(mContext).inflate(layout, parent,false)
        return IntroHolder(view)
    }

    override fun onBindViewHolder(holder: IntroHolder, position: Int) {
        val item: PageViewItem = introList[position]
        holder.image.setImageResource(item.getImage())
        holder.description.text = item.getDescription()
    }

    override fun getItemCount(): Int {
        return introList.size
    }
}