package com.challenge.ui

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import com.challenge.designsystem.component.CircularProgressComposable
import com.challenge.designsystem.theme.Blue
import com.challenge.designsystem.theme.WhiteSmoke
import com.challenge.common.NetworkResult
import com.challenge.common.model.newsavingdomain.NewSavingGoalResponseDomain
import com.challenge.common.utils.NetworkStatus
import com.challenge.savingsgoals.R


@SuppressLint("VisibleForTests")
@Composable
fun AddNewGoalScreen(
    newGoalModelState: NetworkResult<NewSavingGoalResponseDomain>,
    viewModel: AddNewGoalViewModel,
    networkStatus: NetworkStatus,
    onSubmitSuccess: (Boolean) -> Unit
) {
    val localContext = LocalContext.current
    val keyBordController = LocalSoftwareKeyboardController.current

    var tripName: String by remember { mutableStateOf("") }
    var targetCurrency: String by remember { mutableStateOf("GBP") }
    var targetAmount: String by remember { mutableStateOf("") }
    var isSubmitPressed by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(color = WhiteSmoke)
        ) {
            Spacer(modifier = Modifier.padding(top = 15.dp))
            Text(
                text = stringResource(R.string.add_goal),
                color = Color.Blue,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                Text(
                    text = stringResource(R.string.trip_name), modifier = Modifier.padding(
                        start = 10.dp
                    ), style = MaterialTheme.typography.caption, color = Blue

                )
                TextField(
                    value = tripName,
                    onValueChange = {
                        tripName = it
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        keyBordController?.hide()
                    }),
                    label = {
                        Text(
                            text = stringResource(R.string.please_enter_trip_name),
                            style = MaterialTheme.typography.caption,
                            color = Color.Black
                        )
                    },
                    maxLines = 1,
                    textStyle = MaterialTheme.typography.subtitle1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, start = 10.dp, end = 10.dp),
                    shape = RectangleShape
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                Text(
                    text = stringResource(R.string.currency), modifier = Modifier.padding(
                        start = 10.dp
                    ), style = MaterialTheme.typography.caption, color = Blue

                )
                TextField(
                    value = targetCurrency,
                    enabled = false,
                    onValueChange = {
                        targetCurrency = it
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        keyBordController?.hide()
                    }),
                    maxLines = 1,
                    textStyle = MaterialTheme.typography.subtitle1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, start = 10.dp, end = 10.dp),
                    shape = RectangleShape
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                Text(
                    text = stringResource(R.string.amount), modifier = Modifier.padding(
                        start = 10.dp
                    ), style = MaterialTheme.typography.caption, color = Blue

                )
                TextField(
                    value = targetAmount,
                    onValueChange = {
                        targetAmount = it
                    },
                    label = {
                        Text(
                            text = stringResource(R.string.amount_e_g_100),
                            style = MaterialTheme.typography.caption,
                            color = Color.Black
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Number
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        keyBordController?.hide()
                    }),
                    maxLines = 1,
                    textStyle = MaterialTheme.typography.subtitle1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, start = 10.dp, end = 10.dp),
                    shape = RectangleShape
                )
            }
            Button(
                enabled = viewModel.isValidInput(tripName, targetAmount),
                onClick = {
                    viewModel.postNewSavingGoal(tripName, targetCurrency, targetAmount.toLong())
                    isSubmitPressed = true
                },
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .height(80.dp)
                    .padding(start = 30.dp, end = 30.dp, top = 20.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = stringResource(id = R.string.submit),
                    modifier = Modifier
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.subtitle1,
                    color = WhiteSmoke,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
    if (isSubmitPressed) {
        when {
            networkStatus.hasNetworkAccess(localContext) -> {
                when (newGoalModelState) {
                    is NetworkResult.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressComposable(Modifier.size(100.dp))
                        }
                    }

                    is NetworkResult.Success -> {
                        onSubmitSuccess(true)
                    }

                    is NetworkResult.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Toast.makeText(
                                localContext,newGoalModelState.message, Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            else -> {
                Toast.makeText(
                    localContext, com.challenge.common.R.string.no_internet, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}