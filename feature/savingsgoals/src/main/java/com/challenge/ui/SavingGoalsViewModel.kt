package com.challenge.ui

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.challenge.common.utils.CurrencyUnitsMapper
import com.challenge.common.NetworkResult
import com.challenge.common.UserAccountRepository
import com.challenge.common.model.savinggoaldomain.SavingsGoalDomain
import com.challenge.common.model.savinggoaldomain.SavingsGoalsDomain
import com.challenge.common.model.savinggoaldomain.TransferDomain
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
                .collect {
                    _savingsGoalsList.value = it
                }
        }
    }

    fun addMoneyIntoSavingGoals(roundUpSum: Long, savingsGoalDto: SavingsGoalDomain?) {

        val accountUid = userAccountRepository.getAccountUid()
        val savingAmount =
            savingsGoalDto?.target?.currency?.let { currencyAndAmountMapper.map(it, roundUpSum) }

        viewModelScope.launch {
            accountUid?.let { uid ->
                savingGoalsRepository.addMoneyIntoSavingGoal(
                    uid,
                    savingsGoalDto?.savingsGoalUid!!,
                    UUID.randomUUID().toString(),
                    savingAmount!!
                ).collect {
                    _savingGoalPosted.value = it
                }
            }
        }
    }

    @SuppressLint("VisibleForTests")
    fun getCurrencyInReadable(minorUnitsList: List<SavingsGoalDomain>) =
        currencyUnitsMapper.convertSavingGoalUnits(minorUnitsList)
}