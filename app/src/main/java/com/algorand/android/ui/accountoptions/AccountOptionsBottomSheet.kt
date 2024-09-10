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

package com.algorand.android.ui.accountoptions

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.algorand.android.R
import com.algorand.android.accountcore.ui.model.AccountDisplayName
import com.algorand.android.core.DaggerBaseBottomSheet
import com.algorand.android.databinding.BottomSheetAccountDetailAccountsOptionsBinding
import com.algorand.android.utils.Resource
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.extensions.collectOnLifecycle
import com.algorand.android.utils.extensions.show
import com.algorand.android.utils.setFragmentNavigationResult
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountOptionsBottomSheet : DaggerBaseBottomSheet(
    layoutResId = R.layout.bottom_sheet_account_detail_accounts_options,
    fullPageNeeded = false,
    firebaseEventScreenId = null
) {

    private val args by navArgs<AccountOptionsBottomSheetArgs>()

    private val binding by viewBinding(BottomSheetAccountDetailAccountsOptionsBinding::bind)

    private val accountOptionsViewModel: AccountOptionsViewModel by viewModels()

    private val accountOptionsPreviewCollector: suspend (AccountOptionsPreview?) -> Unit = { preview ->
        preview?.run {
            setupAuthAddressButton(isAuthAddressButtonVisible, authAddress)
            setupViewPassphraseButton(isPassphraseButtonVisible)
            setupCopyButton(accountAddress)
            setupShowQrButton(accountAddress)
            setupUndoRekeyOptionButton(isUndoRekeyButtonVisible, authAccountDisplayName)
            setupRekeyToOptions(canSignTransaction)
        }
    }

    private val publicKey: String
        get() = args.publicKey

    private val notificationObserverCollector: suspend (Resource<Unit>?) -> Unit = {
        it?.use(onLoadingFinished = ::navBack)
    }

    private val notificationFilterCheckCollector: suspend (Boolean?) -> Unit = { isMuted ->
        isMuted?.let { setupNotificationOptionButton(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRenameAccountButton()
        setupRemoveAccountButton()
        initObservers()
    }

    private fun initObservers() {
        viewLifecycleOwner.collectLatestOnLifecycle(
            accountOptionsViewModel.notificationFilterOperationFlow,
            notificationObserverCollector
        )
        viewLifecycleOwner.collectOnLifecycle(
            accountOptionsViewModel.notificationFilterCheckFlow,
            notificationFilterCheckCollector
        )
        viewLifecycleOwner.collectLatestOnLifecycle(
            accountOptionsViewModel.accountOptionsPreviewFlow,
            accountOptionsPreviewCollector
        )
    }

    private fun setupUndoRekeyOptionButton(isUndoRekeyButtonVisible: Boolean, authAccDisplayName: AccountDisplayName?) {
        binding.undoRekeyConstraintLayout.apply {
            setOnClickListener { navToUndoRekeyNavigation() }
            isVisible = isUndoRekeyButtonVisible
        }
        binding.rekeyedAccountTextView.text = context?.getString(
            R.string.rekeyed_to_account_name,
            authAccDisplayName?.primaryDisplayName.orEmpty()
        )
    }

    private fun setupRekeyToOptions(canSignTransaction: Boolean) {
        binding.rekeyToLedgerAccountButton.apply {
            isVisible = canSignTransaction
            setOnClickListener { navToRekeyToLedgerAccountFragment() }
        }
        binding.rekeyToStandardAccountButton.apply {
            isVisible = canSignTransaction
            setOnClickListener { navToRekeyToStandardAccountFragment() }
        }
        binding.rekeyDivider.isVisible = canSignTransaction
    }

    private fun setupNotificationOptionButton(isMuted: Boolean) {
        binding.notificationButton.apply {
            val textRes = if (isMuted) R.string.unmute_notifications else R.string.mute_notifications
            val iconRes = if (isMuted) R.drawable.ic_notification_unmute else R.drawable.ic_empty_notification
            setText(textRes)
            setIconResource(iconRes)
            setOnClickListener { accountOptionsViewModel.startFilterOperation(isMuted.not()) }
            show()
        }
    }

    private fun setupAuthAddressButton(authAddressButtonVisible: Boolean, authAddress: String?) {
        if (authAddressButtonVisible) {
            binding.authAddressButton.apply {
                show()
                setOnClickListener {
                    navToShowQrBottomSheet(
                        getString(R.string.auth_account_address),
                        authAddress.orEmpty()
                    )
                }
            }
        }
    }

    private fun setupRemoveAccountButton() {
        binding.disconnectAccountButton.apply {
            setOnClickListener { navToDisconnectAccountConfirmationBottomSheet() }
            show()
        }
    }

    private fun setupRenameAccountButton() {
        binding.renameAccountButton.apply {
            setOnClickListener { navToRenameAccountBottomSheet() }
            show()
        }
    }

    private fun navToRekeyToLedgerAccountFragment() {
        nav(AccountOptionsBottomSheetDirections.actionAccountOptionsBottomSheetToRekeyLedgerNavigation(publicKey))
    }

    private fun navToRekeyToStandardAccountFragment() {
        nav(
            AccountOptionsBottomSheetDirections.actionAccountOptionsBottomSheetToRekeyToStandardAccountNavigation(
                accountOptionsViewModel.getAccountAddress()
            )
        )
    }

    private fun navToDisconnectAccountConfirmationBottomSheet() {
        navBack()
        setFragmentNavigationResult(ACCOUNT_REMOVE_ACTION_KEY, true)
    }

    private fun navToRenameAccountBottomSheet() {
        nav(
            AccountOptionsBottomSheetDirections.actionAccountOptionsBottomSheetToRenameAccountNavigation(
                name = accountOptionsViewModel.getAccountName(),
                publicKey = accountOptionsViewModel.getAccountAddress()
            )
        )
    }

    private fun navToShowQrBottomSheet(title: String, publicKey: String) {
        nav(AccountOptionsBottomSheetDirections.actionAccountOptionsBottomSheetToShowQrNavigation(title, publicKey))
    }

    private fun onViewPassphraseClicked() {
        if (accountOptionsViewModel.isPinCodeEnabled()) {
            navToInAppPinNavigation()
        } else {
            navToViewPassphraseNavigation()
        }
    }

    private fun setupViewPassphraseButton(isPassphraseButtonVisible: Boolean) {
        if (isPassphraseButtonVisible) {
            binding.viewPassphraseButton.apply {
                setOnClickListener { onViewPassphraseClicked() }
                show()
            }
        }
    }

    private fun setupCopyButton(accountAddress: String) {
        with(binding) {
            copyAddressLayout.setOnClickListener {
                onAccountAddressCopied(accountAddress)
                navBack()
            }
            addressTextView.text = accountAddress
        }
    }

    private fun setupShowQrButton(accountAddress: String) {
        binding.showQrButton.setOnClickListener { navToShowQrBottomSheet(getString(R.string.qr_code), accountAddress) }
    }

    private fun navToUndoRekeyNavigation() {
        nav(AccountOptionsBottomSheetDirections.actionAccountOptionsBottomSheetToRekeyUndoNavigation(publicKey))
    }

    private fun navToInAppPinNavigation() {
        nav(
            AccountOptionsBottomSheetDirections
                .actionAccountOptionsBottomSheetToInAppPinNavigation()
        )
    }

    private fun navToViewPassphraseNavigation() {
        nav(
            AccountOptionsBottomSheetDirections
                .actionAccountOptionsBottomSheetToViewPassphraseNavigation(publicKey)
        )
    }

    companion object {
        const val ACCOUNT_REMOVE_ACTION_KEY = "remove_account_action"
    }
}
