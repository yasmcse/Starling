package com.challenge.starlingbank.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.challenge.common.enums.Screens
import com.challenge.common.utils.NetworkStatus
import com.challenge.home.navigation.homeScreen
import com.challenge.navigation.addNewGoal
import com.challenge.navigation.savingGoalsScreen

@Composable
fun NavigationHost(
    navController: NavController,
    networkStatus: NetworkStatus,
    onRoundUpSum: (Long) -> Unit
) {
    val navHostController = navController as NavHostController
    NavHost(
        navController = navHostController,
        startDestination = Screens.HOME.name
    ) {
        homeScreen(networkStatus) {
            onRoundUpSum(it)
        }
        savingGoalsScreen(networkStatus)
        addNewGoal(networkStatus,navHostController)
    }
}