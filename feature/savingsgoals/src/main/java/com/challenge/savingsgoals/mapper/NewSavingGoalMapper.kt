package com.challenge.savingsgoals.mapper

import com.challenge.model.NewSavingGoal
import com.challenge.model.SavingTarget
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