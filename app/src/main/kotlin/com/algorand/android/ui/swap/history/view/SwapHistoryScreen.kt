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

package com.algorand.android.ui.swap.history.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.PeraToolbar
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.ui.swap.history.viewmodel.SwapHistoryViewModel
import com.algorand.android.ui.swap.history.viewmodel.SwapHistoryViewModel.ViewState.Content
import com.algorand.android.ui.swap.history.viewmodel.SwapHistoryViewModel.ViewState.Idle

@Composable
fun SwapHistoryScreen(
    viewModel: SwapHistoryViewModel,
    listener: SwapHistoryScreenListener
) {
    Column(modifier = Modifier.fillMaxSize()) {
        val viewState = viewModel.state.collectAsStateWithLifecycle()
        when (viewState.value) {
            Idle -> Unit
            is Content -> {
                PeraToolbar(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    text = stringResource(R.string.swap_history),
                    startContainer = {
                        Icon(
                            modifier = Modifier
                                .clickableNoRipple(onClick = listener::onNavBackClick)
                                .size(40.dp)
                                .padding(8.dp),
                            painter = painterResource(R.drawable.ic_left_arrow),
                            tint = PeraTheme.colors.text.main,
                            contentDescription = null
                        )
                    }
                )
                PagingSwapHistoryList(
                    modifier = Modifier.fillMaxSize(),
                    pagingList = (viewState.value as Content).pagingData,
                    contentPadding = PaddingValues(24.dp),
                    onSwapItemClick = viewModel::displayTxnInPeraExplorer
                )
            }
        }
    }
}

interface SwapHistoryScreenListener {
    fun onNavBackClick()
}
