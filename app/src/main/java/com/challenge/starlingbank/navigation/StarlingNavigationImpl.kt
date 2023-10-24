package com.challenge.starlingbank.navigation

import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.challenge.common.StarlingNavigator
import com.challenge.home.HomeFragmentDirections
import com.challenge.savingsgoals.SavingGoalsFragmentDirections


import javax.inject.Inject

class StarlingNavigationImpl @Inject constructor() : StarlingNavigator {

    override fun Fragment.navigateToHomeScreen() {
        navigate(HomeFragmentDirections.actionToHomeFragment())
    }

    override fun Fragment.navigateToSavingsGoalsScreen(sumUp: Long) {
      navigate(SavingGoalsFragmentDirections.actionToSavingGoalsFragment(sumUp))
    }


    override fun Fragment.navigateToAddNewSavingGoalScreen(sumUp: Long) {
        navigate(SavingGoalsFragmentDirections.actionToAddNewSavingGoal(sumUp))
    }

    override fun Fragment.goBack() {
        findNavController().popBackStack()
    }

    private fun Fragment.navigate(navDirections: NavDirections) {
        findNavController().navigate(navDirections)
    }
}