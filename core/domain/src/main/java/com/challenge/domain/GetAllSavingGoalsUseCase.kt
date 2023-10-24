package com.challenge.domain

import com.challenge.di.NetworkResult
import com.challenge.model.SavingsGoals
import com.challenge.repository.savinggoals.SavingGoalsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllSavingGoalsUseCase @Inject constructor(
    private val savingGoalsRepository: SavingGoalsRepository
) {
    suspend fun getAllSavings(accountUid:String): Flow<NetworkResult<SavingsGoals>> =
        savingGoalsRepository.getAllSavingGoals(accountUid)
}