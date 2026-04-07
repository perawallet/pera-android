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

package com.algorand.backup.domain.model

class SensitiveBytes(private val bytes: ByteArray) : AutoCloseable {

    /**
     * Returns the raw bytes. Use this only within a short-lived scope.
     */
    fun reveal(): ByteArray = bytes

    /**
     * Overwrites the underlying array with zeros to remove it from the heap.
     */
    override fun close() {
        bytes.fill(0)
    }

    override fun toString(): String = "SensitiveBytes[REDACTED]"

    override fun hashCode(): Int = System.identityHashCode(this)

    override fun equals(other: Any?): Boolean = this === other
}
