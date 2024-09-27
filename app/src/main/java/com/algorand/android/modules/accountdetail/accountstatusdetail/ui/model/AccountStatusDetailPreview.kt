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

package com.algorand.android.modules.accountdetail.accountstatusdetail.ui.model

import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.ui.AccountAssetItemButtonState
import com.algorand.android.module.account.core.ui.model.AccountDisplayName
import com.algorand.android.module.account.core.ui.model.AccountIconDrawablePreview
import com.algorand.android.utils.Event

data class AccountStatusDetailPreview(
    val titleString: String,

    val accountOriginalTypeDisplayName: AccountDisplayName,
    val accountOriginalTypeIconDrawablePreview: AccountIconDrawablePreview,
    val accountOriginalActionButton: AccountAssetItemButtonState,

    val authAccountDisplayName: AccountDisplayName?,
    val authAccountIconDrawablePreview: AccountIconDrawablePreview?,
    val authAccountActionButton: AccountAssetItemButtonState?,

    val accountTypeDrawablePreview: AccountIconDrawablePreview,
    val accountTypeString: String,

    val descriptionAnnotatedString: AnnotatedString,

    val isRekeyToLedgerAccountVisible: Boolean,
    val isRekeyToStandardAccountVisible: Boolean,

    val copyAccountAddressToClipboardEvent: Event<Unit>?,
    val navToUndoRekeyNavigationEvent: Event<Unit>?
) {

    val isRekeyGroupVisible: Boolean
        get() = authAccountDisplayName != null && authAccountIconDrawablePreview != null
}
