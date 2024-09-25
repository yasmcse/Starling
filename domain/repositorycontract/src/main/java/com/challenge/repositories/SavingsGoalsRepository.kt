package com.challenge.repositories

import com.challenge.common.model.NetworkResult
import com.challenge.model.savinggoal.NewSavingGoalResponseDomain
import com.challenge.model.savinggoal.SavingsGoalsDomain
import com.challenge.common.model.newsavingdto.NewSavingGoal
import com.challenge.model.transaction.TransferDomain
import com.challenge.common.model.savinggoals.SavingAmount

interface SavingsGoalsRepository {
    suspend fun getAllSavingGoals(accountUid: String): NetworkResult<SavingsGoalsDomain>
    suspend fun addMoneyIntoSavingGoal(
        userAccountId: String, goalUid: String, transferUid: String, savingAmount: SavingAmount
    ): NetworkResult<TransferDomain>

    suspend fun createNewSavingGoal(
        userAccountId: String?,
        newSavingGoal: NewSavingGoal
    ): NetworkResult<NewSavingGoalResponseDomain>
}