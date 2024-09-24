package com.challenge.repositorycontract

import com.challenge.common.model.NetworkResult
import com.challenge.mapper.savinggoal.model.NewSavingGoalResponseDomain
import com.challenge.mapper.savinggoal.model.SavingsGoalsDomain
import com.challenge.common.model.newsavingdto.NewSavingGoal
import com.challenge.mapper.transaction.model.TransferDomain
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