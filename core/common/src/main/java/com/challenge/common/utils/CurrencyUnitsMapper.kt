package com.challenge.common.utils

import androidx.annotation.VisibleForTesting
import com.challenge.model.Amount
import com.challenge.model.SavingsGoal
import com.challenge.model.Transaction
import java.lang.String
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.Double
import kotlin.Long
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.with

class CurrencyUnitsMapper @Inject constructor() {

    @VisibleForTesting
    fun convertMinorUnitToMajorUnit(minorUnitsList: List<Transaction>): List<Transaction> {
        val convertedList: MutableList<Transaction> = mutableListOf()

        for (transaction in minorUnitsList) {
            with(transaction) {
                convertedList.add(
                    Transaction(
                        feedItemUid, categoryUid,
                        Amount(
                            amount.currency,
                            amount.minorUnits,
                            convertMinorUnitsToMajorUnits(amount.minorUnits)
                        ),
                        Amount(
                            amount.currency,
                            amount.minorUnits,
                            convertMinorUnitsToMajorUnits(amount.minorUnits)
                        ),
                        direction
                    )
                )
            }
        }
        return convertedList
    }

    @VisibleForTesting
    fun convertSavingGoalUnits(minorUnitsList: List<SavingsGoal>): List<SavingsGoal> {
        val convertedList: MutableList<SavingsGoal> = mutableListOf()

        for (savingGoal in minorUnitsList) {
            with(savingGoal) {
                convertedList.add(
                    SavingsGoal(
                        savingsGoalUid,
                        name,
                        Amount(
                            savingGoal.target.currency,
                            savingGoal.target.minorUnits,
                            convertMinorUnitsToMajorUnits(savingGoal.target.minorUnits)
                        ),
                        Amount(
                            savingGoal.totalSaved.currency,
                            savingGoal.totalSaved.minorUnits,
                            convertMinorUnitsToMajorUnits(savingGoal.totalSaved.minorUnits)
                        ),
                        savingGoal.savedPercentage,
                        savingGoal.state
                    )
                )
            }
        }
        return convertedList
    }


    @VisibleForTesting
    fun convertMinorUnitsToMajorUnits(minorUnits: Long): Double {
        val denominator = 100.00
        val num = BigDecimal(minorUnits.div(denominator))
        return num.toDouble()
    }

    @VisibleForTesting
    fun convertMajorUnitsToMinorUnits(majorUnits: Double):Double{
        val denominator = 100.00
        val num = BigDecimal(majorUnits.times(denominator))
        return roundToNearestPound(num.toDouble())
    }

    @VisibleForTesting
    fun roundToNearestPound(value: Double): Double {
        val floorValue = floor(value)
        val ceilValue = ceil(value)

        val floorDifference = value - floorValue
        val ceilDifference = ceilValue - value

        return if (floorDifference < ceilDifference) {
            floorValue
        } else {
            ceilValue
        }
    }

    @VisibleForTesting
    fun sumUpFractionPartMajorUnit(list: List<Transaction>): Double {
        var sum = 0.0
        for (transaction in list) {
            val bigDecimal = BigDecimal(String.valueOf(transaction.amount.majorUnit))
            val intValue = bigDecimal.toInt()
            sum+= (bigDecimal.subtract(
                BigDecimal(intValue)
            )).toDouble()
        }
        return sum
    }
}