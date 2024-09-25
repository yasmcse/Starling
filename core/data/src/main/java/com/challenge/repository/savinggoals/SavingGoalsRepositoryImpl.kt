package com.challenge.repository.savinggoals

import com.challenge.common.model.NetworkResult
import com.challenge.model.savinggoal.NewSavingGoalResponseDomain
import com.challenge.model.savinggoal.SavingsGoalsDomain
import com.challenge.common.model.newsavingdto.NewSavingGoal
import com.challenge.model.transaction.TransferDomain
import com.challenge.common.model.savinggoals.SavingAmount
import com.challenge.mapper.savinggoal.toNewSavingGoalResponseDomain
import com.challenge.mapper.savinggoal.toSavingGoalTransferDomain
import com.challenge.mapper.savinggoal.toSavingsGoalsDomain
import com.challenge.repositorycontract.SavingsGoalsRepository
import javax.inject.Inject


class SavingGoalsRepositoryImpl @Inject constructor(
    private val apiService: com.challenge.starlingbank.networklayer.api.StarlingApiService
) : SavingsGoalsRepository, com.challenge.starlingbank.networklayer.model.ApiResponse() {
    override suspend fun getAllSavingGoals(accountUid: String): NetworkResult<SavingsGoalsDomain> =
        handleApiCall(
            apiCall = { apiService.getSavingGoals(accountUid) },
            mapToDomain = { it.toSavingsGoalsDomain() }
        )

    override suspend fun addMoneyIntoSavingGoal(
        userAccountId: String, goalUid: String, transferUid: String, savingAmount: SavingAmount
    ): NetworkResult<TransferDomain> =
        handleApiCall(
            apiCall = {
                apiService.addMoneyIntoSavingGoal(
                    userAccountId,
                    goalUid,
                    transferUid,
                    savingAmount
                )
            },
            mapToDomain = { it.toSavingGoalTransferDomain() })


    override suspend fun createNewSavingGoal(
        userAccountId: String?,
        newSavingGoal: NewSavingGoal
    ): NetworkResult<NewSavingGoalResponseDomain> =
        if (userAccountId == null) {
            NetworkResult.Error(null, "User account ID is required")
        } else {
            handleApiCall(
                apiCall = { apiService.createNewSavingGoal(userAccountId, newSavingGoal) },
                mapToDomain = { it.toNewSavingGoalResponseDomain() }
            )
        }
}

