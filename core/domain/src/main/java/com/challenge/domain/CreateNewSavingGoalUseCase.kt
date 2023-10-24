package com.challenge.domain

import com.challenge.di.NetworkResult
import com.challenge.model.NewSavingGoal
import com.challenge.model.NewSavingGoalResponse
import com.challenge.repository.UserAccountRepository
import com.challenge.repository.savinggoals.SavingGoalsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateNewSavingGoalUseCase @Inject constructor(
    private val savingGoalsRepository: SavingGoalsRepository,
    private val userAccountRepository: UserAccountRepository
) {
    suspend fun createNewSavingGoal(newSavingGoal: NewSavingGoal): Flow<NetworkResult<NewSavingGoalResponse>> =
        savingGoalsRepository.createNewSavingGoal(userAccountRepository.getAccountUid(),newSavingGoal)
}