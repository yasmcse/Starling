package com.challenge.savinggoals.extensions

import com.challenge.common.model.Amount
import com.challenge.common.model.newsavingdomain.NewSavingGoalResponseDomain
import com.challenge.common.model.newsavingdto.NewSavingGoalResponseDto
import com.challenge.common.model.savinggoaldomain.SavingsGoalDomain
import com.challenge.common.model.savinggoaldomain.SavingsGoalsDomain
import com.challenge.common.model.savinggoaldomain.TransferDomain
import com.challenge.common.model.savinggoaldto.SavingGoalTransferResponseDto
import com.challenge.common.model.savinggoaldto.SavingGoalsStatus
import com.challenge.common.model.savinggoaldto.SavingsGoalDto
import com.challenge.common.model.savinggoaldto.SavingsGoalsDto
import com.challenge.repository.savinggoals.toNewSavingGoalResponseDomain
import com.challenge.repository.savinggoals.toSavingGoalTransferDomain
import com.challenge.repository.savinggoals.toSavingsGoalsDomain
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SavingsGoalsExtensionsTest {
    @Test
    fun `map savingsGoalsDto to SavingsGoalsDomain`() {
        val savingsGoalsDto = SavingsGoalsDto(
            listOf(
                SavingsGoalDto(
                    "savingsGoalUid",
                    "name", Amount("GBP", 1234L, 0.0),
                    Amount("GBP", 123L, 0.0),
                    10,
                    SavingGoalsStatus.ACTIVE
                )
            )
        )

        val savingsGoalsDomain = SavingsGoalsDomain(
            listOf(
                SavingsGoalDomain(
                    "savingsGoalUid",
                    "name", Amount("GBP", 1234L, 0.0),
                    Amount("GBP", 123L, 0.0)
                )
            )
        )

        assertEquals(savingsGoalsDomain,savingsGoalsDto.toSavingsGoalsDomain())
    }

    @Test
    fun `map SavingGoalTransferResponseDto to TransferDomain`(){
        val savingGoalTransferResponseDto = SavingGoalTransferResponseDto("transferUid",true)
        val transferDomain = TransferDomain("transferUid",true)

        assertEquals(transferDomain,savingGoalTransferResponseDto.toSavingGoalTransferDomain())
    }

    @Test
    fun `map NewSavingGoalResponseDto to NewSavingGoalResponseDomain`(){
        val newSavingGoalTransferResponseDto = NewSavingGoalResponseDto("savingsGoalUid","true")
        val newSavingsGoalDomain = NewSavingGoalResponseDomain("savingsGoalUid","true")

        assertEquals(newSavingsGoalDomain,newSavingGoalTransferResponseDto.toNewSavingGoalResponseDomain())
    }
}