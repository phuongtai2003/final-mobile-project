package com.tdtu.finalproject.model.common

import android.view.View.OnClickListener

class MenuItem(private var image: Int, private var name: String, private var onClick: OnClickListener){
    fun getImage() = image
    fun setImage(image: Int) {
        this.image = image
    }
    fun getName() = name
    fun setName(description: String) {
        this.name = description
    }
    fun getOnClick() = onClick
    fun setOnClick(onClick: OnClickListener) {
        this.onClick = onClick
    }
}