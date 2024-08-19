package com.challenge.home.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.challenge.common.utils.CurrencyUnitsMapper
import com.challenge.common.utils.DispatcherProvider
import com.challenge.common.NetworkResult
import com.challenge.common.UserAccountRepository
import com.challenge.common.model.accountDto.UserAccount
import com.challenge.common.model.transactiondomain.TransactionDomain
import com.challenge.common.model.transactiondomain.TransactionsDomain
import com.challenge.repositorycontract.AccountRepository
import com.challenge.repositorycontract.TransactionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionsRepository,
    private val userAccountRepository: UserAccountRepository,
    private val currencyUnitsMapper: CurrencyUnitsMapper,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _transactionsList: MutableStateFlow<NetworkResult<TransactionsDomain>> =
        MutableStateFlow(NetworkResult.Loading())
    val transactionsList = _transactionsList.asStateFlow()

    fun fetchTransactions() = viewModelScope.launch(dispatcherProvider.default) {
        accountRepository.getAccounts().collect { userAccounts ->
            when {
                userAccounts.data?.accounts?.isNotEmpty() == true -> {
                    with(userAccounts) {
                        data?.accounts?.get(0).let { account ->
                            account?.let { account1 ->
                                UserAccount(
                                    data?.accounts!![0].accountUid,
                                    account1.defaultCategory,
                                    minTransactionTimeStamp,
                                    maxTransactionTimeStamp
                                )
                            }
                        }?.let { userAccount ->
                            transactionRepository.getTransactionsBetween(userAccount)
                                .collect {
                                    // Save user Account info in memory cache
                                    saveUserAccount(userAccount)
                                    _transactionsList.value = it
                                }
                        }
                    }
                }
                else -> {
                    _transactionsList.value = NetworkResult.Error(userAccounts.code,userAccounts.message)
                }
            }
        }
    }

    fun saveUserAccount(userAccount: UserAccount) {
        with(userAccountRepository) {
            setAccountId(userAccount.accountUid)
            setCategoryUid(userAccount.categoryUid)
        }
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

    // Change the dates as you like for testing purposes
    companion object {
        const val minTransactionTimeStamp = "2024-08-19T09:34:56.000Z"
        const val maxTransactionTimeStamp = "2024-08-25T20:22:56.000Z"
    }
}