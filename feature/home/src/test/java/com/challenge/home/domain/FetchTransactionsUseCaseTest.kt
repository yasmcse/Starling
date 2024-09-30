package com.challenge.home.domain

import com.challenge.common.UserAccountRepository
import com.challenge.common.model.NetworkResult
import com.challenge.common.model.accountDto.UserAccount
import com.challenge.home.domain.usecase.FetchTransactionsUseCase
import com.challenge.model.account.AccountDomain
import com.challenge.model.account.AccountsDomain
import com.challenge.model.transaction.TransactionsDomain
import com.challenge.repositories.AccountRepository
import com.challenge.repositories.TransactionsRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
class FetchTransactionsUseCaseTest {

    // Inject MockKRule for initializing @MockK annotations
    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var accountRepository: AccountRepository

    @MockK
    lateinit var transactionRepository: TransactionsRepository

    @MockK
    lateinit var userAccountRepository: UserAccountRepository

    // Subject under test
    private lateinit var fetchTransactionsUseCase: FetchTransactionsUseCase

    @Before
    fun setUp() {
        // Initialize the Use Case with mocked repositories
        fetchTransactionsUseCase = FetchTransactionsUseCase(
            accountRepository, transactionRepository, userAccountRepository
        )
    }

    @Test
    fun `should return NetworkResult_Error when no accounts are found`() = runTest {
        // Given
        val emptyAccountResult = NetworkResult.Success(AccountsDomain(accounts = emptyList()))
        coEvery { accountRepository.getAccounts() } returns emptyAccountResult

        // When
        val result = fetchTransactionsUseCase.invoke()

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("Unknown error", (result as NetworkResult.Error).message)

        coVerify { accountRepository.getAccounts() }
        coVerify(exactly = 0) { transactionRepository.getTransactionsBetween(any()) }
    }

    @Test
    fun `should return NetworkResult_Error when account data is null or empty`() = runTest {
        // Given
        val nullAccountResult: NetworkResult<AccountsDomain> = NetworkResult.Error(code = 404, message = "No accounts available")
        coEvery { accountRepository.getAccounts() } returns nullAccountResult

        // When
        val result = fetchTransactionsUseCase.invoke()

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals(404, (result as NetworkResult.Error).code)
        assertEquals("No accounts available", result.message)

        coVerify { accountRepository.getAccounts() }
        coVerify(exactly = 0) { transactionRepository.getTransactionsBetween(any()) }
    }

    @Test
    fun `should return NetworkResult_Success and save UserAccount when account is found`() = runBlocking {
        // Given
        val accountUid = "accountUid1"
        val accountType = "accountType"
        val categoryUid = "categoryUid1"

        // Mock accounts
        val accounts = listOf(AccountDomain(accountUid = accountUid,accountType, defaultCategory = categoryUid))
        val accountResult = NetworkResult.Success(AccountsDomain(accounts = accounts))

        // Mock user account
        val userAccount = UserAccount(
            accountUid = accountUid,
            categoryUid = categoryUid,
            minTransactionTimeStamp = FetchTransactionsUseCase.minTransactionTimeStamp,
            maxTransactionTimeStamp = FetchTransactionsUseCase.maxTransactionTimeStamp
        )

        // Mock transactions
        val transactionsDomain = TransactionsDomain(listOf())

        // Mock behavior for repositories
        coEvery { accountRepository.getAccounts() } returns accountResult
        coEvery { transactionRepository.getTransactionsBetween(userAccount) } returns NetworkResult.Success(transactionsDomain)

        // Mock saving of user account
        every { userAccountRepository.setAccountId(accountUid) } just Runs
        every { userAccountRepository.setCategoryUid(categoryUid) } just Runs

        // When
        val result = fetchTransactionsUseCase.invoke()

        // Then
        assertTrue(result is NetworkResult.Success)
        assertEquals(transactionsDomain, (result as NetworkResult.Success).data)

        // Verify that the user account was saved
        verify { userAccountRepository.setAccountId(accountUid) }
        verify { userAccountRepository.setCategoryUid(categoryUid) }

        // Verify that transactions were fetched
        coVerify { transactionRepository.getTransactionsBetween(userAccount) }
    }

    @Test
    fun `should return NetworkResult_Error when transaction fetch fails`() = runBlocking {
        // Given
        val accountUid = "accountUid1"
        val accountType = "accountType"
        val categoryUid = "categoryUid1"

        // Mock the account returned by accountRepository
        val accounts = listOf(AccountDomain(accountUid = accountUid,accountType, defaultCategory = categoryUid))
        val accountResult = NetworkResult.Success(AccountsDomain(accounts = accounts))

        // Mock the user account to be passed into getTransactionsBetween
        val userAccount = UserAccount(
            accountUid = accountUid,
            categoryUid = categoryUid,
            minTransactionTimeStamp = FetchTransactionsUseCase.minTransactionTimeStamp,
            maxTransactionTimeStamp = FetchTransactionsUseCase.maxTransactionTimeStamp
        )

        // Mock the failed transaction fetch
        val errorResult:NetworkResult<TransactionsDomain> = NetworkResult.Error(null, "Transaction fetch failed")

        // Mock accountRepository to return a valid account result
        coEvery { accountRepository.getAccounts() } returns accountResult

        // Mock transactionRepository to return an error when fetching transactions
        coEvery { transactionRepository.getTransactionsBetween(userAccount) } returns errorResult

        // Mock the user account repository actions (these do not need to return anything, just run)
        every { userAccountRepository.setAccountId(accountUid) } just Runs
        every { userAccountRepository.setCategoryUid(categoryUid) } just Runs

        // When
        val result = fetchTransactionsUseCase.invoke()

        // Then
        assertTrue(result is NetworkResult.Error)
        assertEquals("Transaction fetch failed", (result as NetworkResult.Error).message)

        // Verify that transaction fetch was attempted
        coVerify { transactionRepository.getTransactionsBetween(userAccount) }

        // Verify that user account was still saved
        verify { userAccountRepository.setAccountId(accountUid) }
        verify { userAccountRepository.setCategoryUid(categoryUid) }
    }
}