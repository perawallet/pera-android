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

package com.algorand.wallet.foundation.security

/**
 * Marks APIs that handle sensitive, plaintext cryptographic data.
 *
 * Any function or class using an API marked with `@SensitiveDataApi` must also
 * be annotated with `@OptIn(SensitiveDataApi::class)` or `@SensitiveDataApi`.
 * This forces the developer to acknowledge that they are responsible for
 * handling and clearing the sensitive data according to security best practices.
 *
 * @sample sensitiveDataApiSample
 */
@RequiresOptIn(
    message = "This API handles sensitive data that must be cleared from memory after use. Ensure you understand the security implications.",
    level = RequiresOptIn.Level.ERROR
)
@Retention(AnnotationRetention.BINARY)
annotation class SensitiveDataApi

private fun sensitiveDataApiSample() {
    @OptIn(SensitiveDataApi::class)
    fun sensitiveFunction() {
        val sensitiveData = ByteArray(32) // Example sensitive data
        try {
            // Use sensitiveData here
        } finally {
            sensitiveData.fill(0) // Clear sensitive data from memory
        }
    }
}
