package com.challenge.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Saving(
    val savingsGoalUid:String,
    val name:String,
    val target:SavingTarget,
    val totalSaved:TotalSaved,
    val savedPercentage:Int,
    val status:SavingGoalsStatus
):Parcelable

@Parcelize
data class SavingTarget(
    val currency:String,
    val minorUnits:Long
):Parcelable

@Parcelize
data class TotalSaved(
    val currency:String,
    val minorUnits:Long
):Parcelable

enum class SavingGoalsStatus{
    ACTIVE,
    INACTIVE
}