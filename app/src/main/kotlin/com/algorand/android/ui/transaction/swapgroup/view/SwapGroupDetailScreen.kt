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

package com.algorand.android.ui.transaction.swapgroup.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.algorand.android.R
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.PeraSingleButtonState
import com.algorand.android.ui.compose.widget.PeraToolbar
import com.algorand.android.ui.compose.widget.modifier.clickableNoRipple
import com.algorand.android.ui.compose.widget.progress.PeraCircularProgressIndicator
import com.algorand.android.ui.transaction.history.model.TransactionHistoryItem
import com.algorand.android.ui.transaction.history.view.TransactionHistoryList
import com.algorand.android.ui.transaction.history.view.TransactionHistoryListListener
import com.algorand.android.ui.transaction.swapgroup.viewmodel.SwapGroupDetailViewModel
import com.algorand.android.ui.transaction.swapgroup.viewmodel.SwapGroupDetailViewModel.ViewState.Content
import com.algorand.android.ui.transaction.swapgroup.viewmodel.SwapGroupDetailViewModel.ViewState.Idle

@Composable
fun SwapGroupDetailScreen(viewModel: SwapGroupDetailViewModel, listener: SwapGroupDetailScreenListener) {
    Column(modifier = Modifier.fillMaxSize()) {
        PeraToolbar(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = stringResource(R.string.swap),
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
        when (val viewState = viewModel.state.collectAsStateWithLifecycle().value) {
            Idle -> Unit
            is Content -> {
                when (val contentState = viewState.state) {
                    is Content.ContentState.Loading -> LoadingState()
                    is Content.ContentState.Error -> ErrorState { viewModel.retryInitTransactions() }
                    is Content.ContentState.Success -> ContentState(contentState.transactionHistoryItems, listener)
                }
            }
        }
    }
}

@Composable
private fun ContentState(
    items: List<TransactionHistoryItem>,
    listener: TransactionHistoryListListener
) {
    TransactionHistoryList(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 12.dp),
        historyItems = items,
        listener = listener
    )
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        PeraCircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(onRetryClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        PeraSingleButtonState(
            modifier = Modifier.padding(24.dp),
            iconResId = null,
            titleResId = null,
            descriptionResId = R.string.an_error_occurred,
            buttonTextResId = R.string.retry,
            onClick = onRetryClick
        )
    }
}

interface SwapGroupDetailScreenListener : TransactionHistoryListListener {
    fun onNavBackClick()
    override fun onSwapClick(groupId: String) {}
}
