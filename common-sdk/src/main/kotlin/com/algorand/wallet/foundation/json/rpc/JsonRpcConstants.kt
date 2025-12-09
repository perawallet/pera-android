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

package com.algorand.wallet.foundation.json.rpc

object JsonRpcConstants {
    const val VERSION = "2.0"

    object ErrorCodes {

        /** Server error: Reserved for implementation-defined server-errors. */
        const val SERVER_ERROR_START = -32099

        /** Server error: Reserved for implementation-defined server-errors. */
        const val SERVER_ERROR_END = -32000

        /** Invalid params: Invalid method parameter(s). */
        const val INVALID_PARAMS = -32602

        /** Method not found: The method does not exist / is not available. */
        const val METHOD_NOT_FOUND = -32601

        /** Invalid Request: The JSON sent is not a valid Request object. */
        const val INVALID_REQUEST = -32600

        /** Internal JSON-RPC error. */
        const val INTERNAL_ERROR = -32603

        /** Parse error: Invalid JSON was received by the server. */
        const val PARSE_ERROR = -32700
    }
}
