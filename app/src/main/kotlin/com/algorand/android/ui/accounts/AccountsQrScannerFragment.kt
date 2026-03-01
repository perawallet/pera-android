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

package com.algorand.android.ui.accounts

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.algorand.android.R
import com.algorand.android.models.AssetAction
import com.algorand.android.models.AssetTransaction
import com.algorand.android.modules.qrscanning.BaseQrScannerFragment
import com.algorand.android.modules.webimport.common.data.model.WebImportQrCode
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.wallet.deeplink.model.DeepLink
import com.algorand.wallet.deeplink.model.NotificationGroupType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountsQrScannerFragment : BaseQrScannerFragment(R.id.accountsQrScannerFragment) {

    private val accountsQrScannerViewModel: AccountsQrScannerViewModel by viewModels()

    override val shouldShowWcSessionsButton: Boolean
        get() = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
    }

    override fun initObservers() {
        super.initObservers()
        viewLifecycleOwner.collectLatestOnLifecycle(
            accountsQrScannerViewModel.isQrCodeInProgressFlow,
            ::onQrCodeProgressChanged
        )
    }

    override fun onAssetTransferWithNotOptInDeepLink(assetId: Long): Boolean {
        return true.also {
            mainActivity?.navToAddAssetTryLaterBottomSheet(AssetAction(assetId))
        }
    }

    override fun onAssetTransferDeepLink(assetTransaction: AssetTransaction): Boolean {
        return true.also {
            mainActivity?.navToSendAlgoNavigation(assetTransaction)
        }
    }

    override fun onAddContactDeepLink(address: String, label: String?): Boolean {
        return true.also {
            mainActivity?.navToContactAdditionNavigation(address, label)
        }
    }

    override fun onAccountAddressDeeplink(address: String, label: String?): Boolean {
        return true.also {
            mainActivity?.navToAccountsAddressScanActionBottomSheet(address, label)
        }
    }

    override fun onDiscoverBrowserDeepLink(webUrl: String): Boolean {
        return true.also {
            mainActivity?.navToDiscoverUrlViewerNavigation(webUrl)
        }
    }

    override fun onRecoverAccountDeepLink(mnemonic: String): Boolean {
        return true.also {
            mainActivity?.handleRecoverAccountDeeplink(mnemonic)
        }
    }

    override fun onWebImportQrCodeDeepLink(webImportQrCode: WebImportQrCode): Boolean {
        return true.also {
            mainActivity?.navToWebImportNavigation(webImportQrCode)
        }
    }

    override fun onAssetOptInDeepLink(assetAction: AssetAction): Boolean {
        return true.also {
            mainActivity?.handleOptInDeeplink(assetAction)
        }
    }

    override fun onWalletConnectConnectionDeeplink(wcUrl: String): Boolean {
        handleWalletConnectUrl(wcUrl)
        return true.also { navBack() }
    }

    override fun onKeyRegDeeplink(deepLink: DeepLink.KeyReg): Boolean {
        return true.also {
            mainActivity?.handleKeyRegDeepLink(deepLink)
        }
    }

    override fun onDiscoverDeepLink(path: String): Boolean {
        return true.also {
            mainActivity?.navToDiscoverWithPath(path)
        }
    }

    override fun onCardsDeepLink(path: String?): Boolean {
        return true.also {
            mainActivity?.navToCardsFragment(path)
        }
    }

    override fun onStakingDeepLink(path: String?): Boolean {
        return true.also {
            mainActivity?.navToStakingFragment(path)
        }
    }

    override fun onAccountDetailDeepLink(address: String): Boolean {
        return true.also {
            mainActivity?.handleAccountDetailDeeplink(address)
        }
    }

    override fun onAssetInboxDeepLink(accountAddress: String): Boolean {
        return true.also {
            mainActivity?.handleAssetInboxDeepLink(accountAddress)
        }
    }

    override fun onAddWatchAccountDeepLink(address: String, label: String?): Boolean {
        return true.also {
            mainActivity?.navToRegisterWatchAccountNavigation(address)
        }
    }

    override fun onAddressActionsDeepLink(address: String, label: String?): Boolean {
        return true.also {
            mainActivity?.navToAccountsAddressScanActionBottomSheet(address, label)
        }
    }

    override fun onAssetDetailDeepLink(address: String, assetId: Long): Boolean {
        return true.also {
            mainActivity?.handleAssetDetailDeeplink(address, assetId)
        }
    }

    override fun onBuyDeepLink(address: String, path: String?): Boolean {
        return true.also {
            mainActivity?.navToOnramp(address, path)
        }
    }

    override fun onSellDeepLink(address: String): Boolean {
        return true.also {
            mainActivity?.navToBidaliNavigation(address)
        }
    }

    override fun onInternalBrowserDeepLink(url: String): Boolean {
        return true.also {
            mainActivity?.navToInternalBrowser(url)
        }
    }

    override fun onSwapDeepLink(address: String, assetInId: Long?, assetOutId: Long?): Boolean {
        return true.also {
            mainActivity?.navToSwapNavigation(
                address = address,
                assetInId = assetInId,
                assetOutId = assetOutId
            )
        }
    }

    override fun onNotificationDeepLink(
        accountAddress: String,
        assetId: Long,
        notificationGroupType: NotificationGroupType,
        transactionId: String?,
    ): Boolean {
        return true.also {
            mainActivity?.handleNotificationDeepLink(accountAddress, assetId, notificationGroupType, transactionId)
        }
    }

    override fun onFidoDeepLink(uri: String): Boolean {
        return true.also {
            navBack()
            mainActivity?.launchIntentWithUri(uri)
        }
    }
}
