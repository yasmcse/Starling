package com.challenge.starlingbank.ui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.rememberNavController
import com.challenge.common.enums.Screens
import com.challenge.common.utils.NetworkStatus
import com.challenge.designsystem.component.AddGoalFloatingActionButton
import com.challenge.designsystem.component.BottomNav
import com.challenge.designsystem.component.Drawer
import com.challenge.designsystem.component.TopBar
import com.challenge.home.navigation.navigateToHomeScreen
import com.challenge.navigation.navigateToAddNewGoalScreen
import com.challenge.navigation.navigateToSavingGoalsScreen
import com.challenge.savingsgoals.R.*
import com.challenge.starlingbank.navigation.NavigationHost
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AppScaffold(networkStatus: NetworkStatus) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var selectedScreen by remember { mutableStateOf(Screens.HOME) }
    var roundUpSum = 0L

    ModalDrawer(
        drawerState = drawerState,
        drawerContent =
        {
            Drawer { screen ->
                with(drawerState) {
                    coroutineScope.launch {
                        if (isOpen) close()
                        selectedScreen = screen
                        when (selectedScreen) {
                            Screens.HOME -> {
                                navController.navigateToHomeScreen()
                            }

                            Screens.SAVING_GOALS -> {
                                navController.navigateToSavingGoalsScreen(roundUpSum)
                            }

                            Screens.ADD_NEW_GOAL -> {
                                navController.navigateToAddNewGoalScreen()
                            }
                        }
                    }
                }
            }
        }
    )
    {
        Scaffold(
            topBar = {
                TopBar(
                    onBackPresses = {
                        navController.navigateUp()
                    },
                    onMenuClick = {
                        coroutineScope.launch {
                            drawerState.apply {
                                if (isOpen) close() else open()
                            }
                        }
                    }
                )
            },
            bottomBar = {
                BottomNav(selectedScreen) { screen ->
                    selectedScreen = screen
                    when (selectedScreen) {
                        Screens.HOME -> {
                            navController.navigateToHomeScreen()
                        }

                        Screens.SAVING_GOALS -> {
                            navController.navigateToSavingGoalsScreen(roundUpSum)
                        }

                        Screens.ADD_NEW_GOAL -> {
                            navController.navigateToAddNewGoalScreen()
                        }
                    }
                }
            },
            floatingActionButton = {
                AnimatedVisibility(
                    visible = selectedScreen == Screens.SAVING_GOALS,
                    enter = fadeIn(initialAlpha = 0.5f),
                    exit = fadeOut(
                        animationSpec = TweenSpec(durationMillis = 500)
                    )

                ) {
                    AddGoalFloatingActionButton(
                        stringResource(
                            string.add_new_goal
                        )
                    ) {
                        navController.navigate(Screens.ADD_NEW_GOAL.name)
                    }
                }
            }
        ) { _ ->
            NavigationHost(
                navController = navController,
                networkStatus = networkStatus,
            )
            { sum ->
                roundUpSum = sum
            }
        }
    }
}

