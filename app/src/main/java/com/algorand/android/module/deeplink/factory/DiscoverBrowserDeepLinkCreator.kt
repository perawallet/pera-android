package com.algorand.android.module.deeplink.factory

import com.algorand.android.module.deeplink.model.BaseDeepLink
import com.algorand.android.module.deeplink.model.RawDeepLink
import javax.inject.Inject

internal class DiscoverBrowserDeepLinkCreator @Inject constructor() : DeepLinkCreator {

    override suspend fun createDeepLink(rawDeeplink: com.algorand.android.module.deeplink.model.RawDeepLink): com.algorand.android.module.deeplink.model.BaseDeepLink {
        return com.algorand.android.module.deeplink.model.BaseDeepLink.DiscoverBrowserDeepLink(
            webUrl = rawDeeplink.url.orEmpty()
        )
    }

    override fun doesDeeplinkMeetTheRequirements(rawDeepLink: com.algorand.android.module.deeplink.model.RawDeepLink): Boolean {
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
