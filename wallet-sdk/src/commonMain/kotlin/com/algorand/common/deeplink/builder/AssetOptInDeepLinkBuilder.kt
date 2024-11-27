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

package com.algorand.common.deeplink.builder

import com.algorand.common.deeplink.model.DeepLink
import com.algorand.common.deeplink.model.DeepLinkPayload

internal class AssetOptInDeepLinkBuilder : DeepLinkBuilder {

    override fun doesDeeplinkMeetTheRequirements(payload: DeepLinkPayload): Boolean {
        return with(payload) {
            val doesDeeplinkHaveAssetOptInQueries = assetId != null && amount == "0"
            doesDeeplinkHaveAssetOptInQueries &&
                accountAddress == null &&
                walletConnectUrl == null &&
                note == null &&
                xnote == null &&
                url == null &&
                label == null &&
                webImportQrCode == null &&
                notificationGroupType == null
        }
    }

    override fun createDeepLink(payload: DeepLinkPayload): DeepLink {
        return payload.assetId?.let { safeAssetId ->
            DeepLink.AssetOptIn(safeAssetId)
        } ?: DeepLink.UndefinedDeepLink(payload.rawDeepLinkUri)
    }
}
