package com.challenge.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaction(
    @SerializedName("feedItemUid") val feedItemUid: String,
    @SerializedName("categoryUid") val categoryUid: String,
    @SerializedName("amount") val amount: Amount,
    @SerializedName("sourceAmount") val sourceAmount: Amount,
    @SerializedName("direction") val direction: String,
) : Parcelable