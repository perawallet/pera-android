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

package com.algorand.backup.domain.usecase

import com.algorand.backup.domain.model.DerivedKeyMaterial
import com.algorand.backup.domain.repository.BackupRepository
import com.algorand.backup.domain.security.BackupRequestSigner
import com.algorand.backup.domain.security.NonceGenerator
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

internal class RegisterBackupUseCase @Inject constructor(
    private val getBackupWalletPrivateKey: GetBackupWalletPrivateKey,
    private val requestSigner: BackupRequestSigner,
    private val nonceGenerator: NonceGenerator,
    private val backupRepository: BackupRepository,
    private val provideBackupDeviceId: ProvideBackupDeviceId
) : RegisterBackup {

    override suspend fun invoke(keyMaterial: DerivedKeyMaterial, mnemonic: String): PeraResult<Unit> {
        val walletPrivateKey = getBackupWalletPrivateKey(mnemonic)
            ?: return PeraResult.Error(IllegalStateException("Failed to derive wallet private key from mnemonic"))

        val deviceId = provideBackupDeviceId()
        val proof = requestSigner.createRegistrationProof(
            authPublicKey = keyMaterial.authPublicKey,
            backupId = keyMaterial.backupId,
            deviceId = deviceId,
            nonce = nonceGenerator.generate(),
            walletPrivateKey = walletPrivateKey
        )
        return backupRepository.register(proof)
    }
}
