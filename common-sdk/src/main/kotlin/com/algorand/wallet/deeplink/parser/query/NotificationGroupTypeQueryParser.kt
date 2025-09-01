/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.deeplink.parser.query

import com.algorand.wallet.deeplink.model.NotificationGroupType
import com.algorand.wallet.deeplink.model.PeraUri

internal class NotificationGroupTypeQueryParser : DeepLinkQueryParser<NotificationGroupType?> {

    override fun parseQuery(peraUri: PeraUri): NotificationGroupType? {
        return when {
            peraUri.rawUri.contains(NOTIFICATION_ACTION_ASSET_TRANSACTIONS) -> NotificationGroupType.TRANSACTIONS
            peraUri.rawUri.contains(NOTIFICATION_ACTION_ASSET_OPTIN) -> NotificationGroupType.OPT_IN
            peraUri.rawUri.contains(NOTIFICATION_ACTION_ASSET_INBOX) -> NotificationGroupType.ASSET_INBOX
            else -> null
        }
    }
    
    private companion object {
        const val NOTIFICATION_ACTION_ASSET_TRANSACTIONS = "asset/transactions"
        const val NOTIFICATION_ACTION_ASSET_OPTIN = "asset/opt-in"
        const val NOTIFICATION_ACTION_ASSET_INBOX = "asset-inbox"
    }
}
