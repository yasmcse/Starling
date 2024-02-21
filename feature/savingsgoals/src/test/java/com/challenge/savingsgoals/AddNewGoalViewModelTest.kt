package com.challenge.savingsgoals

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.challenge.common.utils.MainCoroutineRule
import com.challenge.common.NetworkResult
import com.challenge.common.model.newsavingdomain.NewSavingGoalResponseDomain
import com.challenge.usecase.CreateNewSavingGoalUseCase
import com.challenge.common.model.newsavingdto.NewSavingGoal
import com.challenge.common.model.savinggoaldto.SavingTarget
import com.challenge.savingsgoals.mapper.NewSavingGoalMapper
import com.challenge.ui.AddNewGoalViewModel
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
class AddNewGoalViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mockCreateNewSavingGoalUseCase = mockk<CreateNewSavingGoalUseCase>()
    private val mockNewSavingGoalMapper = mockk<NewSavingGoalMapper>()

    private lateinit var sut: AddNewGoalViewModel

    @Before
    fun setUp() {
        sut = AddNewGoalViewModel(
            mockCreateNewSavingGoalUseCase,
            mockNewSavingGoalMapper
        )
    }

    @Test
    fun `Given when postSavingGoal is called returns the success response`() {

        runTest {
            val newSavingGoal = NewSavingGoal("France", "GBP", SavingTarget("GBP", 1234), null)
            val newSavingGoalResponseDomain = NewSavingGoalResponseDomain("savingsGoalUid", "true")

            coEvery { mockNewSavingGoalMapper.map("France", "GBP", 1234) } returns newSavingGoal

            val networkResult: NetworkResult<NewSavingGoalResponseDomain> =
                NetworkResult.Success(newSavingGoalResponseDomain)

            val createNewFlow = flow {
                emit(networkResult)
            }
            coEvery { mockCreateNewSavingGoalUseCase.createNewSavingGoal(newSavingGoal) } returns createNewFlow

            sut.postNewSavingGoal("France","GBP",1234)

            assertEquals("savingsGoalUid", sut.newGoal.value.data?.savingsGoalUid)
            assertEquals("true", sut.newGoal.value.data?.success)
        }
    }

    @Test
    fun `Given when postSavingGoal is called returns error`() {

        runTest {
            val newSavingGoal = NewSavingGoal("France", "GBP", SavingTarget("GBP", 1234), null)
            val newSavingGoalResponseDomain = NewSavingGoalResponseDomain("savingsGoalUid", "true")

            coEvery { mockNewSavingGoalMapper.map("France", "GBP", 1234) } returns newSavingGoal

            val networkResult: NetworkResult<NewSavingGoalResponseDomain> =
                NetworkResult.Error(401,"Token Expired")

            val createNewFlow = flow {
                emit(networkResult)
            }

            var expectedResponse:NewSavingGoalResponseDomain? = null
            createNewFlow.collect{
                expectedResponse = it.data
            }
            coEvery { mockCreateNewSavingGoalUseCase.createNewSavingGoal(newSavingGoal) } returns createNewFlow

            sut.postNewSavingGoal("France","GBP",1234)

            assertEquals(expectedResponse, sut.newGoal.value.data)
        }
    }
}