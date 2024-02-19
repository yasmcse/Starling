package com.challenge.common.model.savinggoaldto

import com.challenge.common.model.Amount


data class SavingsGoalDto(
    val savingsGoalUid:String,
    val name:String,
    val target: Amount,
    val totalSaved: Amount,
    val savedPercentage:Int,
    val state: SavingGoalsStatus
)
