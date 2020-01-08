package com.alfara.kotlinmvp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Post (
    @SerializedName("id") var id : Int?= null,
    @SerializedName("title") var title : String?= null,
    @SerializedName("content") var content : String?= null
) : Parcelable