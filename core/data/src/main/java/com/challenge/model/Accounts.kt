package com.challenge.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Accounts(
   @SerializedName("accounts") val accounts:List<Account>
):Parcelable