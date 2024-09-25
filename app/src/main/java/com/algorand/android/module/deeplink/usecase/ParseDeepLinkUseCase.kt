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

package com.algorand.android.module.deeplink.usecase

import android.net.Uri
import com.algorand.android.module.deeplink.mapper.WebImportQrCodeMapper
import com.algorand.android.module.deeplink.model.NotificationGroupType
import com.algorand.android.module.deeplink.model.RawDeepLink
import com.algorand.android.module.deeplink.model.RawMnemonicPayload
import com.algorand.android.module.deeplink.model.WebImportQrCode
import com.algorand.android.module.deeplink.model.WebQrCode
import com.algorand.android.foundation.common.decodeBase64ToString
import com.algorand.android.foundation.json.JsonSerializer
import com.algorand.android.transaction.domain.usecase.IsValidAlgorandAddress
import java.math.BigInteger
import javax.inject.Inject

internal class ParseDeepLinkUseCase @Inject constructor(
    private val jsonSerializer: JsonSerializer,
    private val isValidAlgorandAddress: IsValidAlgorandAddress,
    private val webImportQrCodeMapper: WebImportQrCodeMapper
) : ParseDeepLink {

    override fun invoke(deepLink: String): com.algorand.android.module.deeplink.model.RawDeepLink {
        val parsedUri = Uri.parse(deepLink)
        return com.algorand.android.module.deeplink.model.RawDeepLink(
            rawUrl = deepLink,
            accountAddress = getAccountAddress(parsedUri),
            walletConnectUrl = getWalletConnectUrl(parsedUri),
            assetId = getAssetId(parsedUri),
            amount = getAmount(parsedUri),
            note = getNote(parsedUri),
            xnote = getXnote(parsedUri),
            mnemonic = getMnemonic(parsedUri),
            label = getLabel(parsedUri),
            transactionId = getTransactionId(parsedUri),
            transactionStatus = getTransactionStatus(parsedUri),
            webImportQrCode = getWebImportData(parsedUri),
            notificationGroupType = getNotificationGroupType(parsedUri),
            url = getUrl(parsedUri)
        )
    }

    private fun getXnote(parsedUri: Uri): String? {
        return parseQueryIfExist(XNOTE_QUERY_KEY, parsedUri)
    }

    private fun getNote(parsedUri: Uri): String? {
        return parseQueryIfExist(NOTE_QUERY_KEY, parsedUri)
    }

    private fun getAmount(parsedUri: Uri): BigInteger? {
        val amountAsString = parseQueryIfExist(AMOUNT_QUERY_KEY, parsedUri)
        return amountAsString?.toBigIntegerOrNull()
    }

    private fun getAssetId(parsedUri: Uri): Long? {
        val assetIdAsString = parseQueryIfExist(ASSET_ID_QUERY_KEY, parsedUri)
        return assetIdAsString?.toLongOrNull()
    }

    private fun getLabel(parsedUri: Uri): String? {
        return parseQueryIfExist(LABEL_QUERY_KEY, parsedUri)
    }

    private fun getTransactionId(parsedUri: Uri): String? {
        return parseQueryIfExist(TRANSACTION_ID_KEY, parsedUri)
    }

    private fun getTransactionStatus(parsedUri: Uri): String? {
        return parseQueryIfExist(TRANSACTION_STATUS_KEY, parsedUri)
    }

    private fun getUrl(parsedUri: Uri): String? {
        return parseQueryIfExist(URL_QUERY_KEY, parsedUri)?.decodeBase64ToString()
    }

    private fun getAccountAddress(uri: Uri): String? {
        return with(uri) {
            if (isApplink(this)) {
                path?.split(PATH_SEPARATOR)?.firstOrNull { isValidAlgorandAddress(it) }
            } else {
                parseQueryIfExist(ACCOUNT_ID_QUERY_KEY, this) ?: authority
            }.takeIf { isValidAlgorandAddress(it.orEmpty()) } ?: uri.toString().takeIf { isValidAlgorandAddress(it) }
        }
    }

    private fun getWalletConnectUrl(uri: Uri): String? {
        return with(uri) {
            val parsedUrl = if (isApplink(this)) {
                val walletConnectUrl = toString().split(PERAWALLET_WC_AUTH_KEY).lastOrNull()
                walletConnectUrl?.removePrefix(PATH_SEPARATOR)
            } else {
                if (authority.isNullOrBlank()) {
                    uri.toString()
                } else {
                    removeAuthSeparator(schemeSpecificPart)
                }
            }
            parsedUrl.takeIf { it?.startsWith(WALLET_CONNECT_AUTH_KEY) == true }
        }
    }

    private fun getMnemonic(uri: Uri): String? {
        return try {
            jsonSerializer.fromJson(uri.toString(), RawMnemonicPayload::class.java)?.mnemonic
        } catch (exception: Exception) {
            null
        }
    }

    private fun getWebImportData(uri: Uri): WebImportQrCode? {
        return try {
            jsonSerializer.fromJson<WebQrCode>(uri.toString(), WebQrCode::class.java)?.let { qrCode ->
                webImportQrCodeMapper(qrCode)
            }
        } catch (exception: Exception) {
            null
        }
    }

    private fun getNotificationGroupType(uri: Uri): NotificationGroupType? {
        return with(uri) {
            when (authority + path) {
                NOTIFICATION_ACTION_ASSET_TRANSACTIONS -> NotificationGroupType.TRANSACTIONS
                NOTIFICATION_ACTION_ASSET_OPTIN -> NotificationGroupType.OPT_IN
                else -> null
            }
        }
    }

    private fun parseQueryIfExist(queryKey: String, uri: Uri): String? {
        if (!uri.isHierarchical) return null
        val hasQueryKey = uri.queryParameterNames.contains(queryKey)
        return if (hasQueryKey) uri.getQueryParameter(queryKey) else null
    }

    private fun isApplink(uri: Uri): Boolean {
        return removeAuthSeparator(uri.schemeSpecificPart).startsWith(PERAWALLET_APPLINK_AUTH_KEY)
    }

    private fun removeAuthSeparator(uriString: String): String {
        return uriString.removePrefix(AUTH_SEPARATOR)
    }

    companion object {
        private const val PERAWALLET_APPLINK_AUTH_KEY = "perawallet.app"

        private const val PERAWALLET_WC_AUTH_KEY = "perawallet-wc"
        private const val WALLET_CONNECT_AUTH_KEY = "wc"

        private const val AMOUNT_QUERY_KEY = "amount"
        private const val ASSET_ID_QUERY_KEY = "asset"
        private const val ACCOUNT_ID_QUERY_KEY = "account"
        private const val NOTE_QUERY_KEY = "note"
        private const val XNOTE_QUERY_KEY = "xnote"
        private const val LABEL_QUERY_KEY = "label"
        private const val TRANSACTION_ID_KEY = "transactionId"
        private const val TRANSACTION_STATUS_KEY = "transactionStatus"
        private const val NOTIFICATION_ACTION_ASSET_TRANSACTIONS = "asset/transactions"
        private const val NOTIFICATION_ACTION_ASSET_OPTIN = "asset/opt-in"
        private const val URL_QUERY_KEY = "url"

        private const val AUTH_SEPARATOR = "//"
        private const val PATH_SEPARATOR = "/"
    }
}
