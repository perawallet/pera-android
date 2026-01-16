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

package com.algorand.android.modules.accountdetail.history.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.algorand.android.ui.transaction.csv.viewmodel.CsvViewModel
import com.algorand.android.ui.transaction.history.view.TransactionHistoryListItemHeader
import com.algorand.android.ui.transaction.history.view.TransactionHistoryListListener
import com.algorand.android.ui.transaction.history.view.pagingTransactionHistoryListItems
import com.algorand.android.ui.transaction.history.viewmodel.TransactionHistoryViewModel
import com.algorand.android.ui.transaction.history.viewmodel.TransactionHistoryViewModel.ViewState

@Composable
fun AccountTransactionHistoryScreen(
    csvViewModel: CsvViewModel,
    transactionHistoryViewModel: TransactionHistoryViewModel,
    listener: AccountHistoryScreenListener
) {
    val txnHistoryState = transactionHistoryViewModel.state.collectAsStateWithLifecycle()
    val historyItems = (txnHistoryState.value as? ViewState.Content)?.pagingData?.collectAsLazyPagingItems()
    Column(modifier = Modifier.fillMaxSize()) {
        TransactionHistoryListItemHeader(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            csvViewModel = csvViewModel,
            isFilterSelected = transactionHistoryViewModel.isFilterSelected(),
            onFilterClick = listener::onFilterClick,
            onCsvClick = listener::onCsvClick
        )
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            pagingTransactionHistoryListItems(historyItems, listener)
        }
    }
}

interface AccountHistoryScreenListener : TransactionHistoryListListener {
    fun onFilterClick()
    fun onCsvClick()
}
