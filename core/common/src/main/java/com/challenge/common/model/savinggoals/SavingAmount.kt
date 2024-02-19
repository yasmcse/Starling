package com.challenge.common.model.savinggoals

data class SavingAmount(
    val amount: SavingGoalAmount
)
data class SavingGoalAmount(
    val currency:String,
    val minorUnits:Long
)