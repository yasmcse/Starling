package com.challenge.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Amount(
    @SerializedName("currency") val currency: String,
    @SerializedName("minorUnits") val minorUnits: Long,
    val majorUnit:Double
) : Parcelable