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

package com.algorand.backup.domain.hashing

import java.security.MessageDigest
import javax.inject.Inject

internal class MessageDigestHashGenerator @Inject constructor() : BackupHashGenerator {

    override fun generateHash(items: List<Any>): String {
        return generateHash(items.joinToString(separator = "|"))
    }

    override fun generateHash(item: Any): String {
        return generate(item.toString())
    }

    private fun generate(input: String): String {
        val bytes = MessageDigest
            .getInstance("SHA-256")
            .digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
