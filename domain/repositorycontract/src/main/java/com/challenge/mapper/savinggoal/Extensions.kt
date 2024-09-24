package com.challenge.mapper.savinggoal

import com.challenge.mapper.savinggoal.model.NewSavingGoalResponseDomain
import com.challenge.common.model.newsavingdto.NewSavingGoalResponseDto
import com.challenge.mapper.savinggoal.model.SavingsGoalDomain
import com.challenge.mapper.savinggoal.model.SavingsGoalsDomain
import com.challenge.mapper.transaction.model.TransferDomain
import com.challenge.common.model.savinggoaldto.SavingGoalTransferResponseDto
import com.challenge.common.model.savinggoaldto.SavingsGoalsDto

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
    return SavingsGoalsDomain(savingGoals.toList())
}

fun SavingGoalTransferResponseDto.toSavingGoalTransferDomain(): TransferDomain =
    TransferDomain(transferUid, success)

fun NewSavingGoalResponseDto.toNewSavingGoalResponseDomain() =
    NewSavingGoalResponseDomain(savingsGoalUid, success)