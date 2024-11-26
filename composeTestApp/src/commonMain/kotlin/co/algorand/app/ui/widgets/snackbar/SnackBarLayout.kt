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

package co.algorand.app.ui.widgets.snackbar

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import co.algorand.app.ui.widgets.snackbar.SnackbarViewModel.ViewEvent.Idle

@Composable
fun SnackBarLayout(
    viewModel: SnackbarViewModel,
    hostState: SnackbarHostState,
) {
    LaunchedEffect(viewModel.viewEvent) {
        viewModel.viewEvent.collect { event ->
            when (event) {
                is SnackbarViewModel.ViewEvent.DisplaySnackBar -> {
                    hostState.showSnackbar(
                        duration = SnackbarDuration.Short,
                        message = event.message,
                    )
                }
                Idle -> {
                    // Do nothing
                }
            }
        }
    }
}