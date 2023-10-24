package com.challenge.savingsgoals

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.challenge.common.utils.CurrencyUnitsMapper
import com.challenge.di.NetworkResult
import com.challenge.model.SavingGoalTransferResponse
import com.challenge.model.SavingsGoal
import com.challenge.model.SavingsGoals
import com.challenge.repository.UserAccountRepository
import com.challenge.repository.savinggoals.SavingGoalsRepository
import com.challenge.savingsgoals.mapper.CurrencyAndAmountMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SavingGoalsViewModel @Inject constructor(
    private val savingGoalsRepository: SavingGoalsRepository,
    private val userAccountRepository: UserAccountRepository,
    private val currencyUnitsMapper: CurrencyUnitsMapper,
    private val currencyAndAmountMapper: CurrencyAndAmountMapper
) : ViewModel() {

    private val _savingsGoalsList: MutableStateFlow<NetworkResult<SavingsGoals>> =
        MutableStateFlow(NetworkResult.Loading())
    val savingsGoalsList = _savingsGoalsList

    private val _savingGoalPosted: MutableStateFlow<NetworkResult<SavingGoalTransferResponse>> =
        MutableStateFlow(NetworkResult.Loading())
    val savingGoalPosted = _savingGoalPosted


    @VisibleForTesting
    fun fetchSavingsGoals() = viewModelScope.launch {
        userAccountRepository.getAccountUid()?.let { accountUid ->
            savingGoalsRepository
                .getAllSavingGoals(accountUid)
                .collect {
                    _savingsGoalsList.value = it
                }
        }
    }

    @VisibleForTesting
    fun addMoneyIntoSavingGoals(roundUpSum: Long, savingsGoal: SavingsGoal?) {

        val accountUid = userAccountRepository.getAccountUid()!!
        val savingAmount =
            savingsGoal?.target?.currency?.let { currencyAndAmountMapper.map(it, roundUpSum) }

        viewModelScope.launch {
            savingGoalsRepository.addMoneyIntoSavingGoal(
                accountUid,
                savingsGoal?.savingsGoalUid!!,
                UUID.randomUUID().toString(),
                savingAmount!!
            ).collect {
                _savingGoalPosted.value = it
            }
        }
    }


    @VisibleForTesting
    fun getCurrencyInReadable(minorUnitsList: List<SavingsGoal>) =
        currencyUnitsMapper.convertSavingGoalUnits(minorUnitsList)
}