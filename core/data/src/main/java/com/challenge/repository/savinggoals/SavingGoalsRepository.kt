package com.challenge.repository.savinggoals

import com.challenge.api.StarlingApiService
import com.challenge.di.ApiResponse
import com.challenge.di.NetworkResult
import com.challenge.model.NewSavingGoal
import com.challenge.model.NewSavingGoalResponse
import com.challenge.model.SavingAmount
import com.challenge.model.SavingGoalTransferResponse
import com.challenge.model.SavingsGoals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


interface SavingGoalsRepository {
    suspend fun getAllSavingGoals(accountUid: String): Flow<NetworkResult<SavingsGoals>>
    suspend fun addMoneyIntoSavingGoal(
        userAccountId: String, goalUid: String, transferUid: String, savingAmount: SavingAmount
    ): Flow<NetworkResult<SavingGoalTransferResponse>>

    suspend fun createNewSavingGoal(
        userAccountId: String?,
        newSavingGoal: NewSavingGoal
    ): Flow<NetworkResult<NewSavingGoalResponse>>
}

class SavingGoalsRepoImpl @Inject constructor(
    private val apiService: StarlingApiService
) : SavingGoalsRepository, ApiResponse() {
    override suspend fun getAllSavingGoals(accountUid: String): Flow<NetworkResult<SavingsGoals>> {
        return flow {
            emit(safeApiCall {
                apiService.getSavingGoals(accountUid)
            })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun addMoneyIntoSavingGoal(
        userAccountId: String, goalUid: String, transferUid: String, savingAmount: SavingAmount
    ): Flow<NetworkResult<SavingGoalTransferResponse>> {

        return flow {
            emit(safeApiCall {
                apiService.addMoneyIntoSavingGoal(userAccountId,goalUid,transferUid, savingAmount)
            })
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun createNewSavingGoal(
        userAccountId: String?,
        newSavingGoal: NewSavingGoal
    ): Flow<NetworkResult<NewSavingGoalResponse>> {
        return flow {
            emit(
                safeApiCall {
                    userAccountId
                        ?.let { apiService.createNewSavingGoal(it, newSavingGoal) }!!
                }
            )
        }.flowOn(Dispatchers.IO)
    }
}

