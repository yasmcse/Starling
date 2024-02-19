package com.challenge.repository.savinggoals

import com.challenge.common.model.newsavingdomain.NewSavingGoalResponseDomain
import com.challenge.common.model.newsavingdto.NewSavingGoalResponseDto
import com.challenge.common.model.savinggoaldomain.SavingsGoalDomain
import com.challenge.common.model.savinggoaldomain.SavingsGoalsDomain
import com.challenge.common.model.savinggoaldomain.TransferDomain
import com.challenge.common.model.savinggoaldto.SavingGoalTransferResponseDto
import com.challenge.common.model.savinggoaldto.SavingsGoalsDto
import okhttp3.internal.toImmutableList

fun SavingsGoalsDto.toSavingsGoalsDomain(): SavingsGoalsDomain {
    val savingGoals = mutableListOf<SavingsGoalDomain>()
    for (savingGoal in this.savingsGoalDtos) {
        savingGoals.add(
            SavingsGoalDomain(
                savingGoal.savingsGoalUid,
                savingGoal.name,
                savingGoal.target,
                savingGoal.totalSaved
            )
        )
    }
    return SavingsGoalsDomain(savingGoals.toImmutableList())
}

fun SavingGoalTransferResponseDto.toSavingGoalTransferDomain(): TransferDomain =
    TransferDomain(transferUid, success)

fun NewSavingGoalResponseDto.toNewSavingGoalResponseDomain() =
    NewSavingGoalResponseDomain(savingsGoalUid, success)