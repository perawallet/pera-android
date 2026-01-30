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

package com.algorand.android.modules.addaccount.joint.transaction.domain.usecase

import androidx.core.net.toUri
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountSignatureStatus
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountSignerItem
import com.algorand.android.repository.ContactRepository
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.detail.domain.usecase.GetAccountType
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccount
import com.algorand.wallet.jointaccount.transaction.domain.model.ParticipantSignature
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestResponseType
import javax.inject.Inject

internal class CreateSignerAccountsUseCase @Inject constructor(
    private val getAccountDisplayName: GetAccountDisplayName,
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview,
    private val getLocalAccount: GetLocalAccount,
    private val contactRepository: ContactRepository,
    private val getAccountType: GetAccountType
) : CreateSignerAccounts {

    override suspend operator fun invoke(
        participantAddresses: List<String>,
        responses: List<ParticipantSignature>
    ): List<JointAccountSignerItem> {
        val responseMap = responses.associateBy { it.address }
        return participantAddresses.map { address ->
            createSignerItem(address, responseMap[address])
        }
    }

    override suspend fun hasSigningCapableLocalAccount(address: String): Boolean {
        return getAccountType(address)?.canSignTransaction() == true
    }

    private suspend fun createSignerItem(
        address: String,
        response: ParticipantSignature?
    ): JointAccountSignerItem {
        val status = when (response?.type) {
            SignRequestResponseType.SIGNED -> JointAccountSignatureStatus.Signed
            SignRequestResponseType.REJECTED -> JointAccountSignatureStatus.Rejected
            else -> JointAccountSignatureStatus.Pending
        }
        val contact = contactRepository.getContactByAddress(address)
        val localAccount = getLocalAccount(address)
        val isLedger = getAccountType(address) is AccountType.LedgerBle

        return JointAccountSignerItem(
            accountAddress = address,
            accountDisplayName = getAccountDisplayName(address),
            accountIconDrawablePreview = getAccountIconDrawablePreview(address),
            imageUri = contact?.imageUriAsString?.toUri(),
            signatureStatus = status,
            isLedgerAccount = isLedger,
            ledgerBluetoothAddress = (localAccount as? LocalAccount.LedgerBle)?.deviceMacAddress,
            ledgerAccountIndex = (localAccount as? LocalAccount.LedgerBle)?.indexInLedger
        )
    }
}
