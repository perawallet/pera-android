/*
 *  Copyright 2022-2025 Pera Wallet, LDA
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.modules.inbox.allaccounts.ui.mapper

import com.algorand.android.modules.inbox.allaccounts.ui.model.InboxPreview
import com.algorand.android.utils.ErrorResource
import com.algorand.android.utils.Event
import com.algorand.wallet.inbox.asset.domain.model.AssetInboxRequest
import com.algorand.wallet.inbox.domain.model.InboxMessages
import java.time.ZonedDateTime

data class InboxPreviewParams(
    val assetInboxList: List<AssetInboxRequest>,
    val addresses: List<String>,
    val inboxMessages: InboxMessages?,
    val isLoading: Boolean,
    val isEmptyStateVisible: Boolean,
    val showError: Event<ErrorResource>? = null,
    val onNavBack: Event<Unit>? = null,
    val lastOpenedTime: ZonedDateTime? = null,
    val filterAccountAddress: String? = null,
    val localAccountAddresses: List<String> = emptyList()
)

interface InboxPreviewMapper {
    suspend operator fun invoke(params: InboxPreviewParams): InboxPreview
    fun getInitialPreview(): InboxPreview
}
