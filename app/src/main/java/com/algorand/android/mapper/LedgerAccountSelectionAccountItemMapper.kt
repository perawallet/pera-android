/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 *
 */

package com.algorand.android.mapper

import androidx.annotation.DrawableRes
import com.algorand.android.module.account.core.ui.model.AccountDisplayName
import com.algorand.android.module.account.core.ui.model.AccountIconDrawablePreview
import com.algorand.android.modules.rekey.model.AccountSelectionListItem
import com.algorand.android.modules.rekey.model.SelectedLedgerAccount
import javax.inject.Inject

class LedgerAccountSelectionAccountItemMapper @Inject constructor() {

    fun mapTo(
        address: String,
        accountDisplayName: AccountDisplayName,
        accountIconDrawablePreview: AccountIconDrawablePreview,
        @DrawableRes selectorDrawableRes: Int,
        selectedLedgerAccount: SelectedLedgerAccount
    ): AccountSelectionListItem.AccountItem {
        return AccountSelectionListItem.AccountItem(
            selectorDrawableRes = selectorDrawableRes,
            accountDisplayName = accountDisplayName,
            accountIconDrawablePreview = accountIconDrawablePreview,
            isSelected = false,
            address = address,
            selectedLedgerAccount = selectedLedgerAccount
        )
    }
}
