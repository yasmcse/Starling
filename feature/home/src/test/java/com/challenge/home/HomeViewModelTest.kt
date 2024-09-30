package com.challenge.home

import com.challenge.common.model.NetworkResult
import com.challenge.common.utils.DispatcherProvider
import com.challenge.home.domain.usecase.FetchTransactionsUseCase
import com.challenge.home.presentation.HomeViewModel
import com.challenge.model.transaction.TransactionDomain
import com.challenge.model.transaction.TransactionsDomain
import com.challenge.util.CurrencyUnitsMapper
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    private lateinit var fetchTransactionsUseCase: FetchTransactionsUseCase
    private lateinit var currencyUnitsMapper: CurrencyUnitsMapper
    private lateinit var dispatcherProvider: DispatcherProvider

    private lateinit var homeViewModel: HomeViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fetchTransactionsUseCase = mockk()
        currencyUnitsMapper = mockk()
        dispatcherProvider = mockk()

        homeViewModel = HomeViewModel(fetchTransactionsUseCase, currencyUnitsMapper, dispatcherProvider)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchTransactions updates state with fetched data`() = runTest() {
        val mockTransactions = NetworkResult.Success(TransactionsDomain(feedItems = listOf()))
        coEvery { fetchTransactionsUseCase.invoke() } returns mockTransactions

        val job = launch {
            homeViewModel.transactionsList.collect { }
        }

        homeViewModel.fetchTransactions()

        advanceUntilIdle()

        // Assert
        assertEquals(mockTransactions, homeViewModel.transactionsList.first())
        coVerify(exactly = 1) { fetchTransactionsUseCase.invoke() }

        // Clean up
        job.cancel()
    }


    @Test
    fun `getCurrencyInReadable calls currencyUnitsMapper with correct parameters`() {

        val transactionList = listOf<TransactionDomain>()
        every { currencyUnitsMapper.convertMinorUnitToMajorUnit(transactionList) } returns listOf()

        homeViewModel.getCurrencyInReadable(transactionList)

        // Assert
        verify(exactly = 1) { currencyUnitsMapper.convertMinorUnitToMajorUnit(transactionList) }
    }

    @Test
    fun `fetchTransactions sets Loading state before fetching`() = runTest() {
        coEvery { fetchTransactionsUseCase.invoke() } returns NetworkResult.Success(TransactionsDomain(feedItems = listOf()))

        val collectJob = launch {
            homeViewModel.transactionsList.collect {}
        }

        assertTrue(homeViewModel.transactionsList.first() is NetworkResult.Loading)
        collectJob.cancel()
    }
}