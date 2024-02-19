package com.challenge.repositorycontract

import com.challenge.common.NetworkResult
import com.challenge.common.model.newsavingdomain.NewSavingGoalResponseDomain
import com.challenge.common.model.savinggoaldomain.SavingsGoalsDomain
import com.challenge.common.model.newsavingdto.NewSavingGoal
import com.challenge.common.model.savinggoaldomain.TransferDomain
import com.challenge.common.model.savinggoals.SavingAmount
import kotlinx.coroutines.flow.Flow

interface SavingsGoalsRepository {
    suspend fun getAllSavingGoals(accountUid: String): Flow<NetworkResult<SavingsGoalsDomain>>
    suspend fun addMoneyIntoSavingGoal(
        userAccountId: String, goalUid: String, transferUid: String, savingAmount: SavingAmount
    ): Flow<NetworkResult<TransferDomain>>

    suspend fun createNewSavingGoal(
        userAccountId: String?,
        newSavingGoal: NewSavingGoal
    ): Flow<NetworkResult<NewSavingGoalResponseDomain>>
}