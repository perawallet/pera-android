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
import javax.inject.Inject

internal class AccountAddressDeepLinkCreator @Inject constructor() : DeepLinkCreator {

    override suspend fun createDeepLink(rawDeeplink: RawDeepLink): BaseDeepLink {
        return BaseDeepLink.AccountAddressDeepLink(
            accountAddress = rawDeeplink.accountAddress.orEmpty(),
            label = rawDeeplink.label
        )
    }

    override fun doesDeeplinkMeetTheRequirements(rawDeepLink: RawDeepLink): Boolean {
        return with(rawDeepLink) {
            accountAddress != null &&
                walletConnectUrl == null &&
                assetId == null &&
                amount == null &&
                note == null &&
                xnote == null &&
                webImportQrCode == null &&
                notificationGroupType == null
        }
    }
}
