package com.challenge.ui

import android.annotation.SuppressLint
import android.widget.Toast
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.challenge.designsystem.component.BottomSheet
import com.challenge.designsystem.component.CircularProgressComposable
import com.challenge.designsystem.theme.WhiteSmoke
import com.challenge.common.NetworkResult
import com.challenge.common.model.savinggoaldomain.SavingsGoalDomain
import com.challenge.common.model.savinggoaldomain.SavingsGoalsDomain
import com.challenge.common.utils.NetworkStatus
import com.challenge.savingsgoals.R


@SuppressLint("VisibleForTests")
@Composable
fun SavingGoalsScreen(
    savingGoalsState: NetworkResult<SavingsGoalsDomain>,
    viewModel: SavingGoalsViewModel,
    networkStatus: NetworkStatus,
    roundUpSum: Long?
) {
    val localContext = LocalContext.current

    when {
        networkStatus.hasNetworkAccess(localContext) -> {
            when (savingGoalsState) {
                is NetworkResult.Loading ->
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressComposable(Modifier.size(100.dp))
                    }

                is NetworkResult.Success -> {
                    savingGoalsState.data?.list?.let {
                        SavingsGoalsList(
                            roundUpSum,
                            model = viewModel.getCurrencyInReadable(savingGoalsState.data?.list!!),
                            viewModel,
                            networkStatus
                        )
                    }
                }

                is NetworkResult.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        savingGoalsState.message?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.subtitle1,
                                color = MaterialTheme.colors.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        else -> {
            Toast.makeText(
                localContext,
                com.challenge.common.R.string.no_internet,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

@Composable
fun SavingsGoalsList(
    roundUpSum: Long?,
    model: List<SavingsGoalDomain>,
    viewModel: SavingGoalsViewModel,
    networkStatus: NetworkStatus
) {
    val localContext = LocalContext.current
    var isConfirmationSheetVisible by remember { mutableStateOf(false) }
    var isSavingGoalPosted by remember { mutableStateOf(false) }
    val savingGoalPosted by viewModel.savingGoalPosted.collectAsStateWithLifecycle()
    var selectedIndex by remember { mutableIntStateOf(0) }
    var selectedGoal: SavingsGoalDomain? by remember { mutableStateOf(null) }
    val lazyListState = rememberLazyListState()
    Column(
        modifier = Modifier
            .background(color = WhiteSmoke)
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(bottom = 80.dp)
    ) {

        Spacer(modifier = Modifier.padding(top = 10.dp))
        Text(
            text = if (model.isNotEmpty()) stringResource(R.string.transfer_round_up_sum_into_saving_goal) else "",
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
                    .fillMaxHeight(),
                state = lazyListState
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
        when {
            roundUpSum != null -> {
                if (roundUpSum > 0) {
                    BottomSheet(tripName = model[selectedIndex].name, onDismiss = {
                        isConfirmationSheetVisible = false
                    },
                        onConfirmPressed = {
                            viewModel.addMoneyIntoSavingGoals(roundUpSum, selectedGoal)
                            isSavingGoalPosted = true
                        }
                    )
                }
            }
        }
    }

    if (isSavingGoalPosted) {
        when {
            networkStatus.hasNetworkAccess(localContext) -> {
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
                        viewModel.fetchSavingsGoals()
                    }

                    is NetworkResult.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            savingGoalPosted.message?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.subtitle1,
                                    color = MaterialTheme.colors.error,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    else -> {}
                }
            }

            else -> {
                Toast.makeText(
                    localContext,
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
fun GoalRow(item: SavingsGoalDomain, onClick: (SavingsGoalDomain) -> Unit) {
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