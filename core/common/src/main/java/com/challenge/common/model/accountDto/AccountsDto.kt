package com.challenge.common.model.accountDto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccountsDto(
   @SerializedName("accounts") val accountDtos:List<AccountDto>
):Parcelable