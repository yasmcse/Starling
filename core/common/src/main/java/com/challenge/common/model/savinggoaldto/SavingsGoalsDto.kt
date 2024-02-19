package com.challenge.common.model.savinggoaldto

import com.google.gson.annotations.SerializedName

data class SavingsGoalsDto(
    @SerializedName("savingsGoalList") val savingsGoalDtos: List<SavingsGoalDto>
)