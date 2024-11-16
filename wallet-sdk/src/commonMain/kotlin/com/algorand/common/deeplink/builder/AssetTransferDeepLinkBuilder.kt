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

import com.algorand.common.asset.AssetConstants.ALGO_ID
import com.algorand.common.deeplink.model.DeepLink
import com.algorand.common.deeplink.model.DeepLinkPayload

internal class AssetTransferDeepLinkBuilder : DeepLinkBuilder {

    override fun doesDeeplinkMeetTheRequirements(payload: DeepLinkPayload): Boolean {
        return with(payload) {
            val hasValidAssetQueries = amount != null || assetId != null
            val hasValidTransferQueries = accountAddress != null &&
                walletConnectUrl == null &&
                webImportQrCode == null &&
                notificationGroupType == null

            hasValidAssetQueries && hasValidTransferQueries
        }
    }

    override fun createDeepLink(payload: DeepLinkPayload): DeepLink {
        return DeepLink.AssetTransfer(
            receiverAccountAddress = payload.accountAddress.orEmpty(),
            amount = payload.amount ?: "0",
            note = payload.note,
            xnote = payload.xnote,
            assetId = payload.assetId ?: ALGO_ID,
            label = payload.label
        )
    }
}
