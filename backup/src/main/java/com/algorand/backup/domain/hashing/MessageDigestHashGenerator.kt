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

import com.algorand.backup.domain.model.BackupGlobalHash
import com.algorand.backup.domain.model.BackupItemKey
import com.algorand.backup.domain.model.ItemHash
import com.algorand.backup.domain.model.ManifestItem
import java.security.MessageDigest
import javax.inject.Inject

internal class MessageDigestHashGenerator @Inject constructor() : BackupHashGenerator {

    override fun generateItemHash(encryptedBytes: ByteArray): ItemHash {
        val hash = sha256(encryptedBytes)
        return ItemHash("sha256:$hash")
    }

    override fun generateGlobalHash(items: Map<BackupItemKey, ManifestItem>): BackupGlobalHash {
        val concatenation = items.keys
            .sortedBy { it.value }
            .joinToString("") { key ->
                val item = items[key]!!
                "${key.value}|${item.type.name}|${item.version}|${item.status.name}|${item.hash.value}"
            }
        val hash = sha256(concatenation.toByteArray(Charsets.UTF_8))
        return BackupGlobalHash("sha256:$hash")
    }

    private fun sha256(input: ByteArray): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input)
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
