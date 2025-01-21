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

package com.algorand.wallet.deeplink.builder

import com.algorand.wallet.deeplink.model.DeepLink
import com.algorand.wallet.deeplink.model.DeepLinkPayload
import com.algorand.wallet.deeplink.model.NotificationGroupType
import javax.inject.Inject

internal class AssetInboxDeepLinkBuilder @Inject constructor() : DeepLinkBuilder {

    override fun doesDeeplinkMeetTheRequirements(payload: DeepLinkPayload): Boolean {
        return with(payload) {
            accountAddress != null && notificationGroupType == NotificationGroupType.ASSET_INBOX
        }
    }

    override fun createDeepLink(payload: DeepLinkPayload): DeepLink {
        return DeepLink.AssetInbox(
            address = payload.accountAddress.orEmpty(),
            notificationGroupType = payload.notificationGroupType ?: NotificationGroupType.DEFAULT
        )
    }
}
