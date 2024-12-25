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

package co.algorand.app.di

import com.algorand.common.foundation.network.algod.AlgodInterceptorPluginConfig
import com.algorand.common.foundation.network.algod.GetAlgodInterceptorConfig
import com.algorand.common.foundation.network.indexer.GetIndexerInterceptorConfig
import com.algorand.common.foundation.network.indexer.IndexerInterceptorPluginConfig
import com.algorand.common.foundation.network.pera.GetPeraMobileInterceptorConfig
import com.algorand.common.foundation.network.pera.PeraMobileInterceptorPluginConfig
import com.algorand.common.foundation.network.pera.PeraMobileUserAgent
import org.koin.dsl.module

val accountInformationModule = module {
    factory<GetIndexerInterceptorConfig> {
        GetIndexerInterceptorConfig {
            IndexerInterceptorPluginConfig(
                baseUrl = "-",
                apiKey = "-"
            )
        }
    }
    factory<GetPeraMobileInterceptorConfig> {
        GetPeraMobileInterceptorConfig {
            PeraMobileInterceptorPluginConfig(
                baseUrl = "-",
                apiKey = "-",
                userAgent = PeraMobileUserAgent(
                    packageName = "-",
                    appVersion = "-",
                    appName = "-",
                    osVersion = "-",
                    deviceModel = "-",
                    languageTag = "-",
                    clientType = "-"
                )
            )
        }
    }
    factory<GetAlgodInterceptorConfig> {
        GetAlgodInterceptorConfig {
            AlgodInterceptorPluginConfig(
                baseUrl = "-",
                apiKey = "-"
            )
        }
    }
}
