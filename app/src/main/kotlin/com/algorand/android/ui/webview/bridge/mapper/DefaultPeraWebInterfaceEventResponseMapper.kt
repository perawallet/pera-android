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

import android.util.Base64
import com.algorand.wallet.foundation.json.JsonSerializer
import com.algorand.wallet.foundation.json.rpc.JsonRpcError
import com.algorand.wallet.foundation.json.rpc.JsonRpcRequest
import com.algorand.wallet.foundation.json.rpc.JsonRpcResponse
import javax.inject.Inject

internal class DefaultPeraWebInterfaceEventResponseMapper @Inject constructor(
    private val jsonSerializer: JsonSerializer
) : PeraWebInterfaceEventResponseMapper {

    override fun mapSuccessResponse(id: Long, response: Any): JsonRpcResponse {
        return JsonRpcResponse(
            id = id,
            result = getPayloadMessage(response),
            error = null
        )
    }

    override fun mapErrorResponse(id: Long, code: Int): JsonRpcResponse {
        return JsonRpcResponse(
            id = id,
            result = null,
            error = JsonRpcError(code = code, message = null)
        )
    }

    override fun mapRequest(methodName: String, params: Any?): JsonRpcRequest {
        return JsonRpcRequest(
            id = null,
            method = methodName,
            params = null
        )
    }

    override fun mapResponseMessage(responses: List<Any>): String {
        return if (responses.size == 1) {
            jsonSerializer.toJson(responses.first())
        } else {
            jsonSerializer.toJson(responses)
        }
    }

    private fun getPayloadMessage(response: Any): String {
        val payloadJson = jsonSerializer.toJson(response)
        return Base64.encodeToString(payloadJson.toByteArray(), Base64.DEFAULT or Base64.NO_PADDING or Base64.NO_WRAP)
    }
}
