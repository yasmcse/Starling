package com.challenge.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.challenge.common.utils.MainCoroutineRule
import com.challenge.di.NetworkResult
import com.challenge.domain.GetAccountsUseCase
import com.challenge.model.Account
import com.challenge.model.Accounts
import com.challenge.repository.account.AccountRepository
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
class GetAccountsUseCaseTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var sut: GetAccountsUseCase
    private val accountRepository = mockk<AccountRepository>(relaxed = true)


    @Before
    fun setUp() {
        sut = GetAccountsUseCase(accountRepository)
    }

    @Test
    fun `Given the getAccounts is called succussfully returns the list of accounts`() {

        runTest {
            // Accounts test data
            val account = Account(
                "accountUid", "accountType", "defaultCategory", "currency", "createdAt", "name"
            )
            val accountList = listOf(account)
            val accounts = Accounts(accountList)
            val networkResultAccounts: NetworkResult<Accounts> = NetworkResult.Success(accounts)

            // Accounts flow
            val flowAccounts = flow {
                emit(networkResultAccounts)
            }

            var expectedUserAccountResponse: Accounts? = null
            flowAccounts.collect {
                expectedUserAccountResponse = it.data
            }

            coEvery { accountRepository.getAccounts()  } returns flowAccounts

            var actualUserAccountResponse:Accounts? = null
            sut.getAccounts().collect{
                actualUserAccountResponse = it.data
            }
            assertEquals(expectedUserAccountResponse,actualUserAccountResponse)
        }
    }
}