package com.challenge.model


data class SavingsGoal(
    val savingsGoalUid:String,
    val name:String,
    val target:Amount,
    val totalSaved: Amount,
    val savedPercentage:Int,
    val state:SavingGoalsStatus
)
