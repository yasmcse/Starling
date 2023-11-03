package com.challenge.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.challenge.common.utils.CurrencyUnitsMapper
import com.challenge.common.utils.MainCoroutineRule
import com.challenge.common.utils.TestDispatcherProvider
import com.challenge.di.NetworkResult
import com.challenge.domain.GetAccountsUseCase
import com.challenge.domain.GetTransactionsBetweenDatesUseCase
import com.challenge.model.Account
import com.challenge.model.Accounts
import com.challenge.model.Amount
import com.challenge.model.Transaction
import com.challenge.model.Transactions
import com.challenge.repository.UserAccount
import com.challenge.repository.UserAccountRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class HomeViewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var sut: HomeViewModel
    private val mockAccountsUseCase = mockk<GetAccountsUseCase>(relaxed = true)
    private val mockGetTransactionsBetweenDatesUseCase =
        mockk<GetTransactionsBetweenDatesUseCase>(relaxed = true)
    private val userAccountsRepository = UserAccountRepository()
    private val mockCurrencyUnitsMapper = mockk<CurrencyUnitsMapper>(relaxed = true)
    private lateinit var testDispatcherProvider: TestDispatcherProvider

    @Before
    fun setUp() {
        testDispatcherProvider = TestDispatcherProvider()
        sut = HomeViewModel(
            mockAccountsUseCase,
            mockGetTransactionsBetweenDatesUseCase,
            userAccountsRepository,
            mockCurrencyUnitsMapper,
            testDispatcherProvider
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Given the view model state is not changed returns the initial loader state`() {
        runTest {
            val networkResult: NetworkResult<Transactions> = NetworkResult.Loading()
            assertEquals(networkResult.data, sut.transactionsList.value.data)
        }
    }

    @Test
    fun `Given that the user account is saved in repository successfully`() {

        runTest {
            // Accounts test data
            val account =
                Account(
                    "accountUid",
                    "accountType",
                    "defaultCategory",
                    "currency",
                    "createdAt",
                    "name"
                )
            val accountList = listOf(account)
            val accounts = Accounts(accountList)

            // Account repository test data
            val accountRepository = UserAccountRepository()
            with(accountRepository) {
                setAccountId("accountUid")
                setCategoryUid("defaultCategory")
            }

            // User Account Test Data
            val userAccount = UserAccount(
                accountRepository.getAccountUid()!!,
                accountRepository.getCategoryUid()!!,
                HomeViewModel.Companion.minTransactionTimeStamp,
                HomeViewModel.Companion.maxTransactionTimeStamp
            )

            val networkResultAccounts: NetworkResult<Accounts> = NetworkResult.Success(accounts)

            // Accounts flow

            val flowAccounts = flow {
                emit(networkResultAccounts)
            }

            coEvery { mockAccountsUseCase.getAccounts() } returns flowAccounts

            var userAccountResponse: Accounts? = null
            flowAccounts.collect {
                userAccountResponse = it.data
            }


            sut.saveUserAccount(userAccount)

            assertEquals(
                userAccountResponse?.accounts?.get(0)?.accountUid,
                userAccountsRepository.getAccountUid()
            )
            assertEquals(
                userAccountResponse?.accounts?.get(0)?.defaultCategory,
                userAccountsRepository.getCategoryUid()
            )
        }

    }

    @Test
    fun `fetchTransactions, fetches the transactions list successfully`() {

        runBlocking {
            // Accounts test data
            val account =
                Account(
                    "accountUid",
                    "accountType",
                    "defaultCategory",
                    "currency",
                    "createdAt",
                    "name"
                )
            val accountList = listOf(account)
            val accounts = Accounts(accountList)

            // Account repository test data
            val accountRepository = UserAccountRepository()
            with(accountRepository) {
                setAccountId("accountUid")
                setCategoryUid("defaultCategory")
            }

            // User Account Test Data
            val userAccount = UserAccount(
                accountRepository.getAccountUid()!!,
                accountRepository.getCategoryUid()!!,
                HomeViewModel.Companion.minTransactionTimeStamp,
                HomeViewModel.Companion.maxTransactionTimeStamp
            )

            val networkResultAccounts: NetworkResult<Accounts> = NetworkResult.Success(accounts)

            // Accounts flow

            val flowAccounts = flow {
                emit(networkResultAccounts)
            }

            coEvery { mockAccountsUseCase.getAccounts() } returns flowAccounts

            var userAccountResponse: Accounts? = null
            flowAccounts.collect {
                userAccountResponse = it.data
            }

            // Transactions list in minor units test data
            val amount = Amount("GBP", 1234L, 0.0)
            val transaction = Transaction("FeedItemUid", "CategoryUid", amount, amount, "IN")
            val transactionList = listOf(transaction)
            val transactions = Transactions(transactionList)

            val networkResultTransactions: NetworkResult<Transactions> =
                NetworkResult.Success(transactions)

            val flowTransactions = flow {
                emit(networkResultTransactions)
            }

            coEvery { mockGetTransactionsBetweenDatesUseCase.getTransactions(userAccount) } returns flowTransactions

            var transactionsResponse:Transactions? = null
            flowTransactions.collect{
                transactionsResponse = it.data
            }

            sut.fetchTransactions()
            assertEquals(transactionsResponse,sut.transactionsList.value.data)
        }

    }

    @Test
    fun `Given the transactions list in minor units returns major units in reach transaction as readable format`() {
        // Transactions list in minor units test data
        val amount = Amount("GBP", 1234L, 0.0)
        val transaction = Transaction("FeedItemUid", "CategoryUid", amount, amount, "IN")
        val transactionList = listOf(transaction)

        // Transactions list in major units computed
        val amount2 = Amount("GBP", 1234L, 12.34)
        val transaction2 = Transaction("FeedItemUid", "CategoryUid", amount2, amount2, "IN")
        val transactionList2 = listOf(transaction2)

        every { mockCurrencyUnitsMapper.convertMinorUnitToMajorUnit(transactionList) } returns transactionList2
        assertEquals(transactionList2, sut.getCurrencyInReadable(transactionList))
    }


    @Test
    fun `Given the list of transactions in minor units for each transaction returns the sum of rounded minor units`() {
        // Transactions list in minor units test data
        val amount = Amount("GBP", 1234L, 12.34)
        val transaction = Transaction("FeedItemUid", "CategoryUid", amount, amount, "IN")
        val transactionList = listOf(transaction)

        // Transactions list in major units computed
        val amount2 = Amount("GBP", 1234L, 12.34)
        val transaction2 = Transaction("FeedItemUid", "CategoryUid", amount2, amount2, "IN")
        val transactionList2 = listOf(transaction, transaction2)

        every { mockCurrencyUnitsMapper.convertMajorUnitsToMinorUnits(12.34) } returns 1234.00

        sut.getCurrencyInReadable(transactionList)
        assertEquals(1234.00, mockCurrencyUnitsMapper.convertMajorUnitsToMinorUnits(12.34))
    }


}