package com.tdtu.finalproject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import com.tdtu.finalproject.R
import com.tdtu.finalproject.model.folder.Folder
import com.tdtu.finalproject.model.user.User
import com.tdtu.finalproject.utils.CustomOnItemClickListener

class ChooseFolderAdapter(private var mContext: Context, private var folders: MutableList<Folder>, private var layout:Int, private var user: User):
    RecyclerView.Adapter<ChooseFolderAdapter.FolderViewHolder>() {
    class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val folderEnglishNameTxt: TextView = itemView.findViewById<TextView>(R.id.folderNameEnglishTv)
        val folderVietnameseNameTxt: TextView = itemView.findViewById<TextView>(R.id.folderNameVietnameseTv)
        val folderOwnerImg: ShapeableImageView = itemView.findViewById<ShapeableImageView>(R.id.folderOwnerImg)
        val folderOwnerNameTxt: TextView = itemView.findViewById<TextView>(R.id.folderOwnerNameTxt)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(layout, parent,false)
        return FolderViewHolder(view)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder: Folder = folders[position]
        holder.itemView.background = if (folder.isChosen) mContext.getDrawable(R.drawable.chosen_topic_library_bg) else mContext.getDrawable(R.drawable.topic_library_bg)
        holder.itemView.setOnClickListener{
            folder.isChosen = !folder.isChosen
            notifyItemChanged(position)
        }
        holder.folderEnglishNameTxt.text = folder.folderNameEnglish
        holder.folderVietnameseNameTxt.text = folder.folderNameVietnamese
        holder.folderOwnerNameTxt.text = user.username
        Picasso.get().load(user.profileImage).into(holder.folderOwnerImg)
    }

    override fun getItemCount(): Int {
        return folders.size
    }

}