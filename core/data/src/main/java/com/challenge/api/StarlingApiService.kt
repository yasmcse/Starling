package com.challenge.api

import com.challenge.di.ApiResponse
import com.challenge.model.Accounts
import com.challenge.model.NewSavingGoal
import com.challenge.model.NewSavingGoalResponse
import com.challenge.model.SavingAmount
import com.challenge.model.SavingGoalTransferResponse
import com.challenge.model.SavingsGoals
import com.challenge.model.Transactions
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

const val API_VERSION = "/api/v2"

interface StarlingApiService {

    // Account
    @GET("${API_VERSION}/accounts")
    suspend fun getUserAccounts(): Response<Accounts>


    // Transactions
    @GET("${API_VERSION}/feed/account/{accountUid}/category/{categoryUid}/transactions-between")
    suspend fun getTransactionsBetween(
        @Path("accountUid") accountUid: String,
        @Path("categoryUid") categoryUid: String,
        @Query("minTransactionTimestamp") minTransactionTimeStamp: String,
        @Query("maxTransactionTimestamp") maxTransactionTimeStamp: String
    ): Response<Transactions>

    // Saving Goals
    @GET("${API_VERSION}/account/{accountUid}/savings-goals")
    suspend fun getSavingGoals(@Path("accountUid") accountUid: String): Response<SavingsGoals>

    @PUT("${API_VERSION}/account/{accountUid}/savings-goals/{savingsGoalUid}/add-money/{transferUid}")
    suspend fun addMoneyIntoSavingGoal(
        @Path("accountUid") accountUid: String,
        @Path("savingsGoalUid") savingsGoalUid: String,
        @Path("transferUid") transferUid: String,
        @Body savingAmount: SavingAmount
    ): Response<SavingGoalTransferResponse>

    @PUT("${API_VERSION}/account/{accountUid}/savings-goals")
    suspend fun createNewSavingGoal(
        @Path("accountUid") accountUid: String,
        @Body newSavingGoal: NewSavingGoal
    ):Response<NewSavingGoalResponse>


}