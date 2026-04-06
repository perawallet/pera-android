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

package com.algorand.backup.domain.security

import com.algorand.backup.domain.model.Argon2idConfig
import com.algorand.backup.domain.model.BackupId
import com.algorand.backup.domain.model.DerivedKeyMaterial
import com.algorand.backup.domain.model.KeyDerivationInput
import com.algorand.backup.domain.model.SensitiveBytes
import com.algorand.wallet.foundation.PeraResult

internal interface HkdfKeyManager {
    fun deriveChildKey(masterKey: SensitiveBytes, info: ByteArray): SensitiveBytes
}

internal interface Ed25519KeyManager {
    fun deriveKeyPair(seed: SensitiveBytes): Pair<SensitiveBytes, SensitiveBytes>
}

internal interface BackupEncryptionManager {
    fun importKey(encryptionKey: SensitiveBytes): PeraResult<Unit>
    fun encrypt(plaintext: ByteArray, itemKey: String): PeraResult<ByteArray>
    fun decrypt(ciphertext: ByteArray, itemKey: String): PeraResult<ByteArray>
    fun deleteKey()
}

internal interface BackupIdManager {
    fun createBackupId(publicKey: SensitiveBytes): PeraResult<BackupId>
}

internal interface BackupKeyDerivationManager {
    fun deriveKeys(input: KeyDerivationInput): PeraResult<DerivedKeyMaterial>
}

internal interface ArgonKeyManager {
    fun deriveMasterKey(password: SensitiveBytes, salt: ByteArray, config: Argon2idConfig): SensitiveBytes
}
