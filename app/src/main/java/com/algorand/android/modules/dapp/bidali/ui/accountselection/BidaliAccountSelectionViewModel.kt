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

package com.algorand.android.modules.dapp.bidali.ui.accountselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.module.account.core.ui.accountselection.model.BaseAccountSelectionListItem
import com.algorand.android.modules.dapp.bidali.ui.accountselection.model.BidaliAccountSelectionPreview
import com.algorand.android.modules.dapp.bidali.ui.accountselection.usecase.BidaliAccountSelectionPreviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class BidaliAccountSelectionViewModel @Inject constructor(
    private val bidaliAccountSelectionPreviewUseCase: BidaliAccountSelectionPreviewUseCase
) : ViewModel() {

    val accountItemsFlow: Flow<List<BaseAccountSelectionListItem>>
        get() = _accountItemsFlow
    private val _accountItemsFlow = MutableStateFlow<List<BaseAccountSelectionListItem>>(emptyList())

    private val _bidaliAccountSelectionPreviewFlow = MutableStateFlow(
        bidaliAccountSelectionPreviewUseCase.getInitialPreview()
    )
    val bidaliAccountSelectionPreviewFlow: StateFlow<BidaliAccountSelectionPreview>
        get() = _bidaliAccountSelectionPreviewFlow

    init {
        initAccountItems()
    }

    private fun initAccountItems() {
        viewModelScope.launch {
            _accountItemsFlow.emit(bidaliAccountSelectionPreviewUseCase.getBidaliAccountSelectionList())
        }
    }

    fun onAccountSelected(accountAddress: String) {
        viewModelScope.launch {
            _bidaliAccountSelectionPreviewFlow
                .emit(
                    bidaliAccountSelectionPreviewUseCase
                        .getOnAccountSelectedPreview(
                            _bidaliAccountSelectionPreviewFlow.value,
                            accountAddress
                        )
                )
        }
    }
}
