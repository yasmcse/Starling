package com.challenge.savingsgoals.mapper

import com.challenge.common.model.savinggoals.SavingAmount
import com.challenge.common.model.savinggoals.SavingGoalAmount
import javax.inject.Inject

class CurrencyAndAmountMapper @Inject constructor() {
    fun map(currency: String, minorUnits: Long) = SavingAmount(
        SavingGoalAmount(currency, minorUnits)
    )
}