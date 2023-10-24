package com.challenge.savingsgoals

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.navArgs
import com.challenge.designsystem.component.BottomNav
import com.challenge.designsystem.component.BottomSheet
import com.challenge.common.StarlingNavigator
import com.challenge.designsystem.component.Drawer
import com.challenge.designsystem.component.TopBar
import com.challenge.common.enums.Screens
import com.challenge.common.utils.NetworkStatus
import com.challenge.designsystem.component.AddGoalFloatingActionButton
import com.challenge.designsystem.component.CircularProgressComposable
import com.challenge.designsystem.theme.StarlingBankAppTheme
import com.challenge.designsystem.theme.WhiteSmoke
import com.challenge.di.NetworkResult
import com.challenge.model.SavingGoals
import com.challenge.model.SavingsGoal
import com.challenge.model.SavingsGoals
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SavingGoalsFragment : Fragment() {

    @Inject
    lateinit var starlingNavigator: StarlingNavigator

    @Inject
    lateinit var networkStatus: NetworkStatus

    private val viewModel by viewModels<SavingGoalsViewModel>()
    private val args: SavingGoalsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                StarlingBankAppTheme {
                    Scaffold()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleDeviceBackButton()
    }


    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun Scaffold() {
        val scaffoldState = rememberScaffoldState()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val coroutineScope = rememberCoroutineScope()
        val selectedScreens by remember { mutableStateOf(Screens.values().first()) }


        ModalDrawer(drawerState = drawerState, drawerContent = {
            Drawer { screen ->
                when (screen) {
                    Screens.HOME -> {
                        with(drawerState) {
                            with(starlingNavigator) {
                                coroutineScope.launch {
                                    if (isOpen) close()
                                    navigateToHomeScreen()
                                }
                            }
                        }
                    }

                    Screens.SAVING_GOALS -> {
                        with(drawerState) {
                            with(starlingNavigator) {
                                coroutineScope.launch {
                                    if (isOpen) close()
                                    navigateToSavingsGoalsScreen(args.savingGoals)
                                }
                            }
                        }
                    }
                }
            }
        }) {
            Scaffold(scaffoldState = scaffoldState, topBar = {
                TopBar(onBackPresses = {
                    with(starlingNavigator) {
                        navigateToHomeScreen()
                    }
                }, onMenuClick = {
                    coroutineScope.launch {
                        drawerState.apply {
                            if (isOpen) close() else open()
                        }
                    }
                })
            }, bottomBar = {
                BottomNav(selectedScreens) { screen ->
                    when (screen) {
                        Screens.HOME -> {
                            with(starlingNavigator) {
                                navigateToHomeScreen()
                            }
                        }

                        Screens.SAVING_GOALS -> {
                            with(starlingNavigator) {
                                navigateToSavingsGoalsScreen(args.savingGoals)
                            }
                        }
                    }
                }
            },
                floatingActionButton = {
                    AddGoalFloatingActionButton(text = stringResource(R.string.add_new_goal)) {
                        with(starlingNavigator) {
                            navigateToAddNewSavingGoalScreen(args.savingGoals)
                        }
                    }
                }
            ) { _ ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(color = Color.LightGray)
                ) {
                    LoadScreen()
                }
            }
        }
    }

    @SuppressLint("VisibleForTests")
    @Composable
    fun LoadScreen() {
        val savingsGoals by viewModel.savingsGoalsList.collectAsStateWithLifecycle()

        LaunchedEffect(key1 = "key1") {
            viewModel.fetchSavingsGoals()
        }

        when {
            networkStatus.hasNetworkAccess(activity as FragmentActivity) -> {
                when (savingsGoals) {
                    is NetworkResult.Loading ->
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressComposable(Modifier.size(100.dp))
                        }

                    is NetworkResult.Success -> {
                        savingsGoals.data?.savingsGoals?.let {
                            SavingsGoalsList(model = viewModel.getCurrencyInReadable(savingsGoals.data?.savingsGoals!!))
                        }
                    }

                    is NetworkResult.Error -> {
                        Toast.makeText(
                            context,
                            com.challenge.common.R.string.something_went_wrong,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {}
                }
            }

            else -> {
                Toast.makeText(
                    context,
                    com.challenge.common.R.string.no_internet,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @Composable
    fun SavingsGoalsList(model: List<SavingsGoal>) {
        var isConfirmationSheetVisible by remember { mutableStateOf(false) }
        var isSavingGoalPosted by remember { mutableStateOf(false) }
        val savingGoalPosted by viewModel.savingGoalPosted.collectAsStateWithLifecycle()
        var selectedIndex by remember { mutableIntStateOf(0) }
        var selectedGoal:SavingsGoal? by remember { mutableStateOf(null) }

        Column(
            modifier = Modifier
                .background(color = WhiteSmoke)
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(bottom = 80.dp)
        ) {

            Spacer(modifier = Modifier.padding(top = 10.dp))
            Text(
                text = if (model.isNotEmpty()) stringResource(R.string.please_select_saving_goal) else "",
                color = Color.Blue,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.padding(top = 10.dp))
            if (model.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    items(count = model.size, itemContent = { index ->
                        GoalRow(item = model[index]) { savingsGoal ->
                            selectedGoal = savingsGoal
                            selectedIndex = index
                            isConfirmationSheetVisible = true
                            isSavingGoalPosted = false
                        }
                    })
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center

                ) {
                    Text(
                        text = stringResource(R.string.no_saving_goals_found),
                        color = Color.Blue,
                        style = MaterialTheme.typography.h3,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        if (isConfirmationSheetVisible) {
            if (args.savingGoals > 0) {
                BottomSheet(tripName = model[selectedIndex].name, onDismiss = {
                    isConfirmationSheetVisible = false
                },
                    onConfirmPressed = {
                        viewModel.addMoneyIntoSavingGoals(args.savingGoals,selectedGoal)
                        isSavingGoalPosted = true
                    }
                )
            } else {
                Toast.makeText(
                    context,
                    stringResource(com.challenge.common.R.string.transactions_empty_can_t_round_up),
                    Toast.LENGTH_LONG
                ).show()
            }

        }

        if (isSavingGoalPosted) {
            when {
                networkStatus.hasNetworkAccess(activity as FragmentActivity) -> {
                    when (savingGoalPosted) {
                        is NetworkResult.Loading -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(100.dp),
                                    color = Color.Blue
                                )
                            }
                        }

                        is NetworkResult.Success -> {
                            Toast.makeText(
                                context,
                                stringResource(R.string.round_up_saved),
                                Toast.LENGTH_SHORT
                            ).show()
                            with(starlingNavigator) {
                                navigateToHomeScreen()
                            }
                        }

                        is NetworkResult.Error -> {
                            Toast.makeText(
                                context,
                                savingGoalPosted.errorResponse?.errors?.get(0)?.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        else -> {}
                    }
                }

                else -> {
                    Toast.makeText(
                        context,
                        com.challenge.common.R.string.no_internet,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun GoalRow(item: SavingsGoal, onClick: (SavingsGoal) -> Unit) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 15.dp),
            shape = RoundedCornerShape(20.dp),
            onClick = {
                onClick(item)
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.LightGray),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = item.name,
                        color = Color.Blue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        style = MaterialTheme.typography.h6,
                        textAlign = TextAlign.Center
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        1.0f, color = Color.Blue, modifier = Modifier
                            .size(50.dp)
                            .padding(10.dp)
                    )
                    Text(
                        text = stringResource(
                            R.string.target,
                            item.target.majorUnit,
                            item.target.currency
                        ),
                        color = Color.Blue,
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Image(
                        modifier = Modifier.size(60.dp),
                        painter = painterResource(id = R.drawable.pound_coin),
                        contentDescription = stringResource(R.string.pound_coin)
                    )
                    Text(
                        text = stringResource(
                            R.string.total_saved,
                            item.totalSaved.majorUnit,
                            item.totalSaved.currency
                        ),
                        color = Color.Blue,
                        style = MaterialTheme.typography.subtitle1
                    )
                }
            }
        }
    }

    private fun handleDeviceBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    with(starlingNavigator) {
                        navigateToHomeScreen()
                    }
                }
            })
    }
}