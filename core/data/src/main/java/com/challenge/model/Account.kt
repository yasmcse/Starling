package com.challenge.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Account(
    @SerializedName("accountUid") val accountUid: String,
    @SerializedName("accountType") val accountType: String,
    @SerializedName("defaultCategory") val defaultCategory: String,
    @SerializedName("currency") val currency: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("name") val name: String,
) : Parcelable