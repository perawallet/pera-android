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

import com.algorand.android.ui.webview.bridge.BridgeJsEventNames.CAN_OPEN_URI
import com.algorand.android.ui.webview.bridge.BridgeJsEventNames.CLOSE_WEB_VIEW
import com.algorand.android.ui.webview.bridge.BridgeJsEventNames.GET_ADDRESSES
import com.algorand.android.ui.webview.bridge.BridgeJsEventNames.GET_PUBLIC_SETTINGS
import com.algorand.android.ui.webview.bridge.BridgeJsEventNames.GET_SETTINGS
import com.algorand.android.ui.webview.bridge.BridgeJsEventNames.LOG_ANALYTICS_EVENT
import com.algorand.android.ui.webview.bridge.BridgeJsEventNames.NOTIFY_USER
import com.algorand.android.ui.webview.bridge.BridgeJsEventNames.OPEN_NATIVE_URI
import com.algorand.android.ui.webview.bridge.BridgeJsEventNames.OPEN_SYSTEM_BROWSER
import com.algorand.android.ui.webview.bridge.BridgeJsEventNames.PUSH_WEB_VIEW
import com.algorand.android.ui.webview.bridge.model.CanOpenUriParams
import com.algorand.android.ui.webview.bridge.model.LogAnalyticsEventParams
import com.algorand.android.ui.webview.bridge.model.NotifyUserParams
import com.algorand.android.ui.webview.bridge.model.OpenNativeUriParams
import com.algorand.android.ui.webview.bridge.model.OpenSystemBrowserParams
import com.algorand.android.ui.webview.bridge.model.PushWebViewParams
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Command.CloseWebView
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Command.LogAnalyticsEvent
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Command.NotifyUser
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Command.OpenNativeUri
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Command.OpenSystemBrowser
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Command.PushPublicWebView
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Query
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Query.QueryType.CanOpenUri
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Query.QueryType.GetAddresses
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Query.QueryType.GetPublicSettings
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.Query.QueryType.GetSettings
import com.algorand.android.ui.webview.bridge.model.event.PeraWebInterfaceEventResult
import com.algorand.android.ui.webview.bridge.model.event.PeraWebInterfaceEventResult.Result
import com.algorand.android.ui.webview.bridge.model.event.PeraWebInterfaceEventResult.Result.Error
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.foundation.json.JsonSerializer
import com.algorand.wallet.foundation.json.rpc.JsonRpcConstants
import com.algorand.wallet.foundation.json.rpc.JsonRpcConstants.ErrorCodes.INTERNAL_ERROR
import com.algorand.wallet.foundation.json.rpc.JsonRpcRequest
import com.algorand.wallet.foundation.json.rpc.JsonRpcRequestList
import com.algorand.wallet.logger.PeraErrorLogger
import javax.inject.Inject

class PeraInternalWebInterfaceEventMapper @Inject constructor(
    private val jsonSerializer: JsonSerializer,
    private val notifyUserEventMapper: PeraWebInterfaceNotifyUserEventMapper,
    private val errorLogger: PeraErrorLogger
) : PeraWebInterfaceEventMapper {

    override fun mapRequests(params: String): List<PeraWebInterfaceEventResult> {
        val requests = params.tryParse<JsonRpcRequestList>()
            ?: params.tryParse<JsonRpcRequest>()?.let { listOf(it) }
            ?: return emptyList()
        return requests.map { getEventResult(it) }
    }

    private fun getEventResult(request: JsonRpcRequest): PeraWebInterfaceEventResult {
        val result = when (val eventTypeResult = parseEventType(request)) {
            is PeraResult.Success -> Result.Success(eventTypeResult.data)
            is PeraResult.Error -> Error(eventTypeResult.code ?: INTERNAL_ERROR)
        }
        return PeraWebInterfaceEventResult(request.id, result)
    }

    private fun parseEventType(request: JsonRpcRequest): PeraResult<PeraInternalWebInterfaceEvent> {
        val eventType = when (request.method) {
            PUSH_WEB_VIEW -> mapPushWebViewEvent(request)
            OPEN_SYSTEM_BROWSER -> mapOpenSystemBrowserEvent(request)
            CAN_OPEN_URI -> mapCanOpenUriEvent(request)
            OPEN_NATIVE_URI -> mapOpenNativeUriEvent(request)
            NOTIFY_USER -> mapNotifyUserEvent(request)
            GET_ADDRESSES -> request.id?.let { Query(it, GetAddresses) }
            GET_SETTINGS -> request.id?.let { Query(it, GetSettings) }
            LOG_ANALYTICS_EVENT -> mapLogAnalyticsEvent(request)
            CLOSE_WEB_VIEW -> CloseWebView
            GET_PUBLIC_SETTINGS -> request.id?.let { Query(it, GetPublicSettings) }
            else -> {
                logError(request.id, "Unknown event method: ${request.method}")
                return PeraResult.Error(Exception(), JsonRpcConstants.ErrorCodes.METHOD_NOT_FOUND)
            }
        }
        return if (eventType == null) {
            logError(request.id, "Invalid method params: ${request.params}")
            PeraResult.Error(Exception(), JsonRpcConstants.ErrorCodes.INVALID_PARAMS)
        } else {
            PeraResult.Success(eventType)
        }
    }

    private fun mapPushWebViewEvent(request: JsonRpcRequest): PushPublicWebView? {
        return request.params?.tryParse<PushWebViewParams>()?.run {
            PushPublicWebView(url, title, projectId, isFavorite)
        }
    }

    private fun mapOpenSystemBrowserEvent(request: JsonRpcRequest): OpenSystemBrowser? {
        return request.params?.tryParse<OpenSystemBrowserParams>()?.run {
            OpenSystemBrowser(url)
        }
    }

    private fun mapCanOpenUriEvent(request: JsonRpcRequest): Query? {
        return request.params?.tryParse<CanOpenUriParams>()?.run {
            Query(request.id ?: return null, CanOpenUri(uri))
        }
    }

    private fun mapOpenNativeUriEvent(request: JsonRpcRequest): OpenNativeUri? {
        return request.params?.tryParse<OpenNativeUriParams>()?.run {
            OpenNativeUri(uri)
        }
    }

    private fun mapLogAnalyticsEvent(request: JsonRpcRequest): LogAnalyticsEvent? {
        return request.params?.tryParse<LogAnalyticsEventParams>()?.run {
            LogAnalyticsEvent(name, this.params)
        }
    }

    private fun mapNotifyUserEvent(request: JsonRpcRequest): NotifyUser? {
        return request.params?.tryParse<NotifyUserParams>()?.run {
            notifyUserEventMapper(this)
        }
    }

    private inline fun <reified T> String.tryParse(): T? = jsonSerializer.fromJson(this, T::class.java)

    private inline fun <reified T> Any.tryParse(): T? = jsonSerializer.fromJson(this, T::class.java)

    private fun logError(id: Long?, message: String) {
        errorLogger.logError("$LOG_TAG requestId: $id message: $message")
    }

    private companion object {
        val LOG_TAG: String = PeraInternalWebInterfaceEventMapper::class.java.simpleName
    }
}
