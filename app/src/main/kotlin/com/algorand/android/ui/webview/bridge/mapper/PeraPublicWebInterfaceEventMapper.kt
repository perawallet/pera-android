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

import com.algorand.android.ui.webview.bridge.BridgeJsEventNames.GET_PUBLIC_SETTINGS
import com.algorand.android.ui.webview.bridge.model.event.PeraPublicWebInterfaceEvent
import com.algorand.android.ui.webview.bridge.model.event.PeraPublicWebInterfaceEvent.EventType
import com.algorand.android.ui.webview.bridge.model.event.PeraWebInterfaceEventResult
import com.algorand.android.ui.webview.bridge.model.event.PeraWebInterfaceEventResult.Result
import com.algorand.android.ui.webview.bridge.model.event.PeraWebInterfaceEventResult.Result.Error
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.foundation.json.JsonSerializer
import com.algorand.wallet.foundation.json.rpc.JsonRpcConstants.ErrorCodes
import com.algorand.wallet.foundation.json.rpc.JsonRpcConstants.ErrorCodes.INTERNAL_ERROR
import com.algorand.wallet.foundation.json.rpc.JsonRpcRequest
import javax.inject.Inject

class PeraPublicWebInterfaceEventMapper @Inject constructor(
    private val jsonSerializer: JsonSerializer
) : PeraWebInterfaceEventMapper {

    override fun mapRequests(params: String): List<PeraWebInterfaceEventResult> {
        val request = jsonSerializer.fromJson(params, JsonRpcRequest::class.java) ?: return emptyList()
        return listOf(getEventResult(request))
    }

    private fun getEventResult(request: JsonRpcRequest): PeraWebInterfaceEventResult {
        val result = when (val eventTypeResult = parseEventType(request)) {
            is PeraResult.Success -> Result.Success(eventTypeResult.data)
            is PeraResult.Error -> Error(eventTypeResult.code ?: INTERNAL_ERROR)
        }
        return PeraWebInterfaceEventResult(request.id, result)
    }

    private fun parseEventType(request: JsonRpcRequest): PeraResult<PeraPublicWebInterfaceEvent> {
        return when (request.method) {
            GET_PUBLIC_SETTINGS -> {
                val requestId = request.id ?: return PeraResult.Error(Exception(), ErrorCodes.INVALID_REQUEST)
                PeraResult.Success(PeraPublicWebInterfaceEvent(EventType.GetPublicSettings(requestId)))
            }
            else -> PeraResult.Error(Exception(), ErrorCodes.METHOD_NOT_FOUND)
        }
    }
}
