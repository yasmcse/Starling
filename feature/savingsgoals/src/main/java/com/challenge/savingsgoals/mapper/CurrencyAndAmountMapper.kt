package com.challenge.savingsgoals.mapper

import com.challenge.model.SavingAmount
import com.challenge.model.SavingGoalAmount
import javax.inject.Inject

class CurrencyAndAmountMapper @Inject constructor() {

    fun map(currency: String, minorUnits: Long) = SavingAmount(
        SavingGoalAmount(currency, minorUnits)
    )
}