package com.challenge.model

import com.google.gson.annotations.SerializedName

data class SavingsGoals(
    @SerializedName("savingsGoalList") val savingsGoals: List<SavingsGoal>
)