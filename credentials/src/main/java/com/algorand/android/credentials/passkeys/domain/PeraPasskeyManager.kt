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

package com.algorand.android.credentials.passkeys.domain

import cash.z.ecc.android.bip39.Mnemonics
import com.algorand.wallet.account.local.domain.usecase.GetHdEntropy
import com.algorand.wallet.encryption.domain.utils.clearFromMemory
import foundation.algorand.deterministicP256.DeterministicP256
import java.security.KeyPair

internal class PeraPasskeyManager(
    private val xPasskey: DeterministicP256,
    private val getHdEntropy: GetHdEntropy
) : PasskeyManager {

    override suspend fun signPasskey(seedId: Int, origin: String, userHandle: String, payload: ByteArray): ByteArray {
        return xPasskey.signWithDomainSpecificKeyPair(derivePasskey(seedId, origin, userHandle), payload)
    }

    override suspend fun derivePasskey(seedId: Int, origin: String, userHandle: String): KeyPair {
        val entropy = getHdEntropy(seedId)
        val key = xPasskey.genDerivedMainKeyWithBIP39(Mnemonics.MnemonicCode(entropy!!).joinToString(" "))
        entropy.clearFromMemory()
        return xPasskey.genDomainSpecificKeypair(key, origin, userHandle)
    }
}
