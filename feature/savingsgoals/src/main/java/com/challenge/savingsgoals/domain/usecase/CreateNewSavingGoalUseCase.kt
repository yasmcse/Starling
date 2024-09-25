package com.challenge.savingsgoals.domain.usecase

import com.challenge.common.UserAccountRepository
import com.challenge.common.model.newsavingdto.NewSavingGoal
import com.challenge.repositorycontract.SavingsGoalsRepository
import javax.inject.Inject

class CreateNewSavingGoalUseCase @Inject constructor(
    private val savingGoalsRepository: SavingsGoalsRepository,
    private val userAccountRepository: UserAccountRepository
) {
    suspend operator fun invoke(newSavingGoal: NewSavingGoal) =
        savingGoalsRepository.createNewSavingGoal(userAccountRepository.getAccountUid(),newSavingGoal)
}