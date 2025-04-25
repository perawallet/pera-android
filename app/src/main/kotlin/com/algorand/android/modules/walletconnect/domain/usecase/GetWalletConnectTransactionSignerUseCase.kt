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

package com.algorand.android.modules.walletconnect.domain.usecase

import com.algorand.android.models.WalletConnectTransactionSigner
import com.algorand.wallet.account.core.domain.model.TransactionSigner
import com.algorand.wallet.account.core.domain.model.TransactionSigner.SignerNotFound
import com.algorand.wallet.account.core.domain.usecase.GetTransactionSigner
import com.algorand.wallet.account.detail.domain.model.AccountRegistrationType
import com.algorand.wallet.account.detail.domain.usecase.GetAccountRegistrationType
import com.algorand.wallet.account.local.domain.usecase.GetLedgerBleAccount
import javax.inject.Inject

internal class GetWalletConnectTransactionSignerUseCase @Inject constructor(
    private val getAccountRegistrationType: GetAccountRegistrationType,
    private val getTransactionSigner: GetTransactionSigner,
    private val getLedgerBleAccount: GetLedgerBleAccount
) : GetWalletConnectTransactionSigner {

    override suspend fun invoke(signer: WalletConnectTransactionSigner): TransactionSigner? {
        return when (signer) {
            is WalletConnectTransactionSigner.Rekeyed -> {
                val address = signer.address.decodedAddress ?: return null
                getForcedSigner(address)
            }
            is WalletConnectTransactionSigner.Sender -> {
                val address = signer.address.decodedAddress ?: return null
                getTransactionSigner(address)
            }
            else -> null
        }
    }

    private suspend fun getForcedSigner(address: String): TransactionSigner? {
        val signerRegistrationType = getAccountRegistrationType(address) ?: return null
        return when (signerRegistrationType) {
            AccountRegistrationType.Algo25 -> TransactionSigner.Algo25(address)
            AccountRegistrationType.LedgerBle -> getLedgerSigner(address)
            AccountRegistrationType.NoAuth -> SignerNotFound.NoAuth(address)
            AccountRegistrationType.HdKey -> null // TODO
        }
    }

    private suspend fun getLedgerSigner(address: String): TransactionSigner? {
        val ledgerBleAccount = getLedgerBleAccount(address) ?: return null
        return TransactionSigner.LedgerBle(
            address = address,
            bluetoothAddress = ledgerBleAccount.deviceMacAddress,
            positionInLedger = ledgerBleAccount.indexInLedger
        )
    }
}
