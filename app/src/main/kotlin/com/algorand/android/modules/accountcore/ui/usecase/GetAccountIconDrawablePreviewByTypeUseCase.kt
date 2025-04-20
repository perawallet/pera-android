/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.accountcore.ui.usecase

import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.wallet.account.detail.domain.model.AccountRegistrationType
import com.algorand.wallet.account.detail.domain.model.AccountType
import javax.inject.Inject

internal class GetAccountIconDrawablePreviewByTypeUseCase @Inject constructor() : GetAccountIconDrawablePreviewByType {

    override fun invoke(type: AccountType): AccountIconDrawablePreview {
        return when (type) {
            AccountType.Algo25 -> AccountIconDrawablePreviews.getAlgo25Drawable()
            AccountType.HdKey -> AccountIconDrawablePreviews.getHdKeyDrawable()
            AccountType.LedgerBle -> AccountIconDrawablePreviews.getLedgerBleDrawable()
            AccountType.NoAuth -> AccountIconDrawablePreviews.getNoAuthDrawable()
            AccountType.Rekeyed, AccountType.RekeyedAuth -> AccountIconDrawablePreviews.getRekeyedDrawable()
        }
    }

    override fun invoke(type: AccountRegistrationType): AccountIconDrawablePreview {
        return when (type) {
            AccountRegistrationType.Algo25 -> AccountIconDrawablePreviews.getAlgo25Drawable()
            AccountRegistrationType.HdKey -> AccountIconDrawablePreviews.getHdKeyDrawable()
            AccountRegistrationType.LedgerBle -> AccountIconDrawablePreviews.getLedgerBleDrawable()
            AccountRegistrationType.NoAuth -> AccountIconDrawablePreviews.getNoAuthDrawable()
        }
    }
}
