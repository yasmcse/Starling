package com.challenge.savingsgoals.domain.usecase

import com.challenge.common.UserAccountRepository
import com.challenge.common.model.NetworkResult
import com.challenge.mapper.savinggoal.model.SavingsGoalDomain
import com.challenge.mapper.transaction.model.TransferDomain
import com.challenge.repositorycontract.SavingsGoalsRepository
import com.challenge.savingsgoals.mapper.CurrencyAndAmountMapper
import java.util.UUID
import javax.inject.Inject

class AddMoneyIntoSavingsGoalUseCase @Inject constructor(
    private val userAccountRepository: UserAccountRepository,
    private val currencyAndAmountMapper: CurrencyAndAmountMapper,
    private val savingGoalsRepository: SavingsGoalsRepository
) {

    suspend operator fun invoke(
        roundUpSum: Long,
        savingsGoalDomain: SavingsGoalDomain?
    ): NetworkResult<TransferDomain> {
        val accountUid = userAccountRepository.getAccountUid() ?: return NetworkResult.Error(
            null,
            "Account UID not found"
        )
        val savingAmount = savingsGoalDomain?.target?.currency?.let {
            currencyAndAmountMapper.map(it, roundUpSum)
        } ?: return NetworkResult.Error(null, "Currency null")

        savingsGoalDomain.savingsGoalUid.let { goalUid ->
            val transactionUid = UUID.randomUUID().toString()
            val result = savingGoalsRepository.addMoneyIntoSavingGoal(
                accountUid, goalUid, transactionUid, savingAmount
            )
            return result
        }
    }
}