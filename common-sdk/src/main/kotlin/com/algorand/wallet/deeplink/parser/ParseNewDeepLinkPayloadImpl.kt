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

package com.algorand.wallet.deeplink.parser

import com.algorand.wallet.deeplink.model.DeepLinkPayload

internal class ParseNewDeepLinkPayloadImpl(
    private val peraNewUriParser: PeraNewUriParser
) : ParseNewDeepLinkPayload {

    override fun invoke(url: String): DeepLinkPayload {
        val peraUri = peraNewUriParser.parseUri(url)
        return DeepLinkPayload(
            accountAddress = peraUri.getQueryParam(ADDRESS_QUERY_KEY),
            receiverAddress = peraUri.getQueryParam(RECEIVER_ADDRESS_QUERY_KEY),
            walletConnectUrl = peraUri.getQueryParam(WALLET_CONNECT_URL_QUERY_KEY),
            assetId = peraUri.getQueryParam(ASSET_ID_QUERY_KEY)?.toLongOrNull(),
            assetInId = peraUri.getQueryParam(ASSET_IN_ID_QUERY_KEY)?.toLongOrNull(),
            assetOutId = peraUri.getQueryParam(ASSET_OUT_ID_QUERY_KEY)?.toLongOrNull(),
            amount = peraUri.getQueryParam(AMOUNT_QUERY_KEY),
            note = peraUri.getQueryParam(NOTE_QUERY_KEY),
            xnote = peraUri.getQueryParam(XNOTE_QUERY_KEY),
            label = peraUri.getQueryParam(LABEL_QUERY_KEY),
            transactionId = peraUri.getQueryParam(TRANSACTION_ID_KEY),
            transactionStatus = peraUri.getQueryParam(TRANSACTION_STATUS_KEY),
            mnemonic = peraUri.getQueryParam(MNEMONIC_QUERY_KEY),
            url = peraUri.getQueryParam(URL_QUERY_KEY),
            encryptionKey = peraUri.getQueryParam(ENCRYPTION_KEY_QUERY_KEY),
            backupId = peraUri.getQueryParam(BACKUP_ID_QUERY_KEY),
            action = peraUri.getQueryParam(ACTION_QUERY_KEY),
            fee = peraUri.getQueryParam(FEE_QUERY_KEY),
            votekey = peraUri.getQueryParam(VOTEKEY_QUERY_KEY),
            selkey = peraUri.getQueryParam(SELKEY_QUERY_KEY),
            sprfkey = peraUri.getQueryParam(SPRFKEY_QUERY_KEY),
            votefst = peraUri.getQueryParam(VOTEFST_QUERY_KEY),
            votelst = peraUri.getQueryParam(VOTELST_QUERY_KEY),
            votekd = peraUri.getQueryParam(VOTEKD_QUERY_KEY),
            type = peraUri.getQueryParam(TYPE_QUERY_KEY),
            path = peraUri.getQueryParam(PATH_KEY),
            host = peraUri.host,
            rawDeepLinkUri = url,
            lastPathSegment = peraUri.lastPathSegment
        )
    }

    private companion object {
        const val ADDRESS_QUERY_KEY = "address"
        const val MNEMONIC_QUERY_KEY = "mnemonic"
        const val BACKUP_ID_QUERY_KEY = "backupId"
        const val ENCRYPTION_KEY_QUERY_KEY = "encryptionKey"
        const val ACTION_QUERY_KEY = "action"
        const val RECEIVER_ADDRESS_QUERY_KEY = "receiverAddress"
        const val WALLET_CONNECT_URL_QUERY_KEY = "walletConnectUrl"
        const val ASSET_ID_QUERY_KEY = "assetId"
        const val ASSET_IN_ID_QUERY_KEY = "assetInId"
        const val ASSET_OUT_ID_QUERY_KEY = "assetOutId"
        const val URL_QUERY_KEY = "url"
        const val AMOUNT_QUERY_KEY = "amount"
        const val NOTE_QUERY_KEY = "note"
        const val XNOTE_QUERY_KEY = "xnote"
        const val LABEL_QUERY_KEY = "label"
        const val TRANSACTION_ID_KEY = "transactionId"
        const val TRANSACTION_STATUS_KEY = "transactionStatus"
        const val TYPE_QUERY_KEY = "type"
        const val SELKEY_QUERY_KEY = "selkey"
        const val SPRFKEY_QUERY_KEY = "sprfkey"
        const val VOTEFST_QUERY_KEY = "votefst"
        const val VOTELST_QUERY_KEY = "votelst"
        const val VOTEKD_QUERY_KEY = "votekd"
        const val VOTEKEY_QUERY_KEY = "votekey"
        const val FEE_QUERY_KEY = "fee"
        const val PATH_KEY = "path"
    }
}
