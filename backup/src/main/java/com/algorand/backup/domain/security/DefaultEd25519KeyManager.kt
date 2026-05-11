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

import com.algorand.backup.domain.model.SensitiveBytes
import javax.inject.Inject
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters

internal class DefaultEd25519KeyManager @Inject constructor() : Ed25519KeyManager {

    override fun deriveKeyPair(seed: SensitiveBytes): Pair<SensitiveBytes, SensitiveBytes> {
        val privateKeyParams = Ed25519PrivateKeyParameters(seed.reveal(), 0)
        val publicKeyParams = privateKeyParams.generatePublicKey()
        return Pair(SensitiveBytes(privateKeyParams.encoded), SensitiveBytes(publicKeyParams.encoded))
    }
}
