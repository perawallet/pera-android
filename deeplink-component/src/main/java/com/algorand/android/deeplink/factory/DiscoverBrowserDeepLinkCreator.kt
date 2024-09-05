package com.algorand.android.deeplink.factory

import com.algorand.android.deeplink.model.BaseDeepLink
import com.algorand.android.deeplink.model.RawDeepLink
import javax.inject.Inject

internal class DiscoverBrowserDeepLinkCreator @Inject constructor() : DeepLinkCreator {

    override suspend fun createDeepLink(rawDeeplink: RawDeepLink): BaseDeepLink {
        return BaseDeepLink.DiscoverBrowserDeepLink(
            webUrl = rawDeeplink.url.orEmpty()
        )
    }

    override fun doesDeeplinkMeetTheRequirements(rawDeepLink: RawDeepLink): Boolean {
        return with(rawDeepLink) {
            url != null &&
                accountAddress == null &&
                assetId == null &&
                amount == null &&
                walletConnectUrl == null &&
                note == null &&
                xnote == null &&
                label == null &&
                webImportQrCode == null &&
                notificationGroupType == null &&
                mnemonic == null
        }
    }
}
