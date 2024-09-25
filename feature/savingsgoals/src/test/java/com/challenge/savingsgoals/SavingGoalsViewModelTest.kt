package com.challenge.savingsgoals

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.challenge.util.CurrencyUnitsMapper
import com.challenge.common.utils.MainCoroutineRule
import com.challenge.starlingbank.networklayer.model.NetworkResult
import com.challenge.common.UserAccountRepository
import com.challenge.common.model.Amount
import com.challenge.model.savinggoal.SavingsGoalDomain
import com.challenge.model.savinggoal.SavingsGoalsDomain
import com.challenge.repositorycontract.SavingsGoalsRepository
import com.challenge.savingsgoals.mapper.CurrencyAndAmountMapper
import com.challenge.savingsgoals.presentation.SavingGoalsViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(JUnit4::class)
class SavingGoalsViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mockSavingGoalRepository = mockk<SavingsGoalsRepository>(relaxed = true)
    private val mockUserAccountRepository = mockk<UserAccountRepository>(relaxed = true)
    private val mockCurrencyUnitsMapper = mockk<CurrencyUnitsMapper>(relaxed = true)
    private val mockCurrencyAndAmountMapper = mockk<CurrencyAndAmountMapper>(relaxed = true)

    private lateinit var sut: SavingGoalsViewModel

    @Before
    fun setUp() {
        sut = SavingGoalsViewModel(
            mockSavingGoalRepository,
            mockUserAccountRepository,
            mockCurrencyUnitsMapper,
            mockCurrencyAndAmountMapper
        )
    }

    @Test
    fun `Given fetchSavingsGoals is called then returns the list of saving goals`() =
        runTest {
            // Account repository test data
            val accountRepository = UserAccountRepository()
            with(accountRepository) {
                setAccountId("accountUid")
                setCategoryUid("defaultCategory")
            }
            // Saving goal test data
            val savingsGoalDomain = SavingsGoalDomain(
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
                )
            )

            val savings = SavingsGoalsDomain(listOf(savingsGoalDomain))
            val networkResultSavingGoals: com.challenge.starlingbank.networklayer.model.NetworkResult<SavingsGoalsDomain> =
                com.challenge.starlingbank.networklayer.model.NetworkResult.Success(savings)


            val flowSavingGoals = flow {
                emit(networkResultSavingGoals)
            }

            every { mockUserAccountRepository.getAccountUid() } returns accountRepository.getAccountUid()
            every { mockUserAccountRepository.getCategoryUid() } returns accountRepository.getCategoryUid()
            coEvery { mockSavingGoalRepository.getAllSavingGoals(accountRepository.getAccountUid()!!) } returns flowSavingGoals

            var expectedResponse: SavingsGoalsDomain? = null
            flowSavingGoals.collect {
                expectedResponse = it.data
            }

            sut.fetchSavingsGoals()
            assertEquals(expectedResponse?.list, sut.savingsGoalsList.value.data?.list)
        }

    @Test
    fun `Given fetchSavingsGoals is called then returns error`() =
        runTest {
            // Account repository test data
            val accountRepository = UserAccountRepository()
            with(accountRepository) {
                setAccountId("accountUid")
                setCategoryUid("defaultCategory")
            }
            // Saving goal test data

            val networkResultSavingGoals: com.challenge.starlingbank.networklayer.model.NetworkResult<SavingsGoalsDomain> =
                com.challenge.starlingbank.networklayer.model.NetworkResult.Error(401, "Token expired")

            val flowSavingGoals = flow {
                emit(networkResultSavingGoals)
            }

            every { mockUserAccountRepository.getAccountUid() } returns accountRepository.getAccountUid()
            every { mockUserAccountRepository.getCategoryUid() } returns accountRepository.getCategoryUid()
            coEvery { mockSavingGoalRepository.getAllSavingGoals(accountRepository.getAccountUid()!!) } returns flowSavingGoals

            var expectedResponse: SavingsGoalsDomain? = null
            flowSavingGoals.collect {
                expectedResponse = it.data
            }

            sut.fetchSavingsGoals()
            assertEquals(expectedResponse?.list, sut.savingsGoalsList.value.data?.list)
        }
    @Test
    fun `Given a list of saving goals is passed to getCurrencyInReadable returns the list of goals in majorUnits`() {
        val savingsGoalsDomain = SavingsGoalDomain(
            "savingsGoalUid", "name",
            Amount("GBP", 1234, 0.0), Amount("GBP", 1234, 0.0)
        )
        val savingsGoalsWithMajorUnitsConvertedDomain = SavingsGoalDomain(
            "savingsGoalUid", "name",
            Amount("GBP", 1234, 12.34), Amount("GBP", 1234, 12.34)
        )

        val savingsGoal = listOf(savingsGoalsDomain)
        val convertedListSavingGoals = listOf(savingsGoalsWithMajorUnitsConvertedDomain)

        every { mockCurrencyUnitsMapper.convertSavingGoalUnits(savingsGoal) } returns convertedListSavingGoals
        assertEquals(convertedListSavingGoals, sut.getCurrencyInReadable(savingsGoal))
    }
}