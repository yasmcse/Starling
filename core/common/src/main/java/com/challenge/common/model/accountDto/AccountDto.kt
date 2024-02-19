package com.challenge.common.model.accountDto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccountDto(
    @SerializedName("accountUid") val accountUid: String,
    @SerializedName("accountType") val accountType: String,
    @SerializedName("defaultCategory") val defaultCategory: String,
    @SerializedName("currency") val currency: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("name") val name: String,
) : Parcelable