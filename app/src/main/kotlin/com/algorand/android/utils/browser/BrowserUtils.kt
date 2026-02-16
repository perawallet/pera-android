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

@file:SuppressWarnings("TooManyFunctions")

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

package com.algorand.android.utils.browser

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import com.algorand.android.utils.MAINNET_NETWORK_SLUG
import com.algorand.android.utils.extensions.appendAt
import com.algorand.android.utils.recordException

const val PRIVACY_POLICY_URL: String = "https://perawallet.app/privacy-policy/"
const val TERMS_AND_SERVICES_URL: String = "https://perawallet.app/terms-and-services/"
const val DISPENSER_URL: String = "https://lora.algokit.io/testnet/fund"
private const val PERA_EXPLORER_URL = "explorer.perawallet.app"
private const val MARKET_PAGE_URL = "https://play.google.com/store/apps/details?id=com.algorand.android"
private const val SUPPORT_CENTER_URL = "https://perawallet.app/support/"
private const val TRANSACTION_INFO_URL = "https://perawallet.app/support/transactions/"
private const val RECOVERY_PASSPHRASE_SUPPORT_URL = "https://perawallet.app/support/passphrase/"
private const val WATCH_ACCOUNT_SUPPORT_URL = "https://perawallet.app/support/watch-accounts/"
private const val TINYMAN_FAQ_PRICE_IMPACT_URL = "https://docs.tinyman.org/faq#what-is-a-price-impact"

const val RECOVER_ACCOUNT_SUPPORT_URL: String = "https://perawallet.app/support/recover-account/"
const val LEDGER_SUPPORT_URL: String = "https://perawallet.app/support/ledger/"
const val ASA_VERIFICATION_URL: String = "https://explorer.perawallet.app/asa-verification/"
const val BASE_TWITTER_URL: String = "https://twitter.com/"
const val BLANK_URL: String = "about:blank"
const val REKEY_SUPPORT_URL: String = "https://perawallet.app/support/rekey/"
const val ASSET_INBOX_SUPPORT_URL: String = "https://perawallet.app/support/asset-inbox/"
const val WATCH_SUPPORT_URL: String = "https://perawallet.app/support/watch-accounts/"
const val HD_ACCOUNT_SUPPORT_URL: String = "https://perawallet.app/support/hd-wallets/"
const val ALGO25_ACCOUNT_SUPPORT_URL: String = "https://perawallet.app/support/create-new-account/"
const val RECOVER_OR_IMPORT_ACCOUNT_SUPPORT_URL: String =
    "https://support.perawallet.app/en/article/recover-or-import-an-algorand-account-with-recovery-passphrase-11gdh1y/"
const val SWAP_INFO_SUPPORT_URL: String =
    "https://support.perawallet.app/en/article/pera-swap-swapping-with-pera-1ep84ky/"
const val JOINT_ACCOUNT_LEARN_MORE_URL: String =
    "https://support.perawallet.app/en/article/introduction-to-joint-accounts-1j0dt2g/?bust=1770846667322"

const val HTTPS_PROTOCOL: String = "https://"
const val HTTP_PROTOCOL: String = "http://"

fun Context.openTermsAndServicesUrl() {
    openUrl(TERMS_AND_SERVICES_URL)
}

fun Context.openPrivacyPolicyUrl() {
    openUrl(PRIVACY_POLICY_URL)
}

fun Context.openSupportCenterUrl() {
    openUrl(SUPPORT_CENTER_URL)
}

fun Context.openTransactionInfoUrl() {
    openUrl(TRANSACTION_INFO_URL)
}

fun Context.openUrl(url: String) {
    try {
        CustomTabsIntent.Builder()
            .build()
            .launchUrl(this, url.toUri())
    } catch (runtimeException: RuntimeException) {
        recordException(runtimeException)
    }
}

// TODO Refactor here
fun Context.openTransactionInPeraExplorer(transactionIdWithoutPrefix: String, networkSlug: String?) {
    val subDomain = createSubDomainWithNetworkSlug(networkSlug)
    openUrl("https://$subDomain$PERA_EXPLORER_URL/tx/$transactionIdWithoutPrefix/")
}

fun Context.openAssetInPeraExplorer(assetId: Long?, networkSlug: String?) {
    val subDomain = createSubDomainWithNetworkSlug(networkSlug)
    openUrl("https://$subDomain$PERA_EXPLORER_URL/asset/$assetId/")
}

fun Context.openAccountAddressInPeraExplorer(accountAddress: String, networkSlug: String?) {
    val subDomain = createSubDomainWithNetworkSlug(networkSlug)
    openUrl("https://$subDomain$PERA_EXPLORER_URL/address/$accountAddress/")
}

fun Context.openApplicationInPeraExplorer(applicationId: Long?, networkSlug: String?) {
    val subDomain = createSubDomainWithNetworkSlug(networkSlug)
    openUrl("https://$subDomain$PERA_EXPLORER_URL/application/$applicationId/")
}

fun Context.openAssetUrl(assetUrl: String?) {
    openUrl(assetUrl.orEmpty())
}

fun Context.openExternalBrowserApp(url: String) {
    try {
        startActivity(
            Intent(ACTION_VIEW, url.toUri())
        )
    } catch (activityNotFoundException: ActivityNotFoundException) {
        recordException(activityNotFoundException)
    }
}

fun Context.openApplicationPageOnStore() {
    try {
        startActivity(
            Intent(ACTION_VIEW, MARKET_PAGE_URL.toUri())
                .apply { setPackage("com.android.vending") }
        )
    } catch (activityNotFoundException: ActivityNotFoundException) {
        recordException(activityNotFoundException)
    }
}

fun Context.openRecoveryPassphraseSupportUrl() {
    openUrl(RECOVERY_PASSPHRASE_SUPPORT_URL)
}

fun Context.openWatchAccountSupportUrl() {
    openUrl(WATCH_ACCOUNT_SUPPORT_URL)
}

fun Context.openASAVerificationUrl() {
    openUrl(ASA_VERIFICATION_URL)
}

fun Context.openGroupTransactionInPeraExplorer(groupId: String?, networkSlug: String?) {
    val subDomain = createSubDomainWithNetworkSlug(networkSlug)
    openUrl("https://$subDomain$PERA_EXPLORER_URL/tx-group/$groupId/")
}

fun Context.openTinymanFaqPriceImpactUrl() {
    openUrl(TINYMAN_FAQ_PRICE_IMPACT_URL)
}

fun Context.openJointAccountLearnMoreUrl() {
    openUrl(JOINT_ACCOUNT_LEARN_MORE_URL)
}

fun getPeraExplorerUrl(transactionId: String, networkSlug: String?): String {
    val subDomain = createSubDomainWithNetworkSlug(networkSlug)
    return "$HTTPS_PROTOCOL$subDomain$PERA_EXPLORER_URL/tx/$transactionId/"
}

private fun createSubDomainWithNetworkSlug(networkSlug: String?): String {
    return if (networkSlug == MAINNET_NETWORK_SLUG) "" else "$networkSlug."
}

fun String?.removeProtocolIfNeed(): String? {
    return when {
        isNullOrBlank() -> this
        contains(HTTP_PROTOCOL) -> removePrefix(HTTP_PROTOCOL)
        contains(HTTPS_PROTOCOL) -> removePrefix(HTTPS_PROTOCOL)
        else -> this
    }
}

fun String?.addProtocolIfNeed(): String? {
    return when {
        isNullOrBlank() -> this
        !contains(HTTP_PROTOCOL) && !contains(HTTPS_PROTOCOL) -> appendAt(0, HTTPS_PROTOCOL)
        else -> this
    }
}
