package com.challenge.home.ui

import android.annotation.SuppressLint
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.challenge.common.R
import com.challenge.designsystem.component.CircularProgressComposable
import com.challenge.designsystem.theme.Purple40
import com.challenge.designsystem.theme.TropicalGreen
import com.challenge.designsystem.theme.WhiteSmoke
import com.challenge.common.NetworkResult
import com.challenge.common.model.transactiondomain.TransactionDomain
import com.challenge.common.model.transactiondomain.TransactionsDomain
import com.challenge.common.utils.NetworkStatus
import com.challenge.home.R.string


@SuppressLint("VisibleForTests")
@Composable
fun HomeScreen(
    transactionsState: NetworkResult<TransactionsDomain>,
    viewModel: HomeViewModel,
    networkStatus: NetworkStatus,
    roundUpSum: (Long) -> Unit,
) {
    val localContext = LocalContext.current
    when {
        networkStatus.hasNetworkAccess(localContext) -> {
            when (transactionsState) {
                is NetworkResult.Loading ->
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressComposable(Modifier.size(100.dp))
                    }

                is NetworkResult.Success -> {
                    transactionsState.data?.feedItems.let { transactionsList ->
                        transactionsList?.let { it1 ->
                            viewModel.getCurrencyInReadable(
                                it1
                            )
                        }?.let { it2 -> TransactionsList(model = it2) }
                        viewModel.getSumOfMinorUnits()?.let { roundUpSum(it) }
                    }
                }

                is NetworkResult.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        transactionsState.message?.let {
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
                stringResource(R.string.something_went_wrong), Toast.LENGTH_SHORT
            ).show()
        }
    }
}

@Composable
fun TransactionsList(model: List<TransactionDomain>) {
    val lazyListState = rememberLazyListState()
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
                text = stringResource(id = string.amount),
                style = MaterialTheme.typography.subtitle1
            )
            Text(
                text = stringResource(string.currency),
                style = MaterialTheme.typography.subtitle1
            )
            Text(
                text = stringResource(string.direction),
                style = MaterialTheme.typography.subtitle1
            )
        }
        if (model.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                state = lazyListState
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
fun TransactionRow(item: TransactionDomain) {
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
        Text(
            text = " ${item.amount.currency}",
            modifier = Modifier.weight(2f),
            style = MaterialTheme.typography.subtitle1
        )
        Text(
            text = " ${item.direction}",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.subtitle1
        )
    }
}