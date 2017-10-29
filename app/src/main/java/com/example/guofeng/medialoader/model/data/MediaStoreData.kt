package com.example.guofeng.medialoader.model.data

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

/**
 * Created by guofeng on 2017/10/16.
 */
data class MediaStoreData(
        val rowId: Long,
        val uri: Uri,
        val mimeType: String,
        val dateModified: Long,
        val orientation: Int,
        val mediaType: MediaType,
        val dateTaken: Long
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readParcelable(Uri::class.java.classLoader),
            parcel.readString(),
            parcel.readLong(),
            parcel.readInt(),
            MediaType.valueOf(parcel.readString()),
            parcel.readLong())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(rowId)
        parcel.writeParcelable(uri, flags)
        parcel.writeString(mimeType)
        parcel.writeLong(dateModified)
        parcel.writeInt(orientation)
        parcel.writeString(mediaType.name)
        parcel.writeLong(dateTaken)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MediaStoreData> {
        override fun createFromParcel(parcel: Parcel): MediaStoreData {
            return MediaStoreData(parcel)
        }

        override fun newArray(size: Int): Array<MediaStoreData?> {
            return arrayOfNulls(size)
        }
    }
}

enum class MediaType {
    VIDEO, IMAGE
}