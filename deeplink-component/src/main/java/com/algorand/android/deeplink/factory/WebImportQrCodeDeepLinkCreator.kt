/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.deeplink.factory

import com.algorand.android.deeplink.model.BaseDeepLink
import com.algorand.android.deeplink.model.RawDeepLink
import com.algorand.android.deeplink.model.WebImportQrCode
import javax.inject.Inject

internal class WebImportQrCodeDeepLinkCreator @Inject constructor() : DeepLinkCreator {

    override suspend fun createDeepLink(rawDeeplink: RawDeepLink): BaseDeepLink {
        return BaseDeepLink.WebImportQrCodeDeepLink(rawDeeplink.webImportQrCode ?: WebImportQrCode("", ""))
    }

    override fun doesDeeplinkMeetTheRequirements(rawDeepLink: RawDeepLink): Boolean {
        return with(rawDeepLink) {
            webImportQrCode != null &&
                accountAddress == null &&
                assetId == null &&
                amount == null &&
                walletConnectUrl == null &&
                note == null &&
                xnote == null &&
                label == null &&
                notificationGroupType == null &&
                mnemonic == null
        }
    }
}
