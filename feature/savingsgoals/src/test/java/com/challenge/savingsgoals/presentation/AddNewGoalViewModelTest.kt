package com.challenge.savingsgoals.presentation

import android.text.TextUtils
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.challenge.common.model.NetworkResult
import com.challenge.common.model.newsavingdto.NewSavingGoal
import com.challenge.model.savinggoal.NewSavingGoalResponseDomain
import com.challenge.savingsgoals.domain.usecase.CreateNewSavingGoalUseCase
import com.challenge.savingsgoals.mapper.NewSavingGoalMapper
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddNewGoalViewModelTest {

    // Mocking dependencies
    private val createNewSavingGoalUseCase = mockk<CreateNewSavingGoalUseCase>()
    private val newSavingGoalMapper = mockk<NewSavingGoalMapper>()

    // The ViewModel under test
    private lateinit var viewModel: AddNewGoalViewModel

    // Using a dispatcher for testing
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    val testDispatcher = UnconfinedTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(TextUtils::class)
        viewModel = AddNewGoalViewModel(createNewSavingGoalUseCase, newSavingGoalMapper)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun reset(){
        Dispatchers.resetMain()
    }

    @Test
    fun `postNewSavingGoal should update newGoal with success result`() = runTest {
        // Given
        val tripName = "Hawaii Trip"
        val currency = "USD"
        val amount = 1000L
        val mappedRequest = mockk<NewSavingGoal>()

        val responseDomain = NewSavingGoalResponseDomain(savingsGoalUid = "savingGoalUid", success = "success")
        val successResult = NetworkResult.Success(responseDomain)

        // Mock mapper behavior
        every { newSavingGoalMapper.map(tripName, currency, amount) } returns mappedRequest

        // Mock use case behavior to return success
        coEvery { createNewSavingGoalUseCase.invoke(mappedRequest) } returns successResult

        // When
        viewModel.postNewSavingGoal(tripName, currency, amount)

        // Then
        assertTrue(viewModel.newGoal.value is NetworkResult.Success)
        assertEquals(responseDomain, (viewModel.newGoal.value as NetworkResult.Success).data)

        // Verify the mapper and use case were called
        verify { newSavingGoalMapper.map(tripName, currency, amount) }
        coVerify { createNewSavingGoalUseCase.invoke(mappedRequest) }
    }

    @Test
    fun `postNewSavingGoal should update newGoal with error result`() = runTest {
        // Given
        val tripName = "Hawaii Trip"
        val currency = "USD"
        val amount = 1000L
        val mappedRequest = mockk<NewSavingGoal>()

        val errorResult = NetworkResult.Error<NewSavingGoalResponseDomain>(null,"Failed to create goal")

        // Mock mapper behavior
        every { newSavingGoalMapper.map(tripName, currency, amount) } returns mappedRequest

        // Mock use case behavior to return an error
        coEvery { createNewSavingGoalUseCase.invoke(mappedRequest) } returns errorResult

        // When
        viewModel.postNewSavingGoal(tripName, currency, amount)

        // Then
        assertTrue(viewModel.newGoal.value is NetworkResult.Error)
        assertEquals("Failed to create goal", (viewModel.newGoal.value as NetworkResult.Error).message)

        // Verify the mapper and use case were called
        verify { newSavingGoalMapper.map(tripName, currency, amount) }
        coVerify { createNewSavingGoalUseCase.invoke(mappedRequest) }
    }

    @Test
    fun `isValidInput should return true for valid trip and amount`() {
        // Given
        val trip = "Trip to Paris"
        val amount = "100"

        // Mocking isDigitsOnly to return true
        every { TextUtils.isDigitsOnly(amount) } returns true

        // When
        val result = viewModel.isValidInput(trip, amount)

        // Then
        assertTrue(result)
    }

    @Test
    fun `isValidInput should return false when trip is empty`() {
        // Given
        val trip = ""
        val amount = "100"

        // Mocking isDigitsOnly to return true
        every { TextUtils.isDigitsOnly(amount) } returns true

        // When
        val result = viewModel.isValidInput(trip, amount)

        // Then
        assertFalse(result)
    }

    @Test
    fun `isValidInput should return false when amount is empty`() {
        // Given
        val trip = "Trip to Paris"
        val amount = ""

        // Mocking isDigitsOnly to return false
        every { TextUtils.isDigitsOnly(amount) } returns false

        // When
        val result = viewModel.isValidInput(trip, amount)

        // Then
        assertFalse(result)
    }

    @Test
    fun `isValidInput should return false when amount is not a digit`() {
        // Given
        val trip = "Trip to Paris"
        val amount = "ABC"

        // Mocking isDigitsOnly to return false
        every { TextUtils.isDigitsOnly(amount) } returns false

        // When
        val result = viewModel.isValidInput(trip, amount)

        // Then
        assertFalse(result)
    }

    @Test
    fun `isValidInput should return false when both trip and amount are invalid`() {
        // Given
        val trip = ""
        val amount = "ABC"

        // Mocking isDigitsOnly to return false
        every { TextUtils.isDigitsOnly(amount) } returns false

        // When
        val result = viewModel.isValidInput(trip, amount)

        // Then
        assertFalse(result)
    }
}