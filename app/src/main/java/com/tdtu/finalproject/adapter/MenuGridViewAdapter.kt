package com.tdtu.finalproject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.tdtu.finalproject.R
import com.tdtu.finalproject.model.MenuItem

class MenuGridViewAdapter(private var mContext:Context, private var layout: Int, private var items: List<MenuItem>): ArrayAdapter<MenuItem>(mContext, layout, items) {
    private class ViewHolder{
        lateinit var image: ImageView
        lateinit var title: TextView
        lateinit var layout: LinearLayout
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view: View
        var viewHolder: ViewHolder

        if(convertView == null){
            val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(layout, parent, false)
            viewHolder = ViewHolder()
            viewHolder.layout = view.findViewById(R.id.homeMenuItem) as LinearLayout
            viewHolder.image = view.findViewById(R.id.menuItemImage) as ImageView
            viewHolder.title = view.findViewById(R.id.menuItemName) as TextView
            view.tag = viewHolder
        }
        else{
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val menuItem = items[position]
        viewHolder.title.text = menuItem.getName()
        viewHolder.image.setImageResource(menuItem.getImage())
        viewHolder.layout.setOnClickListener(menuItem.getOnClick())
        return view
    }
}