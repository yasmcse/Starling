package com.challenge.savingsgoals.mapper

import com.challenge.common.model.newsavingdto.NewSavingGoal
import com.challenge.common.model.savinggoaldto.SavingTarget
import javax.inject.Inject

class NewSavingGoalMapper @Inject constructor() {
    fun map(tripName: String, currency: String, amount: Long) =
        NewSavingGoal(
            tripName,
            currency,
            SavingTarget(currency, amount),
            null
        )
}