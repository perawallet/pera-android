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

package com.algorand.wallet.account.local.domain.model

import com.algorand.wallet.encryption.domain.utils.clearFromMemory
import com.algorand.wallet.foundation.security.SensitiveDataApi

/**
 * ⚠️ WARNING: SENSITIVE DATA CONTAINER ⚠️
 *
 * This class holds raw, decrypted cryptographic entropy in memory.
 * It is a security-critical object and MUST be handled with extreme care.
 *
 * THE RULE: You MUST call invalidate() on any instance of this class
 * immediately after use, preferably in a `try...finally` block, to
 * securely wipe the entropy from memory.
 *
 * @sample hdEntropySample
 *
 * @property entropy The sensitive, plaintext entropy. DO NOT leak this.
 *
 */
@SensitiveDataApi
internal class HdEntropy(
    val seedId: Int,
    val entropy: ByteArray
) {

    fun invalidate() {
        entropy.clearFromMemory()
    }
}

@OptIn(SensitiveDataApi::class)
private fun hdEntropySample() {
    val hdEntropy = HdEntropy(1, ByteArray(32))
    try {
        // Use hdEntropy.entropy here
    } finally {
        hdEntropy.invalidate()
    }
}
