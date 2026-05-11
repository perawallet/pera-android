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

package com.algorand.android.ui.backup.list.mapper

import com.algorand.android.modules.accountcore.ui.usecase.AccountIconDrawablePreviews
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.backup.account.domain.model.AddressBackupPayload
import com.algorand.wallet.account.local.domain.model.LocalAccount
import javax.inject.Inject

class BackupAccountIconPreviewMapper @Inject constructor() {

    fun mapFromLocalAccount(account: LocalAccount): AccountIconDrawablePreview = when (account) {
        is LocalAccount.HdKey -> AccountIconDrawablePreviews.getHdKeyDrawable()
        is LocalAccount.Algo25 -> AccountIconDrawablePreviews.getAlgo25Drawable()
        is LocalAccount.LedgerBle -> AccountIconDrawablePreviews.getLedgerBleDrawable()
        is LocalAccount.NoAuth -> AccountIconDrawablePreviews.getNoAuthDrawable()
        is LocalAccount.Joint -> AccountIconDrawablePreviews.getJointDrawable()
    }

    fun mapFromAddressBackupPayload(payload: AddressBackupPayload): AccountIconDrawablePreview = when (payload) {
        is AddressBackupPayload.HdKey -> AccountIconDrawablePreviews.getHdKeyDrawable()
        is AddressBackupPayload.HdSeed -> AccountIconDrawablePreviews.getHdKeyDrawable()
        is AddressBackupPayload.Algo25 -> AccountIconDrawablePreviews.getAlgo25Drawable()
        is AddressBackupPayload.LedgerBle -> AccountIconDrawablePreviews.getLedgerBleDrawable()
        is AddressBackupPayload.NoAuth -> AccountIconDrawablePreviews.getNoAuthDrawable()
        is AddressBackupPayload.Joint -> AccountIconDrawablePreviews.getJointDrawable()
    }
}
