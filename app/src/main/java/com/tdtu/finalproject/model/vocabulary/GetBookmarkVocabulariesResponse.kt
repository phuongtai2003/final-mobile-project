package com.tdtu.finalproject.model.vocabulary

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class GetBookmarkVocabulariesResponse(
    @SerializedName("bookmarkVocab")
    val bookmarkVocabs: List<Vocabulary>
)

data class BookmarkedVocabularies(
    @SerializedName("_id")
    val id: String?,
    val userId: String?,
    @SerializedName("vocabularyId")
    val vocabulary: Vocabulary?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Vocabulary::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(userId)
        parcel.writeParcelable(vocabulary, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BookmarkedVocabularies> {
        override fun createFromParcel(parcel: Parcel): BookmarkedVocabularies {
            return BookmarkedVocabularies(parcel)
        }

        override fun newArray(size: Int): Array<BookmarkedVocabularies?> {
            return arrayOfNulls(size)
        }
    }

}
