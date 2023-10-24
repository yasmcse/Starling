package com.challenge.savingsgoals

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.challenge.common.utils.MainCoroutineRule
import com.challenge.di.NetworkResult
import com.challenge.domain.CreateNewSavingGoalUseCase
import com.challenge.model.NewSavingGoal
import com.challenge.model.NewSavingGoalResponse
import com.challenge.model.SavingTarget
import com.challenge.savingsgoals.mapper.NewSavingGoalMapper
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

    lateinit var sut: AddNewGoalViewModel

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
            val newSavingGoalResponse = NewSavingGoalResponse("savingsGoalUid", "true")

            coEvery { mockNewSavingGoalMapper.map("France", "GBP", 1234) } returns newSavingGoal

            val networkResult: NetworkResult<NewSavingGoalResponse> =
                NetworkResult.Success(newSavingGoalResponse)

            val createNewFlow = flow {
                emit(networkResult)
            }
            coEvery { mockCreateNewSavingGoalUseCase.createNewSavingGoal(newSavingGoal) } returns createNewFlow

            var expectedResponse: NewSavingGoalResponse? = null
            createNewFlow.collect {
                expectedResponse = it.data
            }

            sut.postNewSavingGoal("France","GBP",1234)

            assertEquals("savingsGoalUid", sut.response.value.data?.savingsGoalUid)
            assertEquals("true", sut.response.value.data?.success)
        }
    }
}