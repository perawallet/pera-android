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

package com.algorand.android.ui.backup.di

import com.algorand.backup.domain.security.BackupRegistrationSigner
import com.algorand.wallet.algosdk.transaction.sdk.SignHdKeyTransaction
import javax.inject.Inject

internal class AlgoSdkBackupRegistrationSigner @Inject constructor(
    private val signHdKeyTransaction: SignHdKeyTransaction
) : BackupRegistrationSigner {

    override fun sign(data: ByteArray, secretKey: ByteArray): ByteArray {
        return signHdKeyTransaction.signLegacyArbitaryData(
            transactionByteArray = data,
            seed = secretKey,
            account = DEFAULT_ACCOUNT,
            change = DEFAULT_CHANGE,
            key = DEFAULT_KEY
        ) ?: throw IllegalStateException("Failed to sign arbitrary data")
    }

    private companion object {
        const val DEFAULT_ACCOUNT = 0
        const val DEFAULT_CHANGE = 0
        const val DEFAULT_KEY = 0
    }
}
