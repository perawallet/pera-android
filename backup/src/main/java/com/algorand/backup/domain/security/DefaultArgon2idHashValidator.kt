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
import com.algorand.backup.domain.model.Argon2idConfig
import com.algorand.wallet.foundation.PeraResult
import javax.inject.Inject

internal class DefaultArgon2idHashValidator @Inject constructor() : Argon2idHashValidator {

    override fun validateParts(parts: List<String>): String? {
        if (parts.size != EXPECTED_PART_COUNT) {
            return "Invalid format: expected $EXPECTED_PART_COUNT parts, got ${parts.size}"
        }
        if (parts[0] != ALGORITHM_ID) {
            return "Unsupported algorithm: ${parts[0]}"
        }
        return null
    }

    override fun parseVersion(field: String): PeraResult<Int> {
        if (!field.startsWith("v=")) {
            return PeraResult.Error(IllegalArgumentException("Invalid version field: $field"))
        }
        val version = field.removePrefix("v=").toIntOrNull()
            ?: return PeraResult.Error(IllegalArgumentException("Invalid version field: $field"))
        return PeraResult.Success(version)
    }

    override fun parseConfig(field: String): PeraResult<Argon2idConfig> {
        val params = mutableMapOf<String, String>()
        for (param in field.split(",")) {
            val kv = param.split("=", limit = 2)
            if (kv.size != 2) {
                return PeraResult.Error(IllegalArgumentException("Invalid config field: $field"))
            }
            params[kv[0]] = kv[1]
        }

        val memoryCost = params["m"]?.toIntOrNull()
        val timeCost = params["t"]?.toIntOrNull()
        val parallelism = params["p"]?.toIntOrNull()

        if (memoryCost == null || timeCost == null || parallelism == null) {
            return PeraResult.Error(IllegalArgumentException("Invalid config field: $field"))
        }
        if (memoryCost <= 0 || timeCost <= 0 || parallelism <= 0) {
            return PeraResult.Error(IllegalArgumentException("Config values must be positive"))
        }

        return PeraResult.Success(
            Argon2idConfig(
                timeCost = timeCost,
                memoryCost = memoryCost,
                parallelism = parallelism,
                outputLength = 0
            )
        )
    }

    override fun decodeBase64(data: String, fieldName: String): PeraResult<ByteArray> {
        val bytes = try {
            Base64.decode(data, Base64.NO_WRAP or Base64.NO_PADDING)
        } catch (e: IllegalArgumentException) {
            return PeraResult.Error(IllegalArgumentException("Invalid base64 $fieldName"))
        }
        if (bytes.isEmpty()) {
            return PeraResult.Error(IllegalArgumentException("$fieldName must not be empty"))
        }
        return PeraResult.Success(bytes)
    }

    private companion object {
        const val ALGORITHM_ID = "argon2id"
        const val EXPECTED_PART_COUNT = 5
    }
}
