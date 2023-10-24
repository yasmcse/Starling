package com.challenge.model

import com.google.gson.annotations.SerializedName

data class Transactions(
    @SerializedName("feedItems") val  feedItems:List<Transaction>
)