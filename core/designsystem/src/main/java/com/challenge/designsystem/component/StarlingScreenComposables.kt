package com.challenge.designsystem.component

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.challenge.common.R
import com.challenge.common.enums.Screens
import com.challenge.designsystem.theme.SatinBlue
import com.challenge.designsystem.theme.WhiteSmoke
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch


@Composable
fun TopBar(
    onBackPresses: () -> Unit,
    onMenuClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(color = MaterialTheme.colors.primary),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = onBackPresses) {
            Icon(
                Icons.Filled.ArrowBack,
                modifier = Modifier.padding(start = 20.dp),
                contentDescription = stringResource(R.string.back_arrow),
                tint = WhiteSmoke
            )
        }

        Text(
            text = stringResource(R.string.starling_bank),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = WhiteSmoke
        )
        IconButton(onClick = onMenuClick) {
            Icon(
                Icons.Filled.Menu,
                modifier = Modifier.padding(start = 20.dp),
                tint = WhiteSmoke,
                contentDescription = stringResource(R.string.menu_icon),
            )
        }
    }
}

@Composable
fun Drawer(onClick: (Screens) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colors.primary)
    ) {

        Image(
            modifier = Modifier
                .size(200.dp)
                .padding(top = 40.dp, bottom = 40.dp)
                .align(Alignment.CenterHorizontally),
            painter = painterResource(id = R.drawable.bank_icon),
            contentDescription = stringResource(R.string.bank_icon)
        )

        Divider(
            modifier = Modifier
                .height(2.dp)
                .fillMaxWidth()
                .background(color = WhiteSmoke)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 30.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        color = WhiteSmoke,
                        bounded = true
                    ),
                    onClick = {
                        onClick(Screens.HOME)
                    }
                ),
            verticalAlignment = Alignment.CenterVertically

        ) {
            Icon(
                Icons.Filled.Home,
                modifier = Modifier
                    .size(70.dp)
                    .padding(end = 40.dp),
                tint = Color.White,
                contentDescription = stringResource(R.string.home)
            )
            Text(
                text = stringResource(id = R.string.home),
                color = Color.White
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 15.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        color = WhiteSmoke,
                        bounded = true
                    ),
                    onClick = {
                        onClick(Screens.SAVING_GOALS)
                    }
                ),
            verticalAlignment = Alignment.CenterVertically

        ) {
            Icon(
                Icons.Filled.Add,
                modifier = Modifier
                    .size(70.dp)
                    .padding(end = 40.dp),
                tint = WhiteSmoke,
                contentDescription = stringResource(R.string.saving_goals)
            )
            Text(
                text = stringResource(R.string.saving_goals),
                color = WhiteSmoke
            )
        }
    }
}

@Composable
fun BottomNav(selectedScreens: Screens, onClick: (Screens) -> Unit) {
    BottomNavigation {
        Screens.values().forEach { screen ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        getBottomBarIcon(screens = screen),
                        contentDescription = stringResource(id = R.string.bottom_bar_icon)
                    )
                },
                label = { Text(text = screen.value, color = Color.White) },
                selected = screen == selectedScreens,
                onClick = {
                    onClick(screen)
                }
            )
        }
    }
}

@Composable
fun getBottomBarIcon(screens: Screens): ImageVector =
    when (screens.value) {
        stringResource(id = R.string.home) -> Icons.Filled.Home
        stringResource(id = R.string.saving_goals) -> Icons.Default.Star
        stringResource(id = R.string.add_new_goal) -> Icons.Filled.Add
        else -> Icons.Filled.Home
    }

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheet(tripName: String, onDismiss: () -> Unit, onConfirmPressed: () -> Unit) {
    val scope = rememberCoroutineScope()
    val state = rememberModalBottomSheetState(ModalBottomSheetValue.Expanded)
    LaunchedEffect(Unit) {
        snapshotFlow { state.currentValue }.filter { it == ModalBottomSheetValue.Hidden }
            .collect {
                onDismiss()
            }
    }

    ModalBottomSheetLayout(scrimColor = Color.White,
        sheetBackgroundColor = Color.White,
        sheetState = state,
        sheetShape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp),
        sheetContent = {
            ConfirmationBottomSheetContent(onConfirmPressed = {
                scope.launch {
                    state.hide()
                    onConfirmPressed()
                }
            }, tripName)
        }) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) { }
    }
}

@Composable
private fun ConfirmationBottomSheetContent(
    onConfirmPressed: () -> Unit,
    tripName: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .background(Color.Cyan),
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Text(
            text = stringResource(R.string.add_money_into, tripName),
            color = SatinBlue,
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            textAlign = TextAlign.Center
        )

        OutlinedButton(
            onClick = {
                onConfirmPressed()
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = SatinBlue),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        {
            Text(text = stringResource(R.string.confirm), color = WhiteSmoke)
        }
        Spacer(modifier = Modifier.padding(top = 50.dp))
    }
}

@SuppressLint("VisibleForTests")
@Composable
fun RoundUpFloatingActionButton(text: String, onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        icon = {
            Icon(
                Icons.Filled.Add,
                text,
                tint = WhiteSmoke
            )
        },
        text = {
            Text(
                text = text,
                color = WhiteSmoke,
            )
        },
        backgroundColor = SatinBlue
    )
}

@SuppressLint("VisibleForTests")
@Composable
fun AddGoalFloatingActionButton(text: String, onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        icon = {
            Icon(
                Icons.Filled.Add,
                text,
                tint = WhiteSmoke
            )
        },
        text = {
            Text(
                text = text,
                color = WhiteSmoke,
            )
        },
        backgroundColor = SatinBlue
    )
}

@Composable
fun CircularProgressComposable(modifier:Modifier) {
    CircularProgressIndicator(
        modifier = modifier,
        color = SatinBlue
    )
}
@Composable
fun TextFieldOutlined() {
    var text by remember { mutableStateOf("") }
    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        label = { Text("Label") }
    )
}