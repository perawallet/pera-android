/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.accountcore.ui.accountselection.model

import com.algorand.android.accountinfo.component.domain.model.AccountInformation
import com.algorand.android.foundation.Event
import com.algorand.android.foundation.PeraResult

data class SenderAccountSelectionPreview(
    val accountList: List<BaseAccountSelectionListItem>,
    val isLoading: Boolean,
    val isEmptyStateVisible: Boolean,
    val senderAccountInformationSuccessEvent: Event<AccountInformation>?,
    val senderAccountInformationErrorEvent: Event<PeraResult.Error>?
)