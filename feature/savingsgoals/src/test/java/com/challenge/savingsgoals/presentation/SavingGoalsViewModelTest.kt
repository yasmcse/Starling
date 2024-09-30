package com.challenge.savingsgoals.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.challenge.common.model.Amount
import com.challenge.common.model.NetworkResult
import com.challenge.model.savinggoal.SavingsGoalDomain
import com.challenge.model.savinggoal.SavingsGoalsDomain
import com.challenge.model.transaction.TransferDomain
import com.challenge.savingsgoals.domain.usecase.AddMoneyIntoSavingsGoalUseCase
import com.challenge.savingsgoals.domain.usecase.FetchSavingGoalsUseCase
import com.challenge.util.CurrencyUnitsMapper
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SavingGoalsViewModelTest {

    // Mock dependencies
    private val addMoneyIntoSavingsGoalUseCase = mockk<AddMoneyIntoSavingsGoalUseCase>()
    private val currencyUnitsMapper = mockk<CurrencyUnitsMapper>()
    private val fetchSavingGoalsUseCase = mockk<FetchSavingGoalsUseCase>()

    // ViewModel under test
    private lateinit var viewModel: SavingGoalsViewModel

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SavingGoalsViewModel(
            addMoneyIntoSavingsGoalUseCase,
            currencyUnitsMapper,
            fetchSavingGoalsUseCase
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `fetchSavingsGoals should update savingsGoalsList with success result`() = runTest {
        // Given
        val savingsGoal1 = SavingsGoalDomain("1", "Goal 1", Amount("pound", 120L, 12.0), Amount("pound", 120L, 12.0))
        val savingsGoal2 = SavingsGoalDomain("2", "Goal 2", Amount("pound", 150L, 15.0), Amount("pound", 150L, 15.0))
        val savingsGoalsDomain = SavingsGoalsDomain(listOf(savingsGoal1, savingsGoal2)) // Wrap in SavingsGoalsDomain
        val successResult = NetworkResult.Success(savingsGoalsDomain)

        // Mock the fetchSavingGoalsUseCase to return the success result
        coEvery { fetchSavingGoalsUseCase.invoke() } returns successResult

        // When
        viewModel.fetchSavingsGoals()

        // Move time forward to allow coroutine execution
        advanceUntilIdle()

        // Then
        val result = viewModel.savingsGoalsList.value
        assertTrue(result is NetworkResult.Success)
        assertEquals(savingsGoalsDomain, (result as NetworkResult.Success).data)

        coVerify { fetchSavingGoalsUseCase.invoke() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `fetchSavingsGoals should update savingsGoalsList with error result`() = runTest {
        // Given
        val errorMessage = "Failed to fetch saving goals"
        val errorResult:NetworkResult<SavingsGoalsDomain> = NetworkResult.Error<SavingsGoalsDomain>(null,errorMessage)

        // Mock the fetchSavingGoalsUseCase to return the error result
        coEvery { fetchSavingGoalsUseCase.invoke() } returns errorResult

        // When
        viewModel.fetchSavingsGoals()

        // Move time forward to allow coroutine execution
        advanceUntilIdle()

        // Then
        val result = viewModel.savingsGoalsList.value
        assertTrue(result is NetworkResult.Error)
        assertEquals(errorMessage, (result as NetworkResult.Error).message)

        coVerify { fetchSavingGoalsUseCase.invoke() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `addMoneyIntoSavingGoals should call use case`() = runTest {
        // Given
        val roundUpSum = 1000L
        val savingsGoalDomain = SavingsGoalDomain("1", "Goal 1", Amount("pound", 120L, 12.0),Amount("pound", 120L, 12.0))
        val transferDomain = TransferDomain("transfer-123",true)
        val successResult = NetworkResult.Success(transferDomain)

        // Mock the addMoneyIntoSavingsGoalUseCase to return success result
        coEvery { addMoneyIntoSavingsGoalUseCase.invoke(roundUpSum, savingsGoalDomain) } returns successResult

        // When
        viewModel.addMoneyIntoSavingGoals(roundUpSum, savingsGoalDomain)

        // Move time forward to allow coroutine execution
        advanceUntilIdle()

        // Then
        coVerify { addMoneyIntoSavingsGoalUseCase.invoke(roundUpSum, savingsGoalDomain) }
    }
}