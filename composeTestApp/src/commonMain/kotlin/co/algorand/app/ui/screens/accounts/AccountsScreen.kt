/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package co.algorand.app.ui.screens.accounts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.navigation.NavController
import co.algorand.app.ui.widgets.snackbar.SnackbarViewModel
import com.algorand.common.account.info.domain.model.AccountInformation
import com.algorand.common.ui.theme.PeraTheme
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    navController: NavController,
    snackbarViewModel: SnackbarViewModel,
    tag: String,
    accountsViewModel: AccountsViewModel = koinViewModel()
) {

    LifecycleStartEffect(Unit) {
        accountsViewModel.initAccounts()
        onStopOrDispose {}
    }

    val bottomSheetState = rememberStandardBottomSheetState(
        skipHiddenState = false,
        initialValue = SheetValue.Hidden
    )
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState)
    var selectedAccountAddress by remember { mutableStateOf("") }

    var isRecoverAlgo25AccountModalVisible by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    BottomSheetScaffold(
        modifier = Modifier.fillMaxSize().background(PeraTheme.colors.background),
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContainerColor = PeraTheme.colors.background,
        sheetContent = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    style = PeraTheme.typography.body.regular.sans,
                    text = selectedAccountAddress,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedButton(
                    onClick = {
                        accountsViewModel.deleteAccount(selectedAccountAddress)
                        scope.launch {
                            scaffoldState.bottomSheetState.hide()
                        }
                    },
                ) {
                    Text(text = "Delete")
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        },
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        ) {
            val state = accountsViewModel.state.collectAsState()
            val currentState = state.value
            if (currentState is AccountsViewModel.ViewState.Accounts) {
                val addresses = currentState.accounts.keys.toList()
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(addresses) { address ->
                        AccountItem(
                            address = address,
                            info = currentState.accounts[address]
                        ) {
                            selectedAccountAddress = address
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        }
                    }
                }
            }

            AccountActionsFab(
                onAddAlgo25Click = accountsViewModel::addAlgo25Account,
                onAddBip39Click = accountsViewModel::addBip39Account,
                onRecoverClick = {
                    isRecoverAlgo25AccountModalVisible = true
                }
            )
        }

        if (isRecoverAlgo25AccountModalVisible) {
            RecoverAlgo25AccountModal(
                onDismiss = {
                    isRecoverAlgo25AccountModalVisible = false
                },
                onRecoverAccount = {
                    accountsViewModel.recoverAccount(it)
                }
            )
        }
    }
}

@Composable
private fun AccountActionsFab(
    onAddAlgo25Click: () -> Unit,
    onAddBip39Click: () -> Unit,
    onRecoverClick: () -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    val rotationDegree = animateFloatAsState(if (isMenuExpanded) 45f else 0f)
    Column(
        horizontalAlignment = Alignment.End
    ) {
        AnimatedVisibility(isMenuExpanded) {
            Column(
                modifier = Modifier.padding(bottom = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.End
            ) {

                ExtendedFloatingActionButton(
                    onClick = {
                        onRecoverClick()
                        isMenuExpanded = false
                    },
                    icon = { },
                    text = { Text(text = "Recover Algo25") },
                )

                Spacer(modifier = Modifier.height(16.dp))

                ExtendedFloatingActionButton(
                    onClick = {
                        onAddBip39Click()
                        isMenuExpanded = false
                    },
                    icon = { },
                    text = { Text(text = "Add Bip39 account") },
                )

                Spacer(modifier = Modifier.height(16.dp))

                ExtendedFloatingActionButton(
                    onClick = {
                        onAddAlgo25Click()
                        isMenuExpanded = false
                    },
                    icon = { },
                    text = { Text(text = "Add Algo25 account") },
                )
            }

        }

        SmallFloatingActionButton(
            modifier = Modifier
                .rotate(rotationDegree.value)
                .padding(16.dp),
            onClick = { isMenuExpanded = !isMenuExpanded },
            shape = CircleShape
        ) {
            Icon(Icons.Filled.Add, "Small floating action button.")
        }
    }
}

@Composable
private fun AccountItem(address: String, info: AccountInformation?, onClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                if (info != null) {
                    onClick(address)
                }
            }
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                modifier = Modifier.padding(end = 16.dp),
                text = "Address",
                style = PeraTheme.typography.body.regular.sansBold,
                color = PeraTheme.colors.textGray,
                fontSize = 12.sp
            )
            Text(
                text = shortenAccountAddress(address)
            )
        }
        Column {
            Text(
                modifier = Modifier.padding(end = 16.dp),
                text = "Algo Balance",
                style = PeraTheme.typography.body.regular.sansBold,
                color = PeraTheme.colors.textGray,
                fontSize = 12.sp
            )
            Text(
                text = info?.amount ?: "-"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecoverAlgo25AccountModal(
    onDismiss: () -> Unit = {},
    onRecoverAccount: (String) -> Unit = {}
) {
    val textValue = remember { mutableStateOf("") }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = textValue.value,
                    onValueChange = {
                        textValue.value = it
                    },
                    label = { Text("") }
                )
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedButton(
                    onClick = {
                        onRecoverAccount(textValue.value)
                        onDismiss()
                    },
                ) {
                    Text(text = "Recover")
                }
            }
        }
    )
}

private fun shortenAccountAddress(address: String): String {
    val charSize = address.length
    return address.slice(0..5) + "..." + address.slice(charSize - 5 until charSize)
}
