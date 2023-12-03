package com.tdtu.finalproject.model.user

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val user: User,
    val token: String,
    val error: String
)

data class User(
    @SerializedName("_id")
    val id: String,
    val email: String,
    val username: String,
    val profileImage: String,
    val almaMater: String,
    val isPremiumAccount: Boolean,
    val bookmarkVocabularyId: List<Any?>,
    val vocabularyStatisticId: List<Any?>,
    val folderId: List<Any?>,
    val learningStatisticsId: List<Any?>,
    val topicId: List<Any?>,
    @SerializedName("__v")
    val v: Long,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readBoolean(),
        mutableListOf<Any?>().apply{ parcel.readList(this, Any::class.java.classLoader)},
        mutableListOf<Any?>().apply{ parcel.readList(this, Any::class.java.classLoader)},
        mutableListOf<Any?>().apply{ parcel.readList(this, Any::class.java.classLoader)},
        mutableListOf<Any?>().apply{ parcel.readList(this, Any::class.java.classLoader)},
        mutableListOf<Any?>().apply{ parcel.readList(this, Any::class.java.classLoader)},
        parcel.readLong()
    )

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        p0.writeString(id)
        p0.writeString(email)
        p0.writeString(username)
        p0.writeString(profileImage)
        p0.writeString(almaMater)
        p0.writeBoolean(isPremiumAccount)
        p0.writeList(bookmarkVocabularyId)
        p0.writeList(vocabularyStatisticId)
        p0.writeList(folderId)
        p0.writeList(learningStatisticsId)
        p0.writeList(topicId)
        p0.writeLong(v)
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
