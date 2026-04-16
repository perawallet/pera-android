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

package com.algorand.wallet.foundation.network.exceptions

/**
 * Exception that carries a user-facing error message parsed from a Pera API error response.
 * This wraps the message extracted from the `detail` or `fallback_message` fields of the
 * standard Pera API error format.
 */
class PeraApiException(
    val userMessage: String,
    val httpCode: Int? = null,
    cause: Throwable? = null
) : Exception(userMessage, cause)
