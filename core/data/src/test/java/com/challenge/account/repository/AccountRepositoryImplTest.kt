package com.challenge.account.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.challenge.api.StarlingApiService
import com.challenge.common.NetworkResult
import com.challenge.common.model.accountDomain.AccountDomain
import com.challenge.common.model.accountDomain.AccountsDomain
import com.challenge.common.model.accountDto.AccountDto
import com.challenge.common.model.accountDto.AccountsDto
import com.challenge.common.utils.MainCoroutineRule
import com.challenge.common.utils.TestDispatcherProvider
import com.challenge.repository.account.AccountRepositoryImpl
import com.challenge.repository.account.toAccountsDomain
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
class AccountRepositoryImplTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainCoroutineDispatcher = MainCoroutineRule()
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mockStarlingApiService = mockk<StarlingApiService>(relaxed = true)

    private lateinit var testDispatcher: TestDispatcherProvider
    private lateinit var sut: AccountRepositoryImpl

    @Before
    fun setUp() {
        testDispatcher = TestDispatcherProvider()
        sut = AccountRepositoryImpl(
            mockStarlingApiService,
            testDispatcher
        )
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getAccounts, returns accounts successfully`() =
        runTest {
            val accountsDto = AccountsDto(
                listOf(
                    AccountDto(
                        "accountUid",
                        "accountType",
                        "defaultCategory",
                        "GBP",
                        "12/01/2023",
                        "Personal Account"
                    )
                )
            )

            val accountsNetworkResult: NetworkResult.Success<AccountsDto> =
                NetworkResult.Success(accountsDto)

            // Create accounts Flow
            val accountsFlow = flow {
                emit(accountsNetworkResult)
            }

            val accountsResponse = Response.success(accountsDto)

            coEvery { mockStarlingApiService.getUserAccounts() } returns accountsResponse

            var expectedAccountsResponse: NetworkResult<List<AccountDomain>>? = null
            accountsFlow.collect {
                expectedAccountsResponse = NetworkResult.Success(it.data?.toAccountsDomain())
            }

            var sutAccountsResponse: NetworkResult<AccountsDomain>? = null
            sut.getAccounts().collect {
                sutAccountsResponse = it
            }
            assertEquals(expectedAccountsResponse?.data, sutAccountsResponse?.data?.accounts)
        }
}