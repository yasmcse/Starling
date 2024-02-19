package com.challenge.common.model.savinggoaldto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SavingGoals(private val savingsGoalList: List<Saving>):Parcelable