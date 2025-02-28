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

package com.algorand.wallet.analytics.data.service

import com.algorand.wallet.analytics.domain.service.PeraReferrerInstallClient
import com.algorand.wallet.analytics.domain.service.PeraReferrerManager
import com.algorand.wallet.analytics.domain.service.PeraReferrerQueryParamParser
import com.algorand.wallet.analytics.domain.usecase.SaveReferrerData
import javax.inject.Inject

internal class PeraReferrerManagerImpl @Inject constructor(
    private val referrerClient: PeraReferrerInstallClient,
    private val saveReferrerData: SaveReferrerData,
    private val peraReferrerQueryParamParser: PeraReferrerQueryParamParser
) : PeraReferrerManager {
    override suspend fun fetchInstallReferrer() {
        val referrerUrl = referrerClient.getReferrerUrl()
        referrerUrl?.let { url ->
            saveReferrerData(url)
        }
    }

    override suspend fun saveReferrerData(referrerUrl: String) {
        val referrerData = peraReferrerQueryParamParser.getReferrerData(referrerUrl)
        saveReferrerData(referrerData)
    }
}
