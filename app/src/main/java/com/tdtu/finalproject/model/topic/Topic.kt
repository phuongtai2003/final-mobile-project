package com.tdtu.finalproject.model.topic

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.tdtu.finalproject.model.user.User
import java.util.ArrayList

data class Topic(
    @SerializedName("_id")
    val id: String?,
    val topicNameEnglish: String?,
    val topicNameVietnamese: String?,
    val vocabularyCount: Int,
    val isPublic: Boolean,
    val upVoteCount: Int,
    val downVoteCount: Int,
    val descriptionEnglish: String?,
    val descriptionVietnamese: String?,
    @SerializedName("userId")
    val userId: ArrayList<String>?,
    val learningStatisticsId: List<String>?,
    val topicInFolderId: List<String>?,
    val vocabularyId: List<String>?,
    val ownerId: User?,
    var chosen: Boolean = false
): Parcelable {
    override fun equals(other: Any?): Boolean {
        if(other is Topic){
            return this.id == other.id
        }

        if (javaClass != other?.javaClass) return false

        return super.equals(other)
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.createStringArrayList(),
        parcel.createStringArrayList(),
        parcel.createStringArrayList(),
        parcel.readParcelable(User::class.java.classLoader),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(topicNameEnglish)
        parcel.writeString(topicNameVietnamese)
        parcel.writeInt(vocabularyCount)
        parcel.writeByte(if (isPublic) 1 else 0)
        parcel.writeInt(upVoteCount)
        parcel.writeInt(downVoteCount)
        parcel.writeString(descriptionEnglish)
        parcel.writeString(descriptionVietnamese)
        parcel.writeStringList(userId)
        parcel.writeStringList(learningStatisticsId)
        parcel.writeStringList(topicInFolderId)
        parcel.writeStringList(vocabularyId)
        parcel.writeParcelable(ownerId, flags)
        parcel.writeByte(if (chosen) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Topic> {
        override fun createFromParcel(parcel: Parcel): Topic {
            return Topic(parcel)
        }

        override fun newArray(size: Int): Array<Topic?> {
            return arrayOfNulls(size)
        }
    }
}