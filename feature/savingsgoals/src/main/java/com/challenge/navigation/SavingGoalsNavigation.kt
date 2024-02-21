package com.challenge.navigation


import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.challenge.common.enums.Screens
import com.challenge.common.utils.NetworkStatus
import com.challenge.ui.SavingGoalsViewModel
import com.challenge.ui.SavingGoalsScreen

fun NavGraphBuilder.savingGoalsScreen(
    networkStatus: NetworkStatus
) {
    composable(
        Screens.SAVING_GOALS.name + "/{roundUpSum}"
    ) { navBackStack ->
        val roundUpSum = navBackStack.arguments?.getString("roundUpSum")

        val viewModel: SavingGoalsViewModel = hiltViewModel()
        val savingsGoalsState by viewModel.savingsGoalsList.collectAsStateWithLifecycle()

        LaunchedEffect(key1 = "key1") {
            viewModel.fetchSavingsGoals()
        }
        SavingGoalsScreen(
            savingsGoalsState, viewModel, networkStatus, roundUpSum?.toLong()
        )
    }
}

fun NavController.navigateToSavingGoalsScreen(roundUpSum: Long) {
    this.navigate(
        Screens.SAVING_GOALS.name + "/{roundUpSum}"
            .replace(
                oldValue = "{roundUpSum}",
                newValue = roundUpSum.toString()
            )
    ) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        this.popUpTo(graph.startDestinationId) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when re-selecting
        launchSingleTop = true
        restoreState = true
    }
}