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

package co.algorand.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.navigation.NavController
import co.algorand.app.ui.widgets.snackbar.SnackbarViewModel
import com.algorand.common.ui.theme.PeraTheme
import org.koin.compose.viewmodel.koinViewModel

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

    Box(
        modifier = Modifier.fillMaxSize().background(PeraTheme.colors.background),
        contentAlignment = Alignment.BottomEnd
    ) {
        val state = accountsViewModel.state.collectAsState()
        val currentState = state.value
        if (currentState is AccountsViewModel.ViewState.Accounts) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(currentState.accounts) { account ->
                    Text(
                        text = account
                    )
                }
            }
        }
        FloatingActionButton(
            modifier = Modifier.padding(end = 16.dp, bottom = 24.dp),
            onClick = {
                accountsViewModel.addAccount()
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add account"
            )
        }
    }
}
