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

package com.algorand.android.ui.webview.bridge.mapper

import com.algorand.wallet.foundation.json.rpc.JsonRpcRequest
import com.algorand.wallet.foundation.json.rpc.JsonRpcResponse

interface PeraWebInterfaceEventResponseMapper {
    fun mapSuccessResponse(id: Long, response: Any): JsonRpcResponse
    fun mapErrorResponse(id: Long, code: Int): JsonRpcResponse
    fun mapRequest(methodName: String, params: Any?): JsonRpcRequest
    fun mapResponseMessage(responses: List<Any>): String
}
