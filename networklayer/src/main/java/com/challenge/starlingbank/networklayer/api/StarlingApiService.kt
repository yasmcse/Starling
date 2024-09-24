package com.challenge.starlingbank.networklayer.api

import com.challenge.common.model.accountDto.AccountsDto
import com.challenge.common.model.newsavingdto.NewSavingGoal
import com.challenge.common.model.newsavingdto.NewSavingGoalResponseDto
import com.challenge.common.model.savinggoals.SavingAmount
import com.challenge.common.model.savinggoaldto.SavingsGoalsDto
import com.challenge.common.model.savinggoaldto.SavingGoalTransferResponseDto
import com.challenge.common.model.transactiondto.TransactionsDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

const val API_VERSION = "/api/v2"

interface StarlingApiService {

    // Account
    @GET("$API_VERSION/accounts")
    suspend fun getUserAccounts(): Response<AccountsDto>


    // Transactions
    @GET("$API_VERSION/feed/account/{accountUid}/category/{categoryUid}/transactions-between")
    suspend fun getTransactionsBetween(
        @Path("accountUid") accountUid: String,
        @Path("categoryUid") categoryUid: String,
        @Query("minTransactionTimestamp") minTransactionTimeStamp: String,
        @Query("maxTransactionTimestamp") maxTransactionTimeStamp: String
    ): Response<TransactionsDto>

    // Saving Goals
    @GET("$API_VERSION/account/{accountUid}/savings-goals")
    suspend fun getSavingGoals(@Path("accountUid") accountUid: String): Response<SavingsGoalsDto>

    @PUT("$API_VERSION/account/{accountUid}/savings-goals/{savingsGoalUid}/add-money/{transferUid}")
    suspend fun addMoneyIntoSavingGoal(
        @Path("accountUid") accountUid: String,
        @Path("savingsGoalUid") savingsGoalUid: String,
        @Path("transferUid") transferUid: String,
        @Body savingAmount: SavingAmount
    ): Response<SavingGoalTransferResponseDto>

    @PUT("$API_VERSION/account/{accountUid}/savings-goals")
    suspend fun createNewSavingGoal(
        @Path("accountUid") accountUid: String,
        @Body newSavingGoal: NewSavingGoal
    ):Response<NewSavingGoalResponseDto>

}