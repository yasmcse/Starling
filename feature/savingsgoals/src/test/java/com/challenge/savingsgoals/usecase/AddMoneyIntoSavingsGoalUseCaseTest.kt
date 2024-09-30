package com.challenge.savingsgoals.usecase

import com.challenge.common.UserAccountRepository
import com.challenge.common.model.Amount
import com.challenge.common.model.NetworkResult
import com.challenge.common.model.savinggoals.SavingAmount
import com.challenge.common.model.savinggoals.SavingGoalAmount
import com.challenge.model.savinggoal.SavingsGoalDomain
import com.challenge.model.transaction.TransferDomain
import com.challenge.repositories.SavingsGoalsRepository
import com.challenge.savingsgoals.domain.usecase.AddMoneyIntoSavingsGoalUseCase
import com.challenge.savingsgoals.mapper.CurrencyAndAmountMapper
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class AddMoneyIntoSavingsGoalUseCaseTest {

    private lateinit var userAccountRepository: UserAccountRepository
    private lateinit var currencyAndAmountMapper: CurrencyAndAmountMapper
    private lateinit var savingsGoalsRepository: SavingsGoalsRepository
    private lateinit var addMoneyIntoSavingsGoalUseCase: AddMoneyIntoSavingsGoalUseCase

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        userAccountRepository = mockk()
        currencyAndAmountMapper = mockk()
        savingsGoalsRepository = mockk()
        addMoneyIntoSavingsGoalUseCase = AddMoneyIntoSavingsGoalUseCase(
            userAccountRepository,
            currencyAndAmountMapper,
            savingsGoalsRepository
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun reset(){
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke should return success when adding money to savings goal`() = runTest {
        // Given
        val roundUpSum = 100L
        val savingsGoalDomain = SavingsGoalDomain("1", "Goal 1", Amount("USD", 1000L, 10.0), Amount("USD", 1000L, 10.0))
        val accountUid = "account-uid"
        val mappedAmount = SavingAmount(SavingGoalAmount("USD", 100L))
        val transactionUid = UUID.randomUUID().toString()
        val transferDomain = TransferDomain(transactionUid, true)

        // Mocking the necessary repository responses
        coEvery { userAccountRepository.getAccountUid() } returns accountUid
        coEvery { currencyAndAmountMapper.map("USD", roundUpSum) } returns mappedAmount
        coEvery {
            savingsGoalsRepository.addMoneyIntoSavingGoal(
                accountUid,
                savingsGoalDomain.savingsGoalUid,
                any(),
                mappedAmount
            )
        } returns NetworkResult.Success(transferDomain)

        // When
        val result = addMoneyIntoSavingsGoalUseCase.invoke(roundUpSum, savingsGoalDomain)

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(transferDomain, (result as NetworkResult.Success).data)
        coVerify { userAccountRepository.getAccountUid() }
        coVerify { currencyAndAmountMapper.map("USD", roundUpSum) }
        coVerify { savingsGoalsRepository.addMoneyIntoSavingGoal(accountUid, savingsGoalDomain.savingsGoalUid, any(), mappedAmount) }
    }

    @Test
    fun `invoke should return error when account UID is not found`() = runTest {
        // Given
        val roundUpSum = 100L
        val savingsGoalDomain = SavingsGoalDomain("1", "Goal 1", Amount("USD", 1000L, 10.0),Amount("USD", 1000L, 10.0))

        // Mocking the userAccountRepository to return null
        coEvery { userAccountRepository.getAccountUid() } returns null

        // When
        val result = addMoneyIntoSavingsGoalUseCase(roundUpSum, savingsGoalDomain)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("Account UID not found", (result as NetworkResult.Error).message)
        coVerify { userAccountRepository.getAccountUid() }
    }
}