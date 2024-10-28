/*
 *  Copyright 2022 Pera Wallet, LDA
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.modules.assetinbox.send.ui.model

import com.algorand.android.modules.assetinbox.send.domain.model.Arc59SendSummary
import com.algorand.android.modules.assetinbox.send.domain.model.Arc59Transactions
import com.algorand.android.utils.ErrorResource
import com.algorand.android.utils.Event

data class Arc59SendSummaryPreview(
    val assetName: String,
    val formattedAssetAmount: String,
    val formattedMinBalanceFee: String,
    val summary: Arc59SendSummary?,
    val isLoading: Boolean,
    val showError: Event<ErrorResource>?,
    val onNavBack: Event<Unit>?,
    val onTxnSendSuccessfully: Event<String?>?,
    val arc59Transactions: Event<Arc59Transactions?>?
)
