package com.challenge.common.model.transactiondto

import android.os.Parcelable
import com.challenge.common.model.Amount
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionDto(
    @SerializedName("feedItemUid") val feedItemUid: String,
    @SerializedName("categoryUid") val categoryUid: String,
    @SerializedName("amount") val amount: Amount,
    @SerializedName("sourceAmount") val sourceAmount: Amount,
    @SerializedName("direction") val direction: String,
) : Parcelable