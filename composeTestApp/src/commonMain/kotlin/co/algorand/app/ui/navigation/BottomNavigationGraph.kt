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

package co.algorand.app.ui.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import co.algorand.app.ui.screens.DiscoverScreen
import co.algorand.app.ui.screens.accounts.AccountsScreen
import co.algorand.app.ui.screens.NftsScreen
import co.algorand.app.ui.screens.SettingsScreen
import co.algorand.app.ui.widgets.snackbar.SnackBarLayout
import co.algorand.app.ui.widgets.snackbar.SnackbarViewModel
import org.koin.compose.viewmodel.koinNavViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
fun NavGraphBuilder.getBottomNavigationGraph(
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    composable<Accounts> {
        val backStackEntry = remember(it) { navController.getBackStackEntry<Accounts>() }
        val sharedViewModel: SnackbarViewModel = koinNavViewModel(viewModelStoreOwner = backStackEntry)
        AccountsScreen(
            tag = backStackEntry.toRoute<Accounts>().details.name,
            navController = navController,
            snackbarViewModel = sharedViewModel,
        )
        SnackBarLayout(sharedViewModel, snackbarHostState)
    }
    composable<Discover> {
        val backStackEntry = remember(it) { navController.getBackStackEntry<Discover>() }
        val sharedViewModel: SnackbarViewModel = koinNavViewModel(viewModelStoreOwner = backStackEntry)
        DiscoverScreen(
            tag = backStackEntry.toRoute<Discover>().details.name,
            navController = navController,
            snackbarViewModel = sharedViewModel,
        )
        SnackBarLayout(sharedViewModel, snackbarHostState)
    }
    composable<Collectibles> {
        val backStackEntry = remember(it) { navController.getBackStackEntry<Collectibles>() }
        val sharedViewModel: SnackbarViewModel = koinNavViewModel(viewModelStoreOwner = backStackEntry)
        NftsScreen(
            tag = backStackEntry.toRoute<Collectibles>().details.name,
            navController = navController,
            snackbarViewModel = sharedViewModel,
        )
        SnackBarLayout(sharedViewModel, snackbarHostState)
    }
    composable<Settings> {
        val backStackEntry = remember(it) { navController.getBackStackEntry<Settings>() }
        val sharedViewModel: SnackbarViewModel = koinNavViewModel(viewModelStoreOwner = backStackEntry)
        SettingsScreen(
            tag = backStackEntry.toRoute<Settings>().details.name,
            navController = navController,
            snackbarViewModel = sharedViewModel,
        )
        SnackBarLayout(sharedViewModel, snackbarHostState)
    }
}
