package com.challenge.home


import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.challenge.common.utils.CurrencyUnitsMapper
import com.challenge.di.NetworkResult
import com.challenge.domain.GetAccountsUseCase
import com.challenge.domain.GetTransactionsBetweenDatesUseCase
import com.challenge.model.Transaction
import com.challenge.model.Transactions
import com.challenge.repository.UserAccount
import com.challenge.repository.UserAccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAccountUseCase: GetAccountsUseCase,
    private val getTransactionsBetweenDatesUseCase: GetTransactionsBetweenDatesUseCase,
    private val userAccountRepository: UserAccountRepository,
    private val currencyUnitsMapper: CurrencyUnitsMapper
) : ViewModel() {

    private val _transactionsList: MutableStateFlow<NetworkResult<Transactions>> =
        MutableStateFlow(NetworkResult.Loading())
    val transactionsList = _transactionsList


    @VisibleForTesting
    fun fetchTransactions() = viewModelScope.launch() {
        getAccountUseCase.getAccounts().collect { userAccount ->
            if (userAccount.data?.accounts?.isNotEmpty() == true) {
                with(userAccount) {
                    data?.accounts?.get(0).let {
                        it?.let { it1 ->
                            UserAccount(
                                data?.accounts!![0].accountUid,
                                it1.defaultCategory,
                                minTransactionTimeStamp,
                                maxTransactionTimeStamp
                            )
                        }
                    }?.let { userAccount ->
                        getTransactionsBetweenDatesUseCase.getTransactions(userAccount)
                            .collect {
                                // Save user Account info in memory cache
                                saveUserAccount(userAccount)
                                _transactionsList.value = it
                            }
                    }
                }
            }
        }
    }

    @VisibleForTesting
    fun saveUserAccount(userAccount: UserAccount) {
        with(userAccountRepository) {
            setAccountId(userAccount.accountUid)
            setCategoryUid(userAccount.categoryUid)
        }
    }

    @VisibleForTesting
    fun getCurrencyInReadable(minorUnitsList: List<Transaction>) =
        currencyUnitsMapper.convertMinorUnitToMajorUnit(minorUnitsList)

    @VisibleForTesting
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

    // Change the dates as you like for testing purposes
    companion object {
        const val minTransactionTimeStamp = "2023-10-16T09:34:56.000Z"
        const val maxTransactionTimeStamp = "2023-10-22T20:22:56.000Z"
    }
}