package com.challenge.home.presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.challenge.util.CurrencyUnitsMapper
import com.challenge.common.utils.DispatcherProvider
import com.challenge.common.model.NetworkResult
import com.challenge.home.domain.usecase.FetchTransactionsUseCase
import com.challenge.model.transaction.TransactionDomain
import com.challenge.model.transaction.TransactionsDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val fetchTransactionsUseCase: FetchTransactionsUseCase,
    private val currencyUnitsMapper: CurrencyUnitsMapper,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _transactionsList: MutableStateFlow<NetworkResult<TransactionsDomain>> =
        MutableStateFlow(NetworkResult.Loading())
    val transactionsList = _transactionsList.asStateFlow()

    fun fetchTransactions() = viewModelScope.launch(dispatcherProvider.default) {
        _transactionsList.value = fetchTransactionsUseCase.invoke()
    }

    fun getCurrencyInReadable(minorUnitsList: List<TransactionDomain>) =
        currencyUnitsMapper.convertMinorUnitToMajorUnit(minorUnitsList)

    fun getSumOfMinorUnits() =
        _transactionsList.value.data?.let {
            with(currencyUnitsMapper) {
                convertMajorUnitsToMinorUnits(
                    sumUpFractionPartMajorUnit(
                        convertMinorUnitToMajorUnit(
                            it.feedItems
                        )
                    )
                ).toLong()
            }
        }
}