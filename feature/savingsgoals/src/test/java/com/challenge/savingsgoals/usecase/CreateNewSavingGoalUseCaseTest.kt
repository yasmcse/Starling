package com.challenge.savingsgoals.usecase

import com.challenge.common.UserAccountRepository
import com.challenge.common.model.NetworkResult
import com.challenge.common.model.newsavingdto.NewSavingGoal
import com.challenge.common.model.savinggoaldto.SavingTarget
import com.challenge.model.savinggoal.NewSavingGoalResponseDomain
import com.challenge.repositories.SavingsGoalsRepository
import com.challenge.savingsgoals.domain.usecase.CreateNewSavingGoalUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateNewSavingGoalUseCaseTest {

    private val savingGoalsRepository: SavingsGoalsRepository = mockk()
    private val userAccountRepository: UserAccountRepository = mockk()
    private val createNewSavingGoalUseCase =
        CreateNewSavingGoalUseCase(savingGoalsRepository, userAccountRepository)

    @Test
    fun `invoke should return success when creating a new saving goal`() = runTest {
        // Given
        val accountUid = "account-uid"
        val newSavingGoal = NewSavingGoal("Goal 1", "USD", SavingTarget("USD", 1000L), "cc")

        // Create a mock response type for the saving goal creation
        val expectedResponse = NewSavingGoalResponseDomain("1", "success")
        val expectedResult = NetworkResult.Success(expectedResponse)

        // Mocking the necessary repository responses
        coEvery { userAccountRepository.getAccountUid() } returns accountUid
        coEvery {
            savingGoalsRepository.createNewSavingGoal(
                accountUid,
                newSavingGoal
            )
        } returns expectedResult

        // When
        val result = createNewSavingGoalUseCase.invoke(newSavingGoal)

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(expectedResult, result)
        coVerify { userAccountRepository.getAccountUid() }
        coVerify { savingGoalsRepository.createNewSavingGoal(accountUid, newSavingGoal) }
    }

    @Test
    fun `invoke should return error when account UID is null`() = runTest {
        // Given
        val newSavingGoal = NewSavingGoal("Goal 1", "USD", SavingTarget("USD", 10L), "")

        // Mocking the necessary repository responses
        coEvery { userAccountRepository.getAccountUid() } returns null
        coEvery { savingGoalsRepository.createNewSavingGoal(null, newSavingGoal) } returns NetworkResult.Error(null, "User account ID is required")
        // When
        val result = createNewSavingGoalUseCase.invoke(newSavingGoal)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals(
            "User account ID is required",
            (result as NetworkResult.Error).message
        )
        coVerify { userAccountRepository.getAccountUid() }
    }

    @Test
    fun `invoke should return error when creating new saving goal fails`() = runTest {
        // Given
        val accountUid = "account-uid"
        val newSavingGoal = NewSavingGoal("Goal 1", "USD", SavingTarget("USD", 10L), "")
        val errorMessage = "Failed to create saving goal"
        val expectedError = NetworkResult.Error<NewSavingGoalResponseDomain>(null, errorMessage)

        // Mocking the necessary repository responses
        coEvery { userAccountRepository.getAccountUid() } returns accountUid
        coEvery { savingGoalsRepository.createNewSavingGoal(accountUid, newSavingGoal) } returns expectedError

        // When
        val result = createNewSavingGoalUseCase.invoke(newSavingGoal)

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals(expectedError, result)
        coVerify { userAccountRepository.getAccountUid() }
        coVerify { savingGoalsRepository.createNewSavingGoal(accountUid, newSavingGoal) }
    }
}