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
import com.algorand.android.HomeNavigationDirections
import com.algorand.android.R
import com.algorand.android.models.AssetAction
import com.algorand.android.models.AssetTransaction
import com.algorand.android.modules.qrscanning.BaseQrScannerFragment
import com.algorand.android.modules.tracking.core.PeraEvent
import com.algorand.android.modules.webimport.common.data.model.WebImportQrCode
import com.algorand.android.ui.accounts.AccountsQrScannerFragmentDirections.Companion.actionAccountsQrScannerFragmentToRecoverWithPassphraseNavigation
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountsQrScannerFragment : BaseQrScannerFragment(R.id.accountsQrScannerFragment) {

    private val viewEventCollector: suspend (AccountsQrScannerViewModel.ViewEvent) -> Unit = { event ->
        when (event) {
            is AccountsQrScannerViewModel.ViewEvent.NavToRecoverWithPassphraseNavigation ->
                navToRecoverWithPassphraseNavigation(event.mnemonic)

            is AccountsQrScannerViewModel.ViewEvent.ShowMaxAccountLimitExceededError ->
                showMaxAccountLimitExceededError()
        }
    }

    private val accountsQrScannerViewModel: AccountsQrScannerViewModel by viewModels()

    override val shouldShowWcSessionsButton: Boolean
        get() = true

    @SuppressWarnings("MaxLineLength")
    override fun onAssetTransferWithNotOptInDeepLink(assetId: Long): Boolean {
        val assetAction = AssetAction(assetId)
        return true.also {
            nav(
                AccountsQrScannerFragmentDirections.actionAccountsQrScannerFragmentToUnsupportedAddAssetTryLaterBottomSheet(
                    assetAction
                )
            )
        }
    }

    @SuppressWarnings("MaxLineLength")
    override fun onAssetTransferDeepLink(assetTransaction: AssetTransaction): Boolean {
        return true.also {
            nav(AccountsQrScannerFragmentDirections.actionAccountsQrScannerFragmentToSendAlgoNavigation(assetTransaction))
        }
    }

    @SuppressWarnings("MaxLineLength")
    override fun onAccountAddressDeeplink(accountAddress: String, label: String?): Boolean {
        return true.also {
            nav(
                AccountsQrScannerFragmentDirections.actionAccountsQrScannerFragmentToAccountsAddressScanActionBottomSheet(
                    accountAddress,
                    label
                )
            )
        }
    }

    override fun onDiscoverBrowserDeepLink(webUrl: String): Boolean {
        return true.also {
            nav(AccountsQrScannerFragmentDirections.actionAccountsQrScannerFragmentDiscoverUrlViewerNavigation(webUrl))
        }
    }

    override fun onImportAccountDeepLink(mnemonic: String): Boolean {
        return true.also {
            accountsQrScannerViewModel.onImportAccountDeepLink(mnemonic)
        }
    }

    override fun onWebImportQrCodeDeepLink(webImportQrCode: WebImportQrCode): Boolean {
        return true.also {
            nav(
                HomeNavigationDirections.actionGlobalWebImportNavigation(
                    webImportQrCode = webImportQrCode
                )
            )
        }
    }

    override fun onAssetOptInDeepLink(assetAction: AssetAction): Boolean {
        return true.also {
            nav(
                AccountsQrScannerFragmentDirections.actionAccountsQrScannerFragmentToAddAssetAccountSelectionFragment(
                    assetId = assetAction.assetId
                )
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
    }

    override fun onWalletConnectConnectionDeeplink(wcUrl: String): Boolean {
        accountsQrScannerViewModel.logEvent(PeraEvent.HOME_SCREEN_QR_SCAN)
        handleWalletConnectUrl(wcUrl)
        return true.also { navBack() }
    }

    override fun initObservers() {
        super.initObservers()
        viewLifecycleOwner.collectLatestOnLifecycle(
            accountsQrScannerViewModel.viewEvent,
            viewEventCollector
        )
    }

    private fun navToRecoverWithPassphraseNavigation(mnemonic: String) {
        nav(actionAccountsQrScannerFragmentToRecoverWithPassphraseNavigation(mnemonic))
    }
}
