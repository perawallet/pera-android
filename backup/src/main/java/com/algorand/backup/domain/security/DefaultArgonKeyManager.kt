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
import com.algorand.backup.domain.model.SensitiveBytes
import javax.inject.Inject
import org.bouncycastle.crypto.generators.Argon2BytesGenerator
import org.bouncycastle.crypto.params.Argon2Parameters

internal class DefaultArgonKeyManager @Inject constructor() : ArgonKeyManager {

    override fun deriveMasterKey(password: SensitiveBytes, salt: ByteArray, config: Argon2idConfig): SensitiveBytes {
        val params = Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
            .withSalt(salt)
            .withIterations(config.timeCost)
            .withMemoryAsKB(config.memoryCost)
            .withParallelism(config.parallelism)
            .build()

        val output = ByteArray(config.outputLength)
        val generator = Argon2BytesGenerator()
        generator.init(params)
        generator.generateBytes(password.reveal(), output)
        return SensitiveBytes(output)
    }
}
