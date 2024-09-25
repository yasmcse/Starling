package com.challenge.savingsgoals.presentation

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.challenge.util.CurrencyUnitsMapper
import com.challenge.common.model.NetworkResult
import com.challenge.model.savinggoal.SavingsGoalDomain
import com.challenge.model.savinggoal.SavingsGoalsDomain
import com.challenge.model.transaction.TransferDomain
import com.challenge.savingsgoals.domain.usecase.AddMoneyIntoSavingsGoalUseCase
import com.challenge.savingsgoals.domain.usecase.FetchSavingGoalsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavingGoalsViewModel @Inject constructor(
    private val addMoneyIntoSavingsGoalUseCase: AddMoneyIntoSavingsGoalUseCase,
    private val currencyUnitsMapper: CurrencyUnitsMapper,
    private val fetchSavingGoalsUseCase: FetchSavingGoalsUseCase,

    ) : ViewModel() {

    private val _savingsGoalsList: MutableStateFlow<NetworkResult<SavingsGoalsDomain>> =
        MutableStateFlow(NetworkResult.Loading())
    val savingsGoalsList = _savingsGoalsList.asStateFlow()

    private val _savingGoalPosted: MutableStateFlow<NetworkResult<TransferDomain>> =
        MutableStateFlow(NetworkResult.Loading())
    val savingGoalPosted = _savingGoalPosted
        .asStateFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NetworkResult.Loading()
        )

    fun fetchSavingsGoals() = viewModelScope.launch {
        fetchSavingGoalsUseCase.invoke().also {
            _savingsGoalsList.value = it
        }
    }

    fun addMoneyIntoSavingGoals(roundUpSum: Long, savingsGoalDomain: SavingsGoalDomain?) =
        viewModelScope.launch {
            addMoneyIntoSavingsGoalUseCase.invoke(roundUpSum, savingsGoalDomain)
        }

    @SuppressLint("VisibleForTests")
    fun getCurrencyInReadable(minorUnitsList: List<SavingsGoalDomain>) =
        currencyUnitsMapper.convertSavingGoalUnits(minorUnitsList)
}