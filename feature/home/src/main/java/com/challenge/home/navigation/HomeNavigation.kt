package com.challenge.home.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.challenge.common.enums.Screens
import com.challenge.common.utils.NetworkStatus
import com.challenge.home.ui.HomeScreen
import com.challenge.home.ui.HomeViewModel

@SuppressLint("VisibleForTests")
fun NavGraphBuilder.homeScreen(
    networkStatus: NetworkStatus,
    roundUpSum: (Long) -> Unit
) {
    composable(Screens.HOME.name) {
        val viewModel: HomeViewModel = hiltViewModel()
        val transactionsState by viewModel.transactionsList.collectAsStateWithLifecycle()

        LaunchedEffect(key1 = "key1") {
            viewModel.fetchTransactions()
        }

        HomeScreen(
            transactionsState,
            viewModel,
            networkStatus
        )
        {
            roundUpSum(it)
        }
    }
}

fun NavController.navigateToHomeScreen() {
    this.navigate(
        Screens.HOME.name
    )
    {
        this.popUpTo(graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}