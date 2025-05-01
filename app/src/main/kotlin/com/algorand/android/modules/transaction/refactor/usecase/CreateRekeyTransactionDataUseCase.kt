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

package com.algorand.android.modules.transaction.refactor.usecase

import com.algorand.android.models.TransactionSignData
import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLite
import com.algorand.wallet.account.core.domain.usecase.GetTransactionSigner
import javax.inject.Inject

internal class CreateRekeyTransactionDataUseCase @Inject constructor(
    private val getTransactionSigner: GetTransactionSigner,
    private val getAccountLite: GetAccountLite
) : CreateRekeyTransactionData {

    override suspend fun invoke(address: String, rekeyToAddress: String): TransactionSignData.Rekey? {
        val senderAccountLite = getAccountLite(address) ?: return null
        val senderInfo = senderAccountLite.cachedInfo ?: return null
        return TransactionSignData.Rekey(
            senderAccountAddress = address,
            senderAuthAddress = senderInfo.rekeyAuthAddress,
            signer = getTransactionSigner(address),
            senderAccountName = senderAccountLite.customName,
            rekeyAdminAddress = rekeyToAddress
        )
    }
}
