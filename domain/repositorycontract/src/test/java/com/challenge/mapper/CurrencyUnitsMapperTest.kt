package com.challenge.mapper

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.challenge.common.utils.MainCoroutineRule
import com.challenge.common.model.Amount
import com.challenge.util.CurrencyUnitsMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CurrencyUnitsMapperTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var sut: CurrencyUnitsMapper


    @Before
    fun setUp() {
        sut = CurrencyUnitsMapper()
    }

    @Test
    fun `Convert minor units to major units`() {
        // Transactions list in minor units test data
        val amount = Amount("GBP", 1234L, 0.0)
        val transactionDomain = com.challenge.mapper.transaction.model.TransactionDomain(
            "FeedItemUid",
            "CategoryUid",
            amount,
            amount,
            "IN"
        )
        val transactionList = listOf(transactionDomain)

        // Transactions list in major units computed
        val amount2 = Amount("GBP", 1234L, 12.34)
        val transactionDomain2 = com.challenge.mapper.transaction.model.TransactionDomain(
            "FeedItemUid",
            "CategoryUid",
            amount2,
            amount2,
            "IN"
        )
        val transactionList2 = listOf(transactionDomain2)

       assertEquals(transactionList2, sut.convertMinorUnitToMajorUnit(transactionList))
    }

    @Test
    fun `convert saving goals minor units to major units`() {
        // Saving Goals test data
        val targetAmount = Amount("GBP", 1234L, 0.0)
        val totalSaved = Amount("GBP", 1234L, 0.0)
        val savingGoal = com.challenge.mapper.savinggoal.model.SavingsGoalDomain(
            "savingsGoalUid",
            "TRip to france",
            targetAmount,
            totalSaved
        )
        val savingGoals = listOf(savingGoal)

        // Expected Goals
        // Saving Goals test data
        val expectedTargetAmount = Amount("GBP", 1234L, 12.34)
        val expectedTotalSaved = Amount("GBP", 1234L, 12.34)
        val expectedSavingGoal = com.challenge.mapper.savinggoal.model.SavingsGoalDomain(
            "savingsGoalUid",
            "TRip to france",
            expectedTargetAmount,
            expectedTotalSaved
        )
        val expectedSavingGoals = listOf(expectedSavingGoal)

        assertEquals(expectedSavingGoals, sut.convertSavingGoalUnits(savingGoals))
    }

    @Test
    fun `convert minor unit to major unit`() {
        val minorUnit = 1234L
        val expectedMajorUnit = 12.34

        assertEquals(expectedMajorUnit, sut.convertMinorUnitsToMajorUnits(minorUnit),0.0)
    }

    @Test
    fun `convert major unit to minor unit`() {
        val expectedMinorUnit = 12.34
        val majorUnit = 1234L

        assertEquals(expectedMinorUnit, sut.convertMinorUnitsToMajorUnits(majorUnit),0.0)
    }

    @Test
    fun `round to nearest pound`() {
        assertEquals(1.0, sut.roundToNearestPound(0.96),0.0)
    }

    @Test
    fun `sum up fractional part of major unit`() {
        // Transactions list in minor units test data
        val amount = Amount("GBP", 1234L, 12.34)
        val transactionDomain = com.challenge.mapper.transaction.model.TransactionDomain(
            "FeedItemUid",
            "CategoryUid",
            amount,
            amount,
            "IN"
        )


        // Transactions list in major units computed
        val amount2 = Amount("GBP", 1234L, 12.34)
        val transactionDomain2 = com.challenge.mapper.transaction.model.TransactionDomain(
            "FeedItemUid",
            "CategoryUid",
            amount2,
            amount2,
            "IN"
        )


        val list = listOf(transactionDomain,transactionDomain2)

        assertEquals(0.68, sut.sumUpFractionPartMajorUnit(list),0.0)
    }

}