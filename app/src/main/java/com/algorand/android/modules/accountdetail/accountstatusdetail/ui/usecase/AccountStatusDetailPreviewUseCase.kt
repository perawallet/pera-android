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

package com.algorand.android.modules.accountdetail.accountstatusdetail.ui.usecase

import com.algorand.android.accountcore.ui.usecase.*
import com.algorand.android.accountinfo.component.domain.usecase.GetAccountInformation
import com.algorand.android.core.component.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.android.core.component.detail.domain.usecase.GetAccountDetailFlow
import com.algorand.android.models.ui.AccountAssetItemButtonState
import com.algorand.android.modules.accountdetail.accountstatusdetail.ui.decider.AccountStatusDetailPreviewDecider
import com.algorand.android.modules.accountdetail.accountstatusdetail.ui.mapper.AccountStatusDetailPreviewMapper
import com.algorand.android.modules.accountdetail.accountstatusdetail.ui.model.AccountStatusDetailPreview
import com.algorand.android.utils.Event
import com.algorand.android.utils.extensions.mapNotBlank
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class AccountStatusDetailPreviewUseCase @Inject constructor(
    private val getAccountDetailFlow: GetAccountDetailFlow,
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getAccountOriginalStateIconDrawablePreview: GetAccountOriginalStateIconDrawablePreview,
    private val accountStatusDetailPreviewMapper: AccountStatusDetailPreviewMapper,
    private val accountStatusDetailPreviewDecider: AccountStatusDetailPreviewDecider,
    private val getAccountInformation: GetAccountInformation
) {

    fun getAccountStatusDetailPreviewFlow(accountAddress: String) = channelFlow<AccountStatusDetailPreview> {
        getAccountDetailFlow(accountAddress).collectLatest { accountDetail ->
            if (accountDetail == null) return@collectLatest
            val accountInformation = getAccountInformation(accountAddress)
            val authAccountAddress = accountInformation?.rekeyAdminAddress
            val hasAccountAuthority = accountDetail.accountType?.canSignTransaction() == true
            val preview = accountStatusDetailPreviewMapper.mapToAccountStatusDetailPreview(
                titleString = accountStatusDetailPreviewDecider.decideTitleString(accountDetail.accountType),
                accountOriginalTypeDisplayName = getAccountDisplayName(accountAddress),
                accountOriginalTypeIconDrawablePreview = getAccountOriginalStateIconDrawablePreview(accountAddress),
                accountOriginalActionButton = AccountAssetItemButtonState.COPY,
                authAccountDisplayName = authAccountAddress?.mapNotBlank { safeAuthAddress ->
                    getAccountDisplayName(safeAuthAddress)
                },
                authAccountIconDrawablePreview = authAccountAddress?.mapNotBlank { safeAuthAddress ->
                    getAccountIconDrawablePreview(safeAuthAddress)
                },
                authAccountActionButton = accountStatusDetailPreviewDecider.decideAuthAccountActionButtonState(
                    accountType = accountDetail.accountType,
                ),
                accountTypeDrawablePreview = getAccountIconDrawablePreview(accountAddress),
                accountTypeString = accountStatusDetailPreviewDecider.decideAccountTypeString(accountDetail),
                descriptionAnnotatedString = accountStatusDetailPreviewDecider.decideDescriptionAnnotatedString(
                    accountDetail = accountDetail
                ),
                isRekeyToLedgerAccountAvailable = hasAccountAuthority,
                isRekeyToStandardAccountAvailable = hasAccountAuthority
            )
            send(preview)
        }
    }

    fun updatePreviewWithAddressCopyEvent(preview: AccountStatusDetailPreview?): AccountStatusDetailPreview? {
        return preview?.copy(copyAccountAddressToClipboardEvent = Event(Unit))
    }

    fun updatePreviewWithUndoRekeyNavigationEvent(preview: AccountStatusDetailPreview?): AccountStatusDetailPreview? {
        return preview?.copy(navToUndoRekeyNavigationEvent = Event(Unit))
    }
}
