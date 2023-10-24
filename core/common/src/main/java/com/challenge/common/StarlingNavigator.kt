package com.challenge.common

import androidx.fragment.app.Fragment

interface StarlingNavigator {
    fun Fragment.navigateToHomeScreen()
    fun Fragment.navigateToSavingsGoalsScreen(sumUp:Long)
    fun Fragment.navigateToAddNewSavingGoalScreen(sumUp: Long)
    fun Fragment.goBack()
}