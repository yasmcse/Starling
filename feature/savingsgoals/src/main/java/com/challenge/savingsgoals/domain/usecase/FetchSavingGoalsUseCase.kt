package com.challenge.savingsgoals.domain.usecase

import com.challenge.common.UserAccountRepository
import com.challenge.common.model.NetworkResult
import com.challenge.mapper.savinggoal.model.SavingsGoalsDomain
import com.challenge.repositorycontract.SavingsGoalsRepository
import javax.inject.Inject

class FetchSavingGoalsUseCase @Inject constructor(
    private val savingGoalsRepository: SavingsGoalsRepository,
    private val userAccountRepository: UserAccountRepository,
) {

    suspend operator fun invoke(): NetworkResult<SavingsGoalsDomain> {
        return try {
            val accountUid = userAccountRepository.getAccountUid()
                ?: return NetworkResult.Error(null, "Account UID not found")

            val savingsGoals = savingGoalsRepository.getAllSavingGoals(accountUid)
            savingsGoals
        } catch (e: Exception) {
            return NetworkResult.Error(null, "Something went wrong")
        }
    }
}