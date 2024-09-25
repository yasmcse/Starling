package com.challenge.home.domain.usecase

import com.challenge.common.UserAccountRepository
import com.challenge.common.model.NetworkResult
import com.challenge.common.model.accountDto.UserAccount
import com.challenge.model.transaction.TransactionsDomain
import com.challenge.repositorycontract.AccountRepository
import com.challenge.repositorycontract.TransactionsRepository
import javax.inject.Inject

class FetchTransactionsUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionsRepository,
    private val userAccountRepository: UserAccountRepository
) {
    suspend operator fun invoke(): NetworkResult<TransactionsDomain> {
            val userAccounts = accountRepository.getAccounts()
            return when {
                userAccounts.data?.accounts?.isNotEmpty() == true -> {
                    val account = userAccounts.data?.accounts?.getOrNull(0)
                    if (account != null) {
                        val userAccount = UserAccount(
                            accountUid = account.accountUid,
                            categoryUid = account.defaultCategory,
                            minTransactionTimeStamp = minTransactionTimeStamp,
                            maxTransactionTimeStamp = maxTransactionTimeStamp
                        )

                        // Fetch transactions for the user account
                        transactionRepository.getTransactionsBetween(userAccount).also {
                            // Save user Account info in memory cache
                            saveUserAccount(userAccount)
                        }
                    } else {
                        NetworkResult.Error(null, "No account found")
                    }
                }
                else -> {
                    // Handle the case where accounts data is null or empty
                    NetworkResult.Error(userAccounts.code, userAccounts.message ?: "Unknown error")
                }
            }
        }

    fun saveUserAccount(userAccount: UserAccount) {
        with(userAccountRepository) {
            setAccountId(userAccount.accountUid)
            setCategoryUid(userAccount.categoryUid)
        }
    }
    // Change the dates as you like for testing purposes
    companion object {
        const val minTransactionTimeStamp = "2024-09-23T09:34:56.000Z"
        const val maxTransactionTimeStamp = "2024-09-27T20:22:56.000Z"
    }
}