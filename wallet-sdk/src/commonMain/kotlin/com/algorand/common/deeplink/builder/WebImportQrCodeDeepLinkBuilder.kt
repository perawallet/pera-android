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

internal class WebImportQrCodeDeepLinkBuilder : DeepLinkBuilder {

    override fun doesDeeplinkMeetTheRequirements(payload: DeepLinkPayload): Boolean {
        return with(payload) {
            webImportQrCode != null &&
                accountAddress == null &&
                assetId == null &&
                amount == null &&
                walletConnectUrl == null &&
                url == null &&
                note == null &&
                xnote == null &&
                label == null &&
                notificationGroupType == null &&
                mnemonic == null
        }
    }

    override fun createDeepLink(payload: DeepLinkPayload): DeepLink {
        return DeepLink.WebImportQrCode(
            backupId = payload.webImportQrCode?.backupId.orEmpty(),
            encryptionKey = payload.webImportQrCode?.encryptionKey.orEmpty()
        )
    }
}
