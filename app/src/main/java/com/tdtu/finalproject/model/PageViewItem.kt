package com.tdtu.finalproject.model

class PageViewItem (private var image: Int, private var description: String){
    fun getImage() = image
    fun setImage(image: Int) {
        this.image = image
    }
    fun getDescription() = description
    fun setDescription(description: String) {
        this.description = description
    }
}