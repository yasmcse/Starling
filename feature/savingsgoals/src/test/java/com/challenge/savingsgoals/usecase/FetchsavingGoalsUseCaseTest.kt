package com.challenge.savingsgoals.usecase

import com.challenge.common.UserAccountRepository
import com.challenge.common.model.Amount
import com.challenge.common.model.NetworkResult
import com.challenge.model.savinggoal.SavingsGoalDomain
import com.challenge.model.savinggoal.SavingsGoalsDomain
import com.challenge.repositories.SavingsGoalsRepository
import com.challenge.savingsgoals.domain.usecase.FetchSavingGoalsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FetchSavingGoalsUseCaseTest {

    private val savingGoalsRepository: SavingsGoalsRepository = mockk()
    private val userAccountRepository: UserAccountRepository = mockk()
    private val fetchSavingGoalsUseCase = FetchSavingGoalsUseCase(savingGoalsRepository, userAccountRepository)

    @Test
    fun `invoke should return success when fetching saving goals`() = runBlocking {
        // Given
        val accountUid = "account-uid"
        val savingsGoalsDomain = SavingsGoalsDomain(listOf(SavingsGoalDomain("savingGoalUid","name",
            Amount("pound", 120L, 12.0), Amount("pound", 120L, 12.0)
        )))
        val expectedResult = NetworkResult.Success(savingsGoalsDomain)

        // Mocking the necessary repository responses
        coEvery { userAccountRepository.getAccountUid() } returns accountUid
        coEvery { savingGoalsRepository.getAllSavingGoals(accountUid) } returns expectedResult

        // When
        val result = fetchSavingGoalsUseCase.invoke()

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(expectedResult, result)
        coVerify { userAccountRepository.getAccountUid() }
        coVerify { savingGoalsRepository.getAllSavingGoals(accountUid) }
    }

    @Test
    fun `invoke should return error when account UID is null`() = runTest {
        // Given
        val expectedError = NetworkResult.Error<Int>(null, "Account UID not found")

        // Mocking the necessary repository responses
        coEvery { userAccountRepository.getAccountUid() } returns null

        // When
        val result = fetchSavingGoalsUseCase.invoke()

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals(expectedError.message, (result as NetworkResult.Error).message)
        coVerify { userAccountRepository.getAccountUid() }
        coVerify(exactly = 0) { savingGoalsRepository.getAllSavingGoals(any()) }
    }

    @Test
    fun `invoke should return error when an exception occurs`() = runBlocking {
        // Given
        val accountUid = "account-uid"

        // Mocking the necessary repository responses
        coEvery { userAccountRepository.getAccountUid() } returns accountUid
        coEvery { savingGoalsRepository.getAllSavingGoals(accountUid) } throws Exception("Database error")

        // When
        val result = fetchSavingGoalsUseCase.invoke()

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("Something went wrong", (result as NetworkResult.Error).message)
        coVerify { userAccountRepository.getAccountUid() }
        coVerify { savingGoalsRepository.getAllSavingGoals(accountUid) }
    }
}