package com.tdtu.finalproject.model.folder

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

data class Folder(
    @SerializedName("_id")
    val id: String?,
    val userId: String?,
    val folderNameEnglish: String?,
    val folderNameVietnamese: String?,
    val topicCount: Int,
    val topicInFolderId: ArrayList<String>?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.createStringArrayList()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(userId)
        parcel.writeString(folderNameEnglish)
        parcel.writeString(folderNameVietnamese)
        parcel.writeInt(topicCount)
        parcel.writeStringList(topicInFolderId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Folder> {
        override fun createFromParcel(parcel: Parcel): Folder {
            return Folder(parcel)
        }

        override fun newArray(size: Int): Array<Folder?> {
            return arrayOfNulls(size)
        }
    }
}