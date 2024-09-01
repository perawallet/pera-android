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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.algorand.android.R
import com.algorand.android.modules.accountdetail.removeaccount.ui.RemoveAccountConfirmationBottomSheet.Companion.ACCOUNT_REMOVE_CONFIRMATION_KEY
import com.algorand.android.utils.extensions.show
import com.algorand.android.utils.useFragmentResultListenerValue
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountErrorOptionsBottomSheet : BaseAccountOptionsBottomSheet() {

    private val args by navArgs<AccountErrorOptionsBottomSheetArgs>()

    private val accountErrorOptionsViewModel by viewModels<AccountErrorOptionsViewModel>()

    override val publicKey: String
        get() = args.publicKey

    override fun onResume() {
        super.onResume()
        initSavedStateListener()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initErrorTextView()
        setupRemoveAccountButton()
    }

    override fun navToShowQrFragment(title: String, publicKey: String) {
        nav(AccountErrorOptionsBottomSheetDirections.actionGlobalShowQrNavigation(title, publicKey))
    }

    override fun navToViewPassphraseNavigation() {
        nav(
            AccountErrorOptionsBottomSheetDirections
                .actionAccountErrorOptionsBottomSheetToViewPassphraseNavigation(publicKey)
        )
    }

    private fun initSavedStateListener() {
        useFragmentResultListenerValue<Boolean>(ACCOUNT_REMOVE_CONFIRMATION_KEY) { isConfirmed ->
            if (isConfirmed) {
                accountErrorOptionsViewModel.removeAccount(publicKey) {
                    navBack()
                }
            }
        }
    }

    private fun initErrorTextView() {
        binding.errorGroupLayout.apply {
            errorTextView.text = getString(R.string.sorry_we_cant_show_account)
            errorGroup.show()
        }
    }

    private fun setupRemoveAccountButton() {
        binding.disconnectAccountButton.apply {
            setOnClickListener { navToDisconnectAccountConfirmationBottomSheet() }
            show()
        }
    }

    private fun navToDisconnectAccountConfirmationBottomSheet() {
        nav(
            AccountErrorOptionsBottomSheetDirections
                .actionAccountErrorOptionsBottomSheetToRemoveAccountConfirmationNavigation(
                    accountAddress = publicKey
                )
        )
    }
}
