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

package com.algorand.android.modules.addaccount.joint.transaction.model

import android.net.Uri
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview

/**
 * Represents a signer account in a joint account transaction
 */
data class JointAccountSignerItem(
    val accountAddress: String,
    val accountDisplayName: AccountDisplayName,
    val accountIconDrawablePreview: AccountIconDrawablePreview,
    val imageUri: Uri?, // For contacts
    val signatureStatus: JointAccountSignatureStatus,
    val showProgress: Boolean = false,
    val isLocalAccount: Boolean = false,
    val isLedgerAccount: Boolean = false,
    val ledgerBluetoothAddress: String? = null,
    val ledgerAccountIndex: Int? = null,
    val accountAuthAddress: String? = null
) {
    /**
     * Returns true if this signer can be signed with Ledger
     * (is a Ledger account AND has all required data AND hasn't signed/declined yet)
     */
    val canSignWithLedger: Boolean
        get() = isLedgerAccount &&
                ledgerBluetoothAddress != null &&
                ledgerAccountIndex != null &&
                signatureStatus == JointAccountSignatureStatus.Pending
}
