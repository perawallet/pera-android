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

import android.util.Base64
import com.algorand.backup.domain.model.Argon2idHash
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

// RFC 9106 PHC string format: $argon2id$v=<version>$m=<memory>,t=<time>,p=<parallelism>$<salt>$<hash>
internal class DefaultArgon2idEncoder @Inject constructor(
    private val validator: Argon2idHashValidator
) : Argon2idEncoder {

    override fun encode(argon2idHash: Argon2idHash): String {
        val encodedSalt = base64Encode(argon2idHash.salt)
        val encodedHash = base64Encode(argon2idHash.hash)
        return "\$argon2id\$v=${argon2idHash.version}" +
            "\$m=${argon2idHash.config.memoryCost},t=${argon2idHash.config.timeCost},p=${argon2idHash.config.parallelism}" +
            "\$$encodedSalt" +
            "\$$encodedHash"
    }

    override fun decode(encoded: String): PeraResult<Argon2idHash> {
        val parts = encoded.split("$").filter { it.isNotEmpty() }

        validator.validateParts(parts)?.let { error ->
            return PeraResult.Error(IllegalArgumentException(error))
        }

        val version = validator.parseVersion(parts[1]).unwrapOrReturn { return it }
        val config = validator.parseConfig(parts[2]).unwrapOrReturn { return it }
        val salt = validator.decodeBase64(parts[3], "salt").unwrapOrReturn { return it }
        val hash = validator.decodeBase64(parts[4], "hash").unwrapOrReturn { return it }

        return PeraResult.Success(
            Argon2idHash(
                version = version,
                config = config.copy(outputLength = hash.size),
                salt = salt,
                hash = hash
            )
        )
    }

    private fun base64Encode(data: ByteArray): String {
        return Base64.encodeToString(data, Base64.NO_WRAP or Base64.NO_PADDING)
    }

    private inline fun <T : Any> PeraResult<T>.unwrapOrReturn(
        onError: (PeraResult.Error) -> Nothing
    ): T {
        return when (this) {
            is PeraResult.Success -> data
            is PeraResult.Error -> onError(this)
        }
    }
}
