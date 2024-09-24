package com.challenge.mapper.savinggoal.model

import com.challenge.common.model.Amount

data class SavingsGoalDomain(
    val savingsGoalUid: String,
    val name: String,
    val target: Amount,
    val totalSaved: Amount
)
