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

package com.algorand.android.core.transaction.sync

import android.util.Base64
import com.algorand.android.utils.signArbitraryData
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetAlgo25SecretKey
import com.algorand.wallet.account.local.domain.usecase.GetHdSeed
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccount
import com.algorand.wallet.encryption.domain.utils.clearFromMemory
import com.algorand.wallet.algosdk.transaction.sdk.SignHdKeyTransaction
import javax.inject.Inject

fun interface SignArbitraryDataForSyncRequest {
    suspend operator fun invoke(
        signRequestId: String,
        proposerAddress: String
    ): String?
}

internal class SignArbitraryDataForSyncRequestUseCase @Inject constructor(
    private val getLocalAccount: GetLocalAccount,
    private val getAlgo25SecretKey: GetAlgo25SecretKey,
    private val getHdSeed: GetHdSeed,
    private val signHdKeyTransaction: SignHdKeyTransaction
) : SignArbitraryDataForSyncRequest {

    override suspend fun invoke(
        signRequestId: String,
        proposerAddress: String
    ): String? {
        val localAccount = getLocalAccount(proposerAddress) ?: return null
        val messageBytes = signRequestId.toByteArray(Charsets.UTF_8)
        val signatureBytes = when (localAccount) {
            is LocalAccount.Algo25 -> signWithAlgo25(messageBytes, proposerAddress)
            is LocalAccount.HdKey -> signWithHdKey(messageBytes, localAccount)
            else -> null
        } ?: return null
        return Base64.encodeToString(signatureBytes, Base64.NO_WRAP)
    }

    private suspend fun signWithAlgo25(messageBytes: ByteArray, proposerAddress: String): ByteArray? {
        val secretKey = getAlgo25SecretKey(proposerAddress) ?: return null
        return try {
            runCatching { messageBytes.signArbitraryData(secretKey) }.getOrNull()
                ?.takeIf { it.isNotEmpty() }
        } finally {
            secretKey.clearFromMemory()
        }
    }

    private suspend fun signWithHdKey(
        messageBytes: ByteArray,
        hdKeyAccount: LocalAccount.HdKey
    ): ByteArray? {
        val seed = getHdSeed(seedId = hdKeyAccount.seedId) ?: return null
        return try {
            signHdKeyTransaction.signLegacyArbitaryData(
                messageBytes,
                seed,
                hdKeyAccount.account,
                hdKeyAccount.change,
                hdKeyAccount.keyIndex
            )
        } finally {
            seed.clearFromMemory()
        }
    }
}
