package com.challenge.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.challenge.common.Error
import com.challenge.common.ErrorResponse
import com.challenge.common.utils.CurrencyUnitsMapper
import com.challenge.common.utils.MainCoroutineRule
import com.challenge.common.utils.TestDispatcherProvider
import com.challenge.common.NetworkResult
import com.challenge.common.UserAccountRepository
import com.challenge.common.model.Amount
import com.challenge.common.model.accountDomain.AccountDomain
import com.challenge.common.model.accountDomain.AccountsDomain
import com.challenge.common.model.accountDto.UserAccount
import com.challenge.common.model.transactiondomain.TransactionDomain
import com.challenge.common.model.transactiondomain.TransactionsDomain
import com.challenge.home.ui.HomeViewModel
import com.challenge.repositorycontract.AccountRepository
import com.challenge.repositorycontract.TransactionsRepository
import io.mockk.coEvery
import io.mockk.every
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
class HomeScreenViewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var sut: HomeViewModel
    private val mockAccountRepository = mockk<AccountRepository>(relaxed = true)
    private val mockTransactionsRepository = mockk<TransactionsRepository>(relaxed = true)
    private val mockUserAccountRepository = mockk<UserAccountRepository>(relaxed = true)
    private val mockCurrencyUnitsMapper = mockk<CurrencyUnitsMapper>(relaxed = true)
    private lateinit var testDispatcherProvider: TestDispatcherProvider

    @Before
    fun setUp() {
        testDispatcherProvider = TestDispatcherProvider()
        sut = HomeViewModel(
            mockAccountRepository,
            mockTransactionsRepository,
            mockUserAccountRepository,
            mockCurrencyUnitsMapper,
            testDispatcherProvider
        )
    }

    @Test
    fun `Given the view model state is not changed returns the initial loader state`() =
        runTest {
            val networkResult: NetworkResult<TransactionsDomain> = NetworkResult.Loading()
            assertEquals(networkResult.data, sut.transactionsList.value.data)
        }


    @Test
    fun `Given that the user account is saved in repository successfully`() =
        runTest {
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
                HomeViewModel.minTransactionTimeStamp,
                HomeViewModel.maxTransactionTimeStamp
            )

            val accountsDomain = AccountsDomain(listOf(AccountDomain("accountUid","accountType","defaultCategory")))
            val networkResultAccountsDomain: NetworkResult<AccountsDomain> =
                NetworkResult.Success(accountsDomain)

            // Create Accounts flow
            val flowAccounts = flow {
                emit(networkResultAccountsDomain)
            }

            var userAccountResponse: AccountsDomain? = null
            flowAccounts.collect {
                userAccountResponse = it.data
            }

            every { mockUserAccountRepository.getAccountUid() } returns accountRepository.getAccountUid()
            every{ mockUserAccountRepository.getCategoryUid() } returns accountRepository.getCategoryUid()

            sut.saveUserAccount(userAccount)

            assertEquals(
                userAccountResponse?.accounts?.get(0)?.accountUid,
                mockUserAccountRepository.getAccountUid()
            )
        }

    @Test
    fun `fetchTransactions, fetches the transactions list successfully`() {
        runTest {
            // Accounts test data
            val accountsDomain = AccountsDomain(listOf(AccountDomain("accountUid","accountType","defaultCategory")))
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
                HomeViewModel.minTransactionTimeStamp,
                HomeViewModel.maxTransactionTimeStamp
            )

            val networkResultAccountsDomain: NetworkResult<AccountsDomain> =
                NetworkResult.Success(accountsDomain)

            // Accounts flow

            val flowAccounts = flow {
                emit(networkResultAccountsDomain)
            }

            coEvery { mockAccountRepository.getAccounts() } returns flowAccounts

            // Transactions list in minor units test data
            val amount = Amount("GBP", 1234L, 0.0)
            val transactionDomain = TransactionDomain("FeedItemUid", "CategoryUid", amount, amount, "IN")
            val transactionList = listOf(transactionDomain)
            val transactionsDomain = TransactionsDomain(transactionList)

            val networkResultTransactionsDomain: NetworkResult<TransactionsDomain> =
                NetworkResult.Success(transactionsDomain)

            val flowTransactions = flow {
                emit(networkResultTransactionsDomain)
            }

            coEvery { mockTransactionsRepository.getTransactionsBetween(userAccount) } returns flowTransactions

            var transactionsDomainResponse: TransactionsDomain? = null
            flowTransactions.collect {
                transactionsDomainResponse = it.data
            }

            sut.fetchTransactions()
            assertEquals(transactionsDomainResponse, sut.transactionsList.value.data)
        }
    }


    @Test
    fun `fetchTransactions,getAccounts successful but getTransactionsBetween returns error`() {
        runTest {

            val accountsDomain = AccountsDomain(listOf(AccountDomain("accountUid","accountType","defaultCategory")))
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
                HomeViewModel.minTransactionTimeStamp,
                HomeViewModel.maxTransactionTimeStamp
            )

            val networkResultAccountsDomain: NetworkResult<AccountsDomain> =
                NetworkResult.Success(accountsDomain)

            // Accounts flow

            val flowAccounts = flow {
                emit(networkResultAccountsDomain)
            }

            coEvery { mockAccountRepository.getAccounts() } returns flowAccounts

            val networkResultTransactionsDomain: NetworkResult<TransactionsDomain> =
                NetworkResult.Error(ErrorResponse(listOf(Error("Api failed")),false))

            val flowTransactions = flow {
                emit(networkResultTransactionsDomain)
            }

            coEvery { mockTransactionsRepository.getTransactionsBetween(userAccount) } returns flowTransactions

            var transactionsDomainResponse: TransactionsDomain? = null
            flowTransactions.collect {
                transactionsDomainResponse = it.data
            }

            sut.fetchTransactions()
            assertEquals(transactionsDomainResponse, sut.transactionsList.value.data)
        }

    }


    @Test
    fun `fetchTransactions, getAccounts returns error`() {
        runTest {

            val networkResultAccountsDomain: NetworkResult<AccountsDomain> =
                NetworkResult.Error(ErrorResponse(listOf(Error("Api failed")),false))

            // Accounts flow

            val flowAccounts = flow {
                emit(networkResultAccountsDomain)
            }

            coEvery { mockAccountRepository.getAccounts() } returns flowAccounts

            sut.fetchTransactions()
            assertEquals(null, sut.transactionsList.value.data)
        }
    }

    @Test
    fun `Given the transactions list in minor units returns major units in reach transaction as readable format`() {
        // Transactions list in minor units test data
        val amount = Amount("GBP", 1234L, 0.0)
        val transactionDomain = TransactionDomain("FeedItemUid", "CategoryUid", amount, amount, "IN")
        val transactionList = listOf(transactionDomain)

        // Transactions list in major units computed
        val amount2 = Amount("GBP", 1234L, 12.34)
        val transactionDomain2 = TransactionDomain("FeedItemUid", "CategoryUid", amount2, amount2, "IN")
        val transactionList2 = listOf(transactionDomain2)

        every { mockCurrencyUnitsMapper.convertMinorUnitToMajorUnit(transactionList) } returns transactionList2
        assertEquals(transactionList2, sut.getCurrencyInReadable(transactionList))
    }



    @Test
    fun `Given the list of transactions in minor units for each transaction returns the sum of rounded minor units`() {
        // Transactions list in minor units test data
        val amount = Amount("GBP", 1234L, 12.34)
        val transactionDomain = TransactionDomain("FeedItemUid", "CategoryUid", amount, amount, "IN")
        val transactionList = listOf(transactionDomain)

        every { mockCurrencyUnitsMapper.convertMajorUnitsToMinorUnits(12.34) } returns 1234.00

        sut.getCurrencyInReadable(transactionList)
        assertEquals(1234.00, mockCurrencyUnitsMapper.convertMajorUnitsToMinorUnits(12.34),0.001)
    }
}