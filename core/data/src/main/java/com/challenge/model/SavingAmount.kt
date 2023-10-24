package com.challenge.model

data class SavingAmount(
    val amount:SavingGoalAmount
)
data class SavingGoalAmount(
    val currency:String,
    val minorUnits:Long
)