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

import com.algorand.android.modules.peraserializer.JsonSerializer
import com.algorand.android.ui.webview.bridge.model.CanOpenUriParams
import com.algorand.android.ui.webview.bridge.model.LogAnalyticsEventParams
import com.algorand.android.ui.webview.bridge.model.NotifyUserParams
import com.algorand.android.ui.webview.bridge.model.OpenNativeUriParams
import com.algorand.android.ui.webview.bridge.model.OpenSystemBrowserParams
import com.algorand.android.ui.webview.bridge.model.PushWebViewParams
import com.algorand.android.ui.webview.bridge.model.event.PeraInternalWebInterfaceEvent.EventType
import com.algorand.wallet.logger.PeraErrorLogger
import javax.inject.Inject

internal class DefaultPeraWebInterfaceEventMapper @Inject constructor(
    private val jsonSerializer: JsonSerializer,
    private val notifyUserEventMapper: PeraWebInterfaceNotifyUserEventMapper,
    private val errorLogger: PeraErrorLogger
) : PeraWebInterfaceEventMapper {

    override fun mapPushWebViewEvent(params: String): EventType.PushPublicWebView? {
        return params.tryParse<PushWebViewParams>()?.run {
            EventType.PushPublicWebView(url, title, projectId, isFavorite)
        }
    }

    override fun mapOpenSystemBrowserEvent(params: String): EventType.OpenSystemBrowser? {
        return params.tryParse<OpenSystemBrowserParams>()?.run {
            EventType.OpenSystemBrowser(url)
        }
    }

    override fun mapCanOpenUriEvent(params: String): EventType.CanOpenUri? {
        return params.tryParse<CanOpenUriParams>()?.run {
            EventType.CanOpenUri(uri)
        }
    }

    override fun mapOpenNativeUriEvent(params: String): EventType.OpenNativeUri? {
        return params.tryParse<OpenNativeUriParams>()?.run {
            EventType.OpenNativeUri(uri)
        }
    }

    override fun mapLogAnalyticsEvent(params: String): EventType.LogAnalyticsEvent? {
        return params.tryParse<LogAnalyticsEventParams>()?.run {
            EventType.LogAnalyticsEvent(name, this.params)
        }
    }

    override fun mapNotifyUserEvent(params: String): EventType.NotifyUser? {
        return params.tryParse<NotifyUserParams>()?.run {
            notifyUserEventMapper(this)
        }
    }

    private inline fun <reified T> String.tryParse(): T? {
        return jsonSerializer.fromJson(this, T::class.java).also { result ->
            if (result == null) {
                logParsingError(T::class.java.simpleName, this)
            }
        }
    }

    private fun logParsingError(eventName: String, params: String) {
        errorLogger.logError("Failed to parse params for event: $eventName, params: $params")
    }
}
