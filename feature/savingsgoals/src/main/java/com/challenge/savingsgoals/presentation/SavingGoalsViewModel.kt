package com.challenge.savingsgoals.presentation

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.challenge.util.CurrencyUnitsMapper
import com.challenge.common.UserAccountRepository
import com.challenge.common.model.NetworkResult
import com.challenge.mapper.savinggoal.model.SavingsGoalDomain
import com.challenge.mapper.savinggoal.model.SavingsGoalsDomain
import com.challenge.mapper.transaction.model.TransferDomain
import com.challenge.repositorycontract.SavingsGoalsRepository
import com.challenge.savingsgoals.mapper.CurrencyAndAmountMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SavingGoalsViewModel @Inject constructor(
    private val savingGoalsRepository: SavingsGoalsRepository,
    private val userAccountRepository: UserAccountRepository,
    private val currencyUnitsMapper: CurrencyUnitsMapper,
    private val currencyAndAmountMapper: CurrencyAndAmountMapper
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
        userAccountRepository.getAccountUid()?.let { accountUid ->
            savingGoalsRepository
                .getAllSavingGoals(accountUid)
                .also {
                    _savingsGoalsList.value = it
                }
        }
    }

    fun addMoneyIntoSavingGoals(roundUpSum: Long, savingsGoalDomain: SavingsGoalDomain?) {

        val accountUid = userAccountRepository.getAccountUid()
        val savingAmount =
            savingsGoalDomain?.target?.currency?.let { currencyAndAmountMapper.map(it, roundUpSum) }

        viewModelScope.launch {
            accountUid?.let { uid ->
                savingGoalsRepository.addMoneyIntoSavingGoal(
                    uid,
                    savingsGoalDomain?.savingsGoalUid!!,
                    UUID.randomUUID().toString(),
                    savingAmount!!
                ).also {
                    _savingGoalPosted.value = it
                }
            }
        }
    }

    @SuppressLint("VisibleForTests")
    fun getCurrencyInReadable(minorUnitsList: List<SavingsGoalDomain>) =
        currencyUnitsMapper.convertSavingGoalUnits(minorUnitsList)
}