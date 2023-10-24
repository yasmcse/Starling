package com.challenge.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.DrawerValue
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.challenge.common.StarlingNavigator
import com.challenge.designsystem.component.BottomNav
import com.challenge.designsystem.component.Drawer
import com.challenge.common.R
import com.challenge.designsystem.component.TopBar
import com.challenge.common.enums.Screens
import com.challenge.common.utils.NetworkStatus
import com.challenge.designsystem.component.CircularProgressComposable
import com.challenge.designsystem.component.RoundUpFloatingActionButton
import com.challenge.designsystem.theme.Purple40
import com.challenge.designsystem.theme.StarlingBankAppTheme
import com.challenge.designsystem.theme.TropicalGreen
import com.challenge.designsystem.theme.WhiteSmoke
import com.challenge.di.NetworkResult
import com.challenge.model.Transaction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    @Inject
    lateinit var starlingNavigator: StarlingNavigator

    @Inject
    lateinit var networkStatus: NetworkStatus

    private val viewModel by viewModels<HomeViewModel>()

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter", "VisibleForTests")
    @Composable
    fun Scaffold() {
        val scaffoldState = rememberScaffoldState()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val coroutineScope = rememberCoroutineScope()
        val selectedScreens by remember { mutableStateOf(Screens.values().first()) }
        var isTransactionListEmpty by remember { mutableStateOf(false) }

        ModalDrawer(
            drawerState = drawerState,
            drawerContent =
            {
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
                                        if (isOpen) {
                                            close()
                                            viewModel.getSumOfMinorUnits()?.let {
                                                navigateToSavingsGoalsScreen(
                                                    it
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            })
        {
            Scaffold(
                scaffoldState = scaffoldState,
                topBar = {
                    TopBar(
                        onBackPresses = {
                            with(starlingNavigator) {
                                goBack()
                            }
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
                    BottomNav(selectedScreens) { screen ->
                        when (screen) {
                            Screens.HOME -> {
                                with(starlingNavigator) {
                                    navigateToHomeScreen()
                                }
                            }

                            Screens.SAVING_GOALS -> {
                                with(starlingNavigator) {
                                    viewModel.getSumOfMinorUnits()?.let {
                                        navigateToSavingsGoalsScreen(
                                            it
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                floatingActionButton = {
                    AnimatedVisibility(
                        visible = !isTransactionListEmpty,
                        enter = fadeIn(initialAlpha = 0.5f),
                        exit = fadeOut(
                            animationSpec = TweenSpec(durationMillis = 500)
                        )

                    ) {
                        RoundUpFloatingActionButton(
                            stringResource(
                                com.challenge.home.R.string.round_up
                            )
                        ) {
                            with(starlingNavigator) {
                                viewModel.getSumOfMinorUnits()?.let {
                                    navigateToSavingsGoalsScreen(
                                        it
                                    )
                                }
                            }
                        }
                    }
                }
            ) { _ ->
                LoadScreen { isEmpty ->
                    if (isEmpty) {
                        isTransactionListEmpty = true
                    }
                }
            }
        }
    }

    @SuppressLint("VisibleForTests")
    @Composable
    fun LoadScreen(isTransactionListEmpty: (Boolean) -> Unit) {
        val transactions by viewModel.transactionsList.collectAsStateWithLifecycle()

        LaunchedEffect(key1 = "key1") {
            viewModel.fetchTransactions()
        }

        if (transactions.data?.feedItems?.isEmpty() == true) {
            isTransactionListEmpty(true)
        }

        when {
            networkStatus.hasNetworkAccess(activity as FragmentActivity) -> {
                when (transactions) {
                    is NetworkResult.Loading ->
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressComposable(Modifier.size(100.dp))
                        }

                    is NetworkResult.Success -> {
                        transactions.data?.feedItems?.let {
                            TransactionsList(model = viewModel.getCurrencyInReadable(it))
                        }
                    }

                    is NetworkResult.Error -> {
                        Toast.makeText(
                            context,
                            transactions.errorResponse?.errors?.get(0)?.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    else -> {}
                }
            }

            else -> {
                Toast.makeText(
                    context,
                    stringResource(R.string.something_went_wrong), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @Composable
    fun TransactionsList(model: List<Transaction>) {

        Column(
            modifier = Modifier
                .background(color = WhiteSmoke)
                .fillMaxWidth()
                .fillMaxHeight()
        ) {

            Spacer(modifier = Modifier.padding(top = 10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(start = 10.dp, end = 10.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = com.challenge.home.R.string.amount),
                    style = MaterialTheme.typography.subtitle1
                )
                Text(
                    text = stringResource(com.challenge.home.R.string.currency),
                    style = MaterialTheme.typography.subtitle1
                )
                Text(
                    text = stringResource(com.challenge.home.R.string.direction),
                    style = MaterialTheme.typography.subtitle1
                )
            }
            if (model.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    items(count = model.size, itemContent = { index ->
                        TransactionRow(item = model[index])
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
                        text = stringResource(R.string.no_transactions_found),
                        color = Color.Blue,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }

        }
    }

    @Composable
    fun TransactionRow(item: Transaction) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(start = 10.dp, end = 10.dp)
                .background(
                    color = if (item.direction.equals(
                            stringResource(R.string.direction_in),
                            true
                        )
                    ) TropicalGreen else Purple40
                ),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = " ${item.amount.majorUnit}",
                modifier = Modifier.weight(2f),
                style = MaterialTheme.typography.subtitle1
            )
            Text(text = " ${item.amount.currency}",
                modifier = Modifier.weight(2f),
                style = MaterialTheme.typography.subtitle1)
            Text(text = " ${item.direction}",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.subtitle1)
        }
    }

    private fun handleDeviceBackButton() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    with(starlingNavigator) {
                        goBack()
                    }
                }
            })
    }
}