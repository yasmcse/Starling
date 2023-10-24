package com.challenge.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.challenge.common.utils.MainCoroutineRule
import com.challenge.di.NetworkResult
import com.challenge.domain.GetTransactionsBetweenDatesUseCase
import com.challenge.model.Amount
import com.challenge.model.Transaction
import com.challenge.model.Transactions
import com.challenge.repository.UserAccount
import com.challenge.repository.UserAccountRepository
import com.challenge.repository.transaction.TransactionsRepository
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


@RunWith(JUnit4::class)
class GetTransactionsBetweenUseCseTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var sut: GetTransactionsBetweenDatesUseCase
    private val mockTransactionsRepository = mockk<TransactionsRepository>(relaxed = true)

    @Before
    fun setUp() {
        sut = GetTransactionsBetweenDatesUseCase(mockTransactionsRepository)
    }

    @Test
    fun `Given the getAccounts is called succussfully returns the list of accounts`() {

        runTest {
            // Transactions test data
            val amount = Amount("GBP", 1234L, 0.0)
            val transaction = Transaction("FeedItemUid", "CategoryUid", amount, amount, "IN")
            val transactions = Transactions(listOf(transaction))

            val networkResultTransactions: NetworkResult<Transactions> =
                NetworkResult.Success(transactions)

            // Transactions flow
            val flowTransactions = flow {
                emit(networkResultTransactions)
            }

            var expectedTransactionsResponse: Transactions? = null
            flowTransactions.collect {
                expectedTransactionsResponse = it.data
            }

            // Account repository test data
            val accountRepository = UserAccountRepository()
            with(accountRepository) {
                setAccountId("account1234")
                setCategoryUid("category123")
            }

            // User Account Test Data
            val userAccount = UserAccount(
                accountRepository.getAccountUid()!!,
                accountRepository.getCategoryUid()!!,
                HomeViewModel.minTransactionTimeStamp,
                HomeViewModel.maxTransactionTimeStamp
            )
            coEvery { mockTransactionsRepository.getTransactionsBetween(userAccount) } returns flowTransactions

            var actualTransactionsResponse:Transactions? = null
            sut.getTransactions(userAccount).collect{
                actualTransactionsResponse = it.data
            }
            assertEquals(expectedTransactionsResponse?.feedItems,actualTransactionsResponse?.feedItems)
        }
    }


}