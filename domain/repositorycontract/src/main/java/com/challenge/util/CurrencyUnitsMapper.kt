package com.challenge.util

import com.challenge.common.model.Amount
import com.challenge.mapper.transaction.model.TransactionDomain
import java.lang.String
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.Double
import kotlin.Long
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.with

class CurrencyUnitsMapper @Inject constructor() {
    fun convertMinorUnitToMajorUnit(minorUnitsList: List<com.challenge.mapper.transaction.model.TransactionDomain>): List<com.challenge.mapper.transaction.model.TransactionDomain> {
        val convertedList: MutableList<TransactionDomain> = mutableListOf()

        for (transaction in minorUnitsList) {
            with(transaction) {
                convertedList.add(
                    com.challenge.mapper.transaction.model.TransactionDomain(
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

    fun convertSavingGoalUnits(minorUnitsList: List<com.challenge.mapper.savinggoal.model.SavingsGoalDomain>): List<com.challenge.mapper.savinggoal.model.SavingsGoalDomain> {
        val convertedList: MutableList<com.challenge.mapper.savinggoal.model.SavingsGoalDomain> = mutableListOf()

        for (savingGoal in minorUnitsList) {
            with(savingGoal) {
                convertedList.add(
                    com.challenge.mapper.savinggoal.model.SavingsGoalDomain(
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
                        )
                    )
                )
            }
        }
        return convertedList
    }


    fun convertMinorUnitsToMajorUnits(minorUnits: Long): Double {
        val denominator = 100.00
        val num = BigDecimal(minorUnits.div(denominator))
        return num.toDouble()
    }

    fun convertMajorUnitsToMinorUnits(majorUnits: Double): Double {
        val denominator = 100.00
        val num = BigDecimal(majorUnits.times(denominator))
        return roundToNearestPound(num.toDouble())
    }

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

    fun sumUpFractionPartMajorUnit(list: List<com.challenge.mapper.transaction.model.TransactionDomain>): Double {
        var sum = 0.0
        for (transaction in list) {
            val bigDecimal = BigDecimal(String.valueOf(transaction.amount.majorUnit))
            val intValue = bigDecimal.toInt()
            sum += (bigDecimal.subtract(
                BigDecimal(intValue)
            )).toDouble()
        }
        return sum
    }
}