package com.tdtu.finalproject.model.quizzes

import android.os.Parcel
import android.os.Parcelable
import com.tdtu.finalproject.model.vocabulary.Vocabulary
import java.util.ArrayList

data class Quiz(
    var correctAnswer: Vocabulary?,
    var wrongAnswer: ArrayList<Vocabulary>?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Vocabulary::class.java.classLoader),
        parcel.createTypedArrayList(Vocabulary)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(correctAnswer, flags)
        parcel.writeTypedList(wrongAnswer)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Quiz> {
        override fun createFromParcel(parcel: Parcel): Quiz {
            return Quiz(parcel)
        }

        override fun newArray(size: Int): Array<Quiz?> {
            return arrayOfNulls(size)
        }
    }

}