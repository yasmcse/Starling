package com.challenge.savingsgoals.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.challenge.common.enums.Screens
import com.challenge.common.utils.NetworkStatus
import com.challenge.savingsgoals.presentation.AddNewGoalScreen
import com.challenge.savingsgoals.presentation.AddNewGoalViewModel

fun NavGraphBuilder.addNewGoal(
    networkStatus: NetworkStatus,
    navController: NavHostController
) {
    composable(Screens.ADD_NEW_GOAL.name) {
        val viewModel: AddNewGoalViewModel = hiltViewModel()
        val newGoalModelState by viewModel.newGoal.collectAsStateWithLifecycle()
        AddNewGoalScreen(newGoalModelState, viewModel, networkStatus
        ) { newGoalCreated ->
            if (newGoalCreated) {
                navController.navigateUp()
            }
        }
    }
}

fun NavController.navigateToAddNewGoalScreen() {
    this.navigate(Screens.ADD_NEW_GOAL.name)
        {
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