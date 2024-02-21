package com.challenge.repository.savinggoals

import com.challenge.api.StarlingApiService
import com.challenge.common.NetworkResult
import com.challenge.common.model.newsavingdomain.NewSavingGoalResponseDomain
import com.challenge.common.model.savinggoaldomain.SavingsGoalsDomain
import com.challenge.common.model.newsavingdto.NewSavingGoal
import com.challenge.common.model.savinggoaldomain.TransferDomain
import com.challenge.common.model.savinggoals.SavingAmount
import com.challenge.common.utils.DispatcherProvider
import com.challenge.di.ApiResponse
import com.challenge.repositorycontract.SavingsGoalsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class SavingGoalsRepositoryImpl @Inject constructor(
    private val apiService: StarlingApiService,
    private val dispatcherProvider: DispatcherProvider
) : SavingsGoalsRepository, ApiResponse() {
    override suspend fun getAllSavingGoals(accountUid: String): Flow<NetworkResult<SavingsGoalsDomain>> =
        flow {
            emit(handleApiCall {
                apiService.getSavingGoals(accountUid)
            })
        }.map {
            when (it) {
                is NetworkResult.Loading -> return@map NetworkResult.Loading()
                is NetworkResult.Success -> return@map NetworkResult.Success(it.data?.toSavingsGoalsDomain())
                is NetworkResult.Error -> return@map NetworkResult.Error(it.code,it.message)
            }
        }.flowOn(dispatcherProvider.io)

    override suspend fun addMoneyIntoSavingGoal(
        userAccountId: String, goalUid: String, transferUid: String, savingAmount: SavingAmount
    ): Flow<NetworkResult<TransferDomain>> =
        flow {
            emit(handleApiCall {
                apiService.addMoneyIntoSavingGoal(userAccountId, goalUid, transferUid, savingAmount)
            })
        }.map {
            when (it) {
                is NetworkResult.Loading -> return@map NetworkResult.Loading()
                is NetworkResult.Success -> return@map NetworkResult.Success(it.data?.toSavingGoalTransferDomain())
                is NetworkResult.Error -> return@map NetworkResult.Error(it.code,it.message)
            }
        }.flowOn(dispatcherProvider.io)

    override suspend fun createNewSavingGoal(
        userAccountId: String?,
        newSavingGoal: NewSavingGoal
    ): Flow<NetworkResult<NewSavingGoalResponseDomain>> =
        flow {
            emit(
                handleApiCall {
                    userAccountId
                        ?.let { apiService.createNewSavingGoal(it, newSavingGoal) }!!
                }
            )
        }.map {
            when (it) {
                is NetworkResult.Loading -> return@map NetworkResult.Loading()
                is NetworkResult.Success -> return@map NetworkResult.Success(it.data?.toNewSavingGoalResponseDomain())
                is NetworkResult.Error -> return@map NetworkResult.Error(it.code,it.message)
            }
        }.flowOn(Dispatchers.IO)
}

