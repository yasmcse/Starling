package com.challenge.transaction.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.challenge.api.StarlingApiService
import com.challenge.common.NetworkResult
import com.challenge.common.model.Amount
import com.challenge.common.model.accountDto.UserAccount
import com.challenge.common.model.transactiondomain.TransactionsDomain
import com.challenge.common.model.transactiondto.TransactionDto
import com.challenge.common.model.transactiondto.TransactionsDto
import com.challenge.common.utils.MainCoroutineRule
import com.challenge.common.utils.TestDispatcherProvider
import com.challenge.repository.transaction.TransactionsRepositoryImpl
import com.challenge.repository.transaction.toTransactionsDomain
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response

@RunWith(JUnit4::class)
class TransactionsRepositoryImplTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainCoroutineDispatcher = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mockStarlingApiService = mockk<StarlingApiService>(relaxed = true)

    private lateinit var testDispatcher: TestDispatcherProvider
    private lateinit var sut: TransactionsRepositoryImpl


    @Before
    fun setUp() {
        testDispatcher = TestDispatcherProvider()
        sut = TransactionsRepositoryImpl(
            mockStarlingApiService,
            testDispatcher
        )
    }

    @Test
    fun `getTransactionsBetween, returns succesful`() =
        runTest {
            val userAccount = UserAccount(
                "accountUid",
                "categoryUid",
                "minTransactionTimeStamp",
                "maxTransactionTimeStamp"
            )

            val transactionsDto = TransactionsDto(
                listOf(
                    TransactionDto(
                        "feedItemUid", "categoryUid",
                        Amount("GBP", 1234L, 0.0),
                        Amount("GBP", 1234L, 0.0),
                        "IN"
                    )
                )
            )

            val response = Response.success(transactionsDto)

            coEvery {
                mockStarlingApiService.getTransactionsBetween(
                    userAccount.accountUid,
                    userAccount.categoryUid,
                    userAccount.minTransactionTimeStamp,
                    userAccount.maxTransactionTimeStamp
                )
            } returns response

            val transactionsNetworkResult: NetworkResult.Success<TransactionsDto> =
                NetworkResult.Success(transactionsDto)

            // Create transactions Flow
            val transactionsFlow = flow {
                emit(transactionsNetworkResult)
            }

            var expectedTransactionResponse: NetworkResult<TransactionsDomain>? = null
            transactionsFlow.collect {
                expectedTransactionResponse = NetworkResult.Success(it.data?.toTransactionsDomain())
            }

            var sutTransactionResponse:NetworkResult<TransactionsDomain>? = null
            sut.getTransactionsBetween(userAccount).collect{
            sutTransactionResponse = it
            }

            assertEquals(expectedTransactionResponse?.data?.feedItems,sutTransactionResponse?.data?.feedItems)
}
}