package com.challenge.starlingbank.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.challenge.common.utils.NetworkStatus
import com.challenge.designsystem.theme.StarlingBankAppTheme

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var networkStatus: NetworkStatus
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            StarlingBankAppTheme {
                AppScaffold(networkStatus = networkStatus)
            }
        }
    }
}
