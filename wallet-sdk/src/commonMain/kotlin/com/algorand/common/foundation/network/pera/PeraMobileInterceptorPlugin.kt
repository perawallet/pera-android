/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.common.foundation.network.pera

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.request.HttpSendPipeline
import io.ktor.client.request.headers
import io.ktor.http.HttpMessageBuilder
import io.ktor.http.URLBuilder
import io.ktor.http.set
import io.ktor.http.takeFrom
import io.ktor.util.AttributeKey
import io.ktor.util.appendAll

internal class PeraMobileInterceptorPlugin(
    private val getPeraMobileInterceptorConfig: GetPeraMobileInterceptorConfig
) : HttpClientPlugin<Unit, PeraMobileInterceptorPlugin> {

    override val key: AttributeKey<PeraMobileInterceptorPlugin> = AttributeKey("PeraMobileInterceptor")

    override fun prepare(block: Unit.() -> Unit): PeraMobileInterceptorPlugin {
        return PeraMobileInterceptorPlugin(getPeraMobileInterceptorConfig)
    }

    override fun install(plugin: PeraMobileInterceptorPlugin, scope: HttpClient) {
        scope.sendPipeline.intercept(HttpSendPipeline.Before) {
            val config = plugin.getPeraMobileInterceptorConfig()
            context.url.setNodeAwareUrl(config)
            context.setNodeAwareHeaders(config)
            context.setUserAgentHeaders(config.userAgent)
            proceed()
        }
    }

    private fun URLBuilder.setNodeAwareUrl(config: PeraMobileInterceptorPluginConfig) {
        val newUrl = URLBuilder(config.baseUrl)
        newUrl.pathSegments = pathSegments
        newUrl.parameters.appendAll(parameters)
        set {
            takeFrom(newUrl)
        }
    }

    private fun HttpMessageBuilder.setNodeAwareHeaders(config: PeraMobileInterceptorPluginConfig) {
        headers {
            append("X-API-Key", config.apiKey)
        }
    }

    private fun HttpMessageBuilder.setUserAgentHeaders(userAgent: PeraMobileUserAgent) {
        headers {
            append("App-Name", userAgent.appName)
            append("Client-Type", userAgent.clientType)
            append("Device-OS-Version", userAgent.osVersion)
            append("App-Package-Name", userAgent.packageName)
            append("App-Version", userAgent.appVersion)
            append("Device-Model", userAgent.deviceModel)
            append("Accept-Language", userAgent.languageTag)
        }
    }
}

data class PeraMobileInterceptorPluginConfig(
    val baseUrl: String,
    val apiKey: String,
    val userAgent: PeraMobileUserAgent
)
