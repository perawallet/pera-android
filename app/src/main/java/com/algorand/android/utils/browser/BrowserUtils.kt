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

@file:SuppressWarnings("TooManyFunctions")

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

package com.algorand.android.utils.browser

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.algorand.android.module.node.domain.NodeConstants.MAINNET_NETWORK_SLUG
import com.algorand.android.utils.extensions.appendAt
import com.algorand.android.utils.recordException

private const val PRIVACY_POLICY_URL = "https://perawallet.app/privacy-policy/"
private const val TERMS_AND_SERVICES_URL = "https://perawallet.app/terms-and-services/"
private const val PERA_EXPLORER_URL = "explorer.perawallet.app"
private const val MARKET_PAGE_URL = "https://play.google.com/store/apps/details?id=com.algorand.android"
private const val SUPPORT_CENTER_URL = "https://perawallet.app/support/"
private const val TRANSACTION_INFO_URL = "https://perawallet.app/support/transactions/"
private const val RECOVERY_PASSPHRASE_SUPPORT_URL = "https://perawallet.app/support/passphrase/"
private const val WATCH_ACCOUNT_SUPPORT_URL = "https://perawallet.app/support/watch-accounts/"
private const val WC_ADVANCED_PERMISSIONS_URL = "https://perawallet.app/support/walletconnect-advanced-options"
const val RECOVER_INFO_URL = "https://perawallet.app/support/recover-account/"
const val LEDGER_HELP_WEB_URL = "https://perawallet.app/support/ledger/"
private const val PERA_INTRODUCTION_URL = "https://perawallet.app/blog/launch-announcement/"
const val PERA_SUPPORT_URL = "https://perawallet.app/support/"
private const val DISPENSER_URL = "https://dispenser.testnet.aws.algodev.network/"
const val ASA_VERIFICATION_URL = "https://explorer.perawallet.app/asa-verification/"
const val BASE_TWITTER_URL = "https://twitter.com/"
private const val VESTIGE_TERMS_OF_SERVICE_URL = "https://about.vestige.fi/disclaimer/terms-of-service"
const val BLANK_URL = "about:blank"
private const val TINYMAN_FAQ_PRICE_IMPACT_URL = "https://docs.tinyman.org/faq#what-is-a-price-impact"
const val ASB_SUPPORT_URL = "https://perawallet.app/support/asb"

// TODO: update here whenever page is ready
const val LEDGER_SUPPORT_URL = "https://perawallet.app/support/rekey/"

const val HTTPS_PROTOCOL = "https://"
const val HTTP_PROTOCOL = "http://"

fun Context.openPeraIntroductionBlog() {
    openUrl(PERA_INTRODUCTION_URL)
}

@SuppressWarnings("MaxLineLength")
const val LEDGER_BLUETOOTH_SUPPORT_URL =
    "https://support.ledger.com/hc/en-us/articles/360025864773-Fix-Bluetooth-pairing-issues?support=true)"

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
            .launchUrl(this, Uri.parse(url))
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

// TODO: 4.03.2022 The site is not supporting test net yet, so it's not tested on MainNet
fun Context.showAssetOnNftExplorer(assetId: Long, networkSlug: String?) {
    val subDomain = createSubDomainWithNetworkSlug(networkSlug)
    openUrl("https://www.nftexplorer.app/asset/$assetId")
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
            Intent(ACTION_VIEW, Uri.parse(url))
        )
    } catch (activityNotFoundException: ActivityNotFoundException) {
        recordException(activityNotFoundException)
    }
}

fun Context.openApplicationPageOnStore() {
    try {
        startActivity(
            Intent(ACTION_VIEW, Uri.parse(MARKET_PAGE_URL))
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

fun Context.openPeraSupportUrl() {
    openUrl(PERA_SUPPORT_URL)
}

fun Context.openWalletConnectAdvancedPermissionsUrl() {
    openUrl(WC_ADVANCED_PERMISSIONS_URL)
}

fun Context.openDispenserUrl(accountAddress: String?) {
    val accountParameter = accountAddress?.let { "?account=$it" }.orEmpty()
    openUrl("$DISPENSER_URL$accountParameter")
}

fun Context.openASAVerificationUrl() {
    openUrl(ASA_VERIFICATION_URL)
}

fun Context.openVestigeTermsOfServiceUrl() {
    openUrl(VESTIGE_TERMS_OF_SERVICE_URL)
}

fun Context.openGroupTransactionInPeraExplorer(groupId: String?, networkSlug: String?) {
    val subDomain = createSubDomainWithNetworkSlug(networkSlug)
    openUrl("https://$subDomain$PERA_EXPLORER_URL/tx-group/$groupId/")
}

fun Context.openTinymanFaqPriceImpactUrl() {
    openUrl(TINYMAN_FAQ_PRICE_IMPACT_URL)
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
