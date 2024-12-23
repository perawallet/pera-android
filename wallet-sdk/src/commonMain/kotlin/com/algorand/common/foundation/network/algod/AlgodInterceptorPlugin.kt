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

package com.algorand.common.foundation.network.algod

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

internal class AlgodInterceptorPlugin(
    private val getAlgodInterceptorConfig: GetAlgodInterceptorConfig
) : HttpClientPlugin<Unit, AlgodInterceptorPlugin> {

    override val key: AttributeKey<AlgodInterceptorPlugin> = AttributeKey("AlgodInterceptor")

    override fun prepare(block: Unit.() -> Unit): AlgodInterceptorPlugin {
        return AlgodInterceptorPlugin(getAlgodInterceptorConfig)
    }

    override fun install(plugin: AlgodInterceptorPlugin, scope: HttpClient) {
        scope.sendPipeline.intercept(HttpSendPipeline.Before) {
            val config = plugin.getAlgodInterceptorConfig()
            context.url.setNodeAwareUrl(config)
            context.setNodeAwareHeaders(config)
            proceed()
        }
    }

    private fun URLBuilder.setNodeAwareUrl(config: AlgodInterceptorPluginConfig) {
        val newUrl = URLBuilder(config.baseUrl)
        newUrl.pathSegments = pathSegments
        newUrl.parameters.appendAll(parameters)
        set {
            takeFrom(newUrl)
        }
    }

    private fun HttpMessageBuilder.setNodeAwareHeaders(config: AlgodInterceptorPluginConfig) {
        headers {
            append("X-Algo-API-Token", config.apiKey)
        }
    }
}

data class AlgodInterceptorPluginConfig(
    val baseUrl: String,
    val apiKey: String
)
