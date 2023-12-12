package com.tdtu.finalproject.model.vocabulary

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.google.gson.annotations.SerializedName

data class Vocabulary(
    @SerializedName("_id")
    var id: String?,
    var englishWord: String?,
    var vietnameseWord: String?,
    var englishMeaning: String?,
    var vietnameseMeaning: String?,
    var topicId: String?,
    var vocabularyStatisticId: List<String>,
    var bookmarkVocabularyId: List<String>,
    var isFront: Boolean = false
): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList()!!,
        parcel.createStringArrayList()!!,
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(englishWord)
        parcel.writeString(vietnameseWord)
        parcel.writeString(englishMeaning)
        parcel.writeString(vietnameseMeaning)
        parcel.writeString(topicId)
        parcel.writeStringList(vocabularyStatisticId)
        parcel.writeStringList(bookmarkVocabularyId)
        parcel.writeByte(if (isFront) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Vocabulary> {
        override fun createFromParcel(parcel: Parcel): Vocabulary {
            return Vocabulary(parcel)
        }

        override fun newArray(size: Int): Array<Vocabulary?> {
            return arrayOfNulls(size)
        }
    }

    override fun equals(other: Any?): Boolean {
        if(other is Vocabulary){
            return this.id == other.id
        }

        if (javaClass != other?.javaClass) return false

        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}