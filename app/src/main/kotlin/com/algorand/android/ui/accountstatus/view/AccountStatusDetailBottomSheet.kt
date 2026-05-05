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

package com.algorand.android.ui.accountstatus.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
import com.algorand.android.R
import com.algorand.android.core.BaseBottomSheet
import com.algorand.android.ui.accountstatus.viewmodel.AccountStatusDetailViewModel
import com.algorand.android.ui.accountstatus.viewmodel.AccountStatusDetailViewModel.ViewEvent.HideFetchingRekeyedAccountsDialog
import com.algorand.android.ui.accountstatus.viewmodel.AccountStatusDetailViewModel.ViewEvent.NavToNoRekeyedAccounts
import com.algorand.android.ui.accountstatus.viewmodel.AccountStatusDetailViewModel.ViewEvent.NavToRecoverRegisteredAccounts
import com.algorand.android.ui.accountstatus.viewmodel.AccountStatusDetailViewModel.ViewEvent.NavToRekeyedAccountSelection
import com.algorand.android.ui.accountstatus.viewmodel.AccountStatusDetailViewModel.ViewEvent.ShowFetchingRekeyedAccountsDialog
import com.algorand.android.ui.accountstatus.viewmodel.AccountStatusDetailViewModel.ViewEvent.ShowGenericError
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.rekeyedaccounts.model.RekeyedAccountSelectionNavArg
import com.algorand.android.ui.rekeyedaccounts.view.FetchingRekeyedAccountsDialogDelegate
import com.algorand.android.utils.browser.openUrl
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountStatusDetailBottomSheet : BaseBottomSheet(R.layout.bottom_sheet_account_status_detail),
    AccountStatusBottomSheetScreenListener {

    private val viewModel by viewModels<AccountStatusDetailViewModel>()

    val args: AccountStatusDetailBottomSheetArgs by navArgs<AccountStatusDetailBottomSheetArgs>()

    private val fetchingRekeyedAccountsDialogDelegate by lazy {
        FetchingRekeyedAccountsDialogDelegate(viewModel::stopFetchingRekeyedAccounts)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PeraTheme {
                    AccountStatusBottomSheetScreen(this@AccountStatusDetailBottomSheet, viewModel)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        viewModel.initializeAccountStatus(args.accountAddress)
    }

    private val viewEventCollector: suspend (AccountStatusDetailViewModel.ViewEvent) -> Unit = { event ->
        when (event) {
            HideFetchingRekeyedAccountsDialog -> fetchingRekeyedAccountsDialogDelegate.dismiss()
            ShowFetchingRekeyedAccountsDialog -> fetchingRekeyedAccountsDialogDelegate.show(requireContext())
            is NavToRecoverRegisteredAccounts -> {
                nav(
                    AccountStatusDetailBottomSheetDirections
                        .actionAccountStatusDetailBottomSheetToRecoverRegisteredAccountsNavigation(
                            event.encryptedEntropyBase64
                        )
                )
            }

            NavToNoRekeyedAccounts -> navToNoRekeyedAccounts()
            is NavToRekeyedAccountSelection -> navToRekeyedAccountSelection(event)
            ShowGenericError -> showGlobalError(getString(R.string.asset_info_load_failed))
        }
    }

    private fun initObservers() {
        collectLatestOnLifecycle(viewModel.viewEvent, viewEventCollector, Lifecycle.State.CREATED)
    }

    private fun navToNoRekeyedAccounts() {
        nav(
            AccountStatusDetailBottomSheetDirections.actionAccountStatusDetailBottomSheetToNoRekeyedAccountsNavigation()
        )
    }

    private fun navToRekeyedAccountSelection(viewEvent: NavToRekeyedAccountSelection) {
        val navArg = with(viewEvent) {
            RekeyedAccountSelectionNavArg(authAddress, authDrawable, rekeyedAddresses)
        }
        nav(
            AccountStatusDetailBottomSheetDirections
                .actionAccountStatusDetailBottomSheetToRescanRekeyedAccountSelectionNavigation(navArg)
        )
    }

    override fun onCopyAddress(address: String) {
        onAccountAddressCopied(address)
    }

    override fun onUndoRekeyClick() {
        nav(
            AccountStatusDetailBottomSheetDirections
                .actionAccountStatusDetailBottomSheetToRekeyUndoNavigation(args.accountAddress)
        )
    }

    override fun onRekeyToLedgerClick() {
        nav(
            AccountStatusDetailBottomSheetDirections
                .actionAccountStatusDetailBottomSheetToRekeyLedgerNavigation(args.accountAddress)
        )
    }

    override fun onRekeyToStandardClick() {
        nav(
            AccountStatusDetailBottomSheetDirections
                .actionAccountStatusDetailBottomSheetToRekeyToStandardAccountNavigation(args.accountAddress)
        )
    }

    override fun onRekeyToJointAccountClick() {
        nav(
            AccountStatusDetailBottomSheetDirections
                .actionAccountStatusDetailBottomSheetToRekeyToJointAccountNavigation(args.accountAddress)
        )
    }

    override fun onRescanRekeyedAddressesClick() {
        viewModel.scanRekeyedAccounts(args.accountAddress)
    }

    override fun onScanRegisteredAddressesClick() {
        viewModel.navigateToRecoverRegisteredAccounts(args.accountAddress)
    }

    override fun onLearnMoreClick(url: String) {
        context?.openUrl(url)
    }
}
