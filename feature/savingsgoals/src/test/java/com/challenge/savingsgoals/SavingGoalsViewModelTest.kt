package com.challenge.savingsgoals

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.challenge.common.utils.CurrencyUnitsMapper
import com.challenge.common.utils.MainCoroutineRule
import com.challenge.di.NetworkResult
import com.challenge.model.Amount
import com.challenge.model.SavingAmount
import com.challenge.model.SavingGoalAmount
import com.challenge.model.SavingGoalTransferResponse
import com.challenge.model.SavingGoalsStatus
import com.challenge.model.SavingsGoal
import com.challenge.model.SavingsGoals
import com.challenge.model.TransferResponse
import com.challenge.repository.UserAccountRepository
import com.challenge.repository.savinggoals.SavingGoalsRepository
import com.challenge.savingsgoals.mapper.CurrencyAndAmountMapper
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.times
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.hamcrest.core.Every
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock
import java.util.UUID


@RunWith(JUnit4::class)
class SavingGoalsViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mockSavingGoalRepository = mockk<SavingGoalsRepository>(relaxed = true)
    private val userAccount = UserAccountRepository()
    private val mockCurrencyUnitsMapper = mockk<CurrencyUnitsMapper>(relaxed = true)
    private val mockCurrencyAndAmountMapper = mockk<CurrencyAndAmountMapper>(relaxed = true)

    lateinit var sut: SavingGoalsViewModel

    @Before
    fun setUp() {
        sut = SavingGoalsViewModel(
            mockSavingGoalRepository,
            userAccount,
            mockCurrencyUnitsMapper,
            mockCurrencyAndAmountMapper
        )
    }

    @Test
    fun `Given fetchSavingsGoals is called then returns the list of saving goals`() {

        runTest {
            // Account repository test data

            with(userAccount) {
                setAccountId("accountUid")
                setCategoryUid("defaultCategory")
            }
            // Saving goal test data
            val savingsGoal = SavingsGoal(
                "savingsGoalsUid",
                "name",
                Amount(
                    "GBP",
                    1234,
                    0.0
                ),
                Amount(
                    "GBP",
                    1234,
                    0.0
                ),
                2,
                SavingGoalsStatus.ACTIVE
            )

            val savings = SavingsGoals(listOf(savingsGoal))
            val networkResultSavingGoals: NetworkResult<SavingsGoals> =
                NetworkResult.Success(savings)


            val flowSavingGoals = flow {
                emit(networkResultSavingGoals)
            }

            coEvery { mockSavingGoalRepository.getAllSavingGoals(userAccount.getAccountUid()!!) } returns flowSavingGoals

            var response: SavingsGoals? = null
            flowSavingGoals.collect {
                response = it.data
            }


            sut.fetchSavingsGoals()

            assertEquals(response?.savingsGoals, sut.savingsGoalsList.value.data?.savingsGoals)
        }
    }



    @Test
    fun `Given a list of saving goals is passed to getCurrencyInReadable returns the list of goals in majorUnits`() {
        val savingsGoals = SavingsGoal(
            "savingsGoalUid", "name",
            Amount("GBP", 1234, 0.0), Amount("GBP", 1234, 0.0), 2, SavingGoalsStatus.ACTIVE
        )
        val savingsGoalsWithMajorUnitsConverted = SavingsGoal(
            "savingsGoalUid", "name",
            Amount("GBP", 1234, 12.34), Amount("GBP", 1234, 12.34), 2, SavingGoalsStatus.ACTIVE
        )

        val savingsGoal = listOf(savingsGoals)
        val convertedListSavingGoals = listOf(savingsGoalsWithMajorUnitsConverted)

        every { mockCurrencyUnitsMapper.convertSavingGoalUnits(savingsGoal) } returns convertedListSavingGoals


        assertEquals(convertedListSavingGoals, sut.getCurrencyInReadable(savingsGoal))
    }
}