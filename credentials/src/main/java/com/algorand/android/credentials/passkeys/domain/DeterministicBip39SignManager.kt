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
import com.algorand.wallet.account.local.domain.usecase.GetAllHdSeedFirstAddresses
import com.algorand.wallet.account.local.domain.usecase.GetHdEntropy
import com.algorand.wallet.encryption.domain.utils.clearFromMemory
import foundation.algorand.deterministicP256.DeterministicP256
import java.security.KeyPair
import javax.inject.Inject

internal class DeterministicBip39SignManager @Inject constructor(
    private val deterministicSigner: DeterministicP256,
    private val getAllHdSeedFirstAddresses: GetAllHdSeedFirstAddresses,
    private val getHdEntropy: GetHdEntropy
) : Bip39SignManager {

    override suspend fun sign(address: String, origin: String, userHandle: String, payload: ByteArray): ByteArray? {
        val keyPair = deriveKeyPair(address, origin, userHandle) ?: return null
        return deterministicSigner.signWithDomainSpecificKeyPair(keyPair, payload)
    }

    override suspend fun deriveKeyPair(address: String, origin: String, userHandle: String): KeyPair? {
        val allFirstAddresses = getAllHdSeedFirstAddresses()
        val seedId = allFirstAddresses.firstOrNull { it.firstAddress == address }?.seedId ?: return null
        val entropy = getHdEntropy(seedId) ?: return null
        return try {
            val key = deterministicSigner.genDerivedMainKeyWithBIP39(Mnemonics.MnemonicCode(entropy).joinToString(" "))
            deterministicSigner.genDomainSpecificKeypair(key, origin, userHandle)
        } catch (e: Exception) {
            null
        } finally {
            entropy.clearFromMemory()
        }
    }

    override fun deriveCredentialId(keyPair: KeyPair): ByteArray {
        val publicKeyBytes = keyPair.public.encoded
        val md = PeraMessageDigest.getInstance()
        return md.digest(publicKeyBytes)
    }
}
