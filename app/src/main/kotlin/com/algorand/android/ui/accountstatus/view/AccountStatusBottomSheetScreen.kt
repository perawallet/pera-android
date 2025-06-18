/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.ui.accountstatus.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.ui.accountstatus.viewmodel.AccountStatusDetailViewModel
import com.algorand.android.ui.accountstatus.viewmodel.AccountStatusDetailViewModel.ViewState.Content
import com.algorand.android.ui.accountstatus.viewmodel.AccountStatusDetailViewModel.ViewState.Idle
import com.algorand.android.ui.compose.widget.PeraBottomSheetDragIndicator

@Composable
fun AccountStatusBottomSheetScreen(
    listener: AccountStatusBottomSheetScreenListener,
    viewModel: AccountStatusDetailViewModel
) {
    Column(horizontalAlignment = CenterHorizontally) {
        PeraBottomSheetDragIndicator(modifier = Modifier.padding(top = 12.dp))
        when (val state = viewModel.state.collectAsStateWithLifecycle().value) {
            Idle -> Unit
            is Content -> AccountStatusBottomSheetContentState(state, listener)
        }
    }
}

interface AccountStatusBottomSheetScreenListener : AccountStatusBottomSheetContentStateListener
