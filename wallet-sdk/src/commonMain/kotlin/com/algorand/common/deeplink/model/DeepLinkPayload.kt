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

package com.algorand.common.deeplink.model

internal data class DeepLinkPayload(
    val accountAddress: String? = null,
    val walletConnectUrl: String? = null,
    val assetId: Long? = null,
    val amount: String? = null,
    val note: String? = null,
    val xnote: String? = null,
    val mnemonic: String? = null,
    val label: String? = null,
    val transactionStatus: String? = null,
    val transactionId: String? = null,
    val url: String? = null,
    val webImportQrCode: WebImportQrCode? = null,
    val notificationGroupType: NotificationGroupType? = null,
    val fee: String? = null,
    val votekey: String? = null,
    val selkey: String? = null,
    val sprfkey: String? = null,
    val votefst: String? = null,
    val votelst: String? = null,
    val votekd: String? = null,
    val type: String? = null,
    val rawDeepLinkUri: String,
)

enum class NotificationGroupType {
    TRANSACTIONS,
    OPT_IN,
    ASSET_INBOX;

    companion object {
        val DEFAULT = TRANSACTIONS
    }
}

internal data class WebImportQrCode(
    val backupId: String,
    val encryptionKey: String,
)
