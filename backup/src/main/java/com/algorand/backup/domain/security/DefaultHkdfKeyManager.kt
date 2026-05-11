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
import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.generators.HKDFBytesGenerator
import org.bouncycastle.crypto.params.HKDFParameters

internal class DefaultHkdfKeyManager @Inject constructor() : HkdfKeyManager {

    override fun deriveChildKey(masterKey: SensitiveBytes, info: ByteArray): SensitiveBytes {
        val output = ByteArray(CHILD_KEY_LENGTH)
        val hkdf = HKDFBytesGenerator(SHA256Digest())
        hkdf.init(HKDFParameters(masterKey.reveal(), null, info))
        hkdf.generateBytes(output, 0, output.size)
        return SensitiveBytes(output)
    }

    companion object {
        private const val CHILD_KEY_LENGTH = 32
    }
}
