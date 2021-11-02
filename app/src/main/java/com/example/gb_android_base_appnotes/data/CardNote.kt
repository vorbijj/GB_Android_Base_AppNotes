package com.example.gb_android_base_appnotes.data

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import java.util.*

data class CardNote(var title: String?, var date: Date, var description: String?, var isLike: Boolean) : Parcelable {
    var id: String? = null

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readLong() as Date,
            parcel.readString(),
            parcel.readByte() != 0.toByte()) {
        id = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeByte(if (isLike) 1 else 0)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Creator<CardNote> {
        override fun createFromParcel(parcel: Parcel): CardNote {
            return CardNote(parcel)
        }

        override fun newArray(size: Int): Array<CardNote?> {
            return arrayOfNulls(size)
        }
    }
}