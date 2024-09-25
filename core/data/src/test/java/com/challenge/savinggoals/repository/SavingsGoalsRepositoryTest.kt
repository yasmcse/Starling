package com.challenge.savinggoals.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.challenge.common.model.NetworkResult
import com.challenge.common.UserAccountRepository
import com.challenge.common.model.Amount
import com.challenge.common.model.accountDto.AccountDto
import com.challenge.model.savinggoal.NewSavingGoalResponseDomain
import com.challenge.common.model.newsavingdto.NewSavingGoal
import com.challenge.common.model.newsavingdto.NewSavingGoalResponseDto
import com.challenge.model.savinggoal.SavingsGoalsDomain
import com.challenge.model.transaction.TransferDomain
import com.challenge.common.model.savinggoaldto.SavingGoalTransferResponseDto
import com.challenge.common.model.savinggoaldto.SavingGoalsStatus
import com.challenge.common.model.savinggoaldto.SavingTarget
import com.challenge.common.model.savinggoaldto.SavingsGoalDto
import com.challenge.common.model.savinggoaldto.SavingsGoalsDto
import com.challenge.common.model.savinggoals.SavingAmount
import com.challenge.common.model.savinggoals.SavingGoalAmount
import com.challenge.common.utils.MainCoroutineRule
import com.challenge.common.utils.TestDispatcherProvider
import com.challenge.repository.savinggoals.SavingGoalsRepositoryImpl
import com.challenge.mapper.savinggoal.toSavingsGoalsDomain
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
import java.util.UUID

@RunWith(JUnit4::class)
class SavingsGoalsRepositoryTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainCoroutineDispatcher = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mockUserAccountRepository = mockk<UserAccountRepository>(relaxed = true)
    private val mockStarlingApiService = mockk<com.challenge.starlingbank.networklayer.api.StarlingApiService>(relaxed = true)

    private lateinit var testDispatcher: TestDispatcherProvider
    private lateinit var sut: SavingGoalsRepositoryImpl

    @Before
    fun setUp() {
        testDispatcher = TestDispatcherProvider()
        sut = SavingGoalsRepositoryImpl(
            mockStarlingApiService
        )
    }

    @Test
    fun `getAllSavingsGoals, returns savings goals successfully`() =
        runTest {
            val accountDto =
                AccountDto(
                    "accountUid",
                    "accountType",
                    "defaultCategory",
                    "GBP",
                    "12/01/2023",
                    "Personal Account"
                )

            val savingsGoalDto = SavingsGoalDto(
                "savingGoalUid",
                "name",
                Amount("GBP", 1000L, 0.0),
                Amount("GBP", 100L, 0.0),
                1,
                SavingGoalsStatus.ACTIVE
            )
            val savingsGoalsDto = SavingsGoalsDto(listOf(savingsGoalDto))

            val savingsGoalsNetworkResult: NetworkResult.Success<SavingsGoalsDto> =
                NetworkResult.Success(savingsGoalsDto)

            // Create accounts Flow
            val accountsFlow = flow {
                emit(savingsGoalsNetworkResult)
            }

            val savingsGoalsResponse = Response.success(savingsGoalsDto)

            coEvery { mockStarlingApiService.getSavingGoals(accountDto.accountUid) } returns savingsGoalsResponse

            var expectedGoalsResponse: NetworkResult<SavingsGoalsDomain>? = null
            accountsFlow.collect {
                expectedGoalsResponse = NetworkResult.Success(it.data?.toSavingsGoalsDomain())
            }

            var sutAccountsResponse: NetworkResult<SavingsGoalsDomain>? = null

            sut.getAllSavingGoals(accountDto.accountUid).also {
                sutAccountsResponse = it
            }
            assertEquals(expectedGoalsResponse?.data?.list, sutAccountsResponse?.data?.list)
        }

    @Test
    fun `addMoneyIntoSavingGoal, returns successful`() =
        runTest {
            val savingsGoalDto = SavingsGoalDto(
                "savingGoalUid",
                "name",
                Amount("GBP", 1000L, 0.0),
                Amount("GBP", 100L, 0.0),
                1,
                SavingGoalsStatus.ACTIVE
            )
            val transferUid = UUID.randomUUID().toString()
            val savingGoalTransferResponseDto = SavingGoalTransferResponseDto(transferUid, true)

            val savingGoalSuccessResponse = Response.success(savingGoalTransferResponseDto)

            val userAccountId = mockUserAccountRepository.getAccountUid()
            val goalUid = savingsGoalDto.savingsGoalUid
            val savingAmount = SavingAmount(SavingGoalAmount("GBP", 1000L))

            coEvery {
                mockStarlingApiService.addMoneyIntoSavingGoal(
                    userAccountId!!,
                    goalUid,
                    transferUid,
                    savingAmount
                )
            } returns savingGoalSuccessResponse

            var sutAddMoneyResponse: NetworkResult<TransferDomain>? = null
            sut.addMoneyIntoSavingGoal(userAccountId!!, goalUid, transferUid, savingAmount)
                .also {
                    sutAddMoneyResponse = it
                }

            assertEquals(transferUid, sutAddMoneyResponse?.data?.transferUid)
            assertEquals(true, sutAddMoneyResponse?.data?.success)
        }

    @Test
    fun `createNewSavingGoal, returns successful`() =
        runTest {
            val userAccountId = "userAccountId"
            val newSavingGoal =
                NewSavingGoal("Trip To New York", "GBP", SavingTarget("GBP", 1234L), "")

            val savingGoalUid = "savingGoalUid"
            val newSavingGoalResponseDto = NewSavingGoalResponseDto(savingGoalUid,"true")

            val response = Response.success(newSavingGoalResponseDto)

            coEvery { mockStarlingApiService.createNewSavingGoal(userAccountId,newSavingGoal) } returns response

            var sutNewSavingGoalResponse: NetworkResult<NewSavingGoalResponseDomain>? = null
            sut.createNewSavingGoal(userAccountId, newSavingGoal).also {
                sutNewSavingGoalResponse = it
            }

            assertEquals(savingGoalUid,sutNewSavingGoalResponse?.data?.savingsGoalUid)
            assertEquals("true",sutNewSavingGoalResponse?.data?.success)
        }
}