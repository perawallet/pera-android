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
import com.algorand.android.R
import com.algorand.android.core.DaggerBaseBottomSheet
import com.algorand.android.databinding.BottomSheetAccountsOptionsBinding
import com.algorand.android.utils.extensions.show
import com.algorand.android.utils.viewbinding.viewBinding

abstract class BaseAccountOptionsBottomSheet : DaggerBaseBottomSheet(
    layoutResId = R.layout.bottom_sheet_accounts_options,
    fullPageNeeded = false,
    firebaseEventScreenId = null
) {

    protected val binding by viewBinding(BottomSheetAccountsOptionsBinding::bind)

    private val accountOptionsViewModel: AccountOptionsViewModel by viewModels()

    abstract val publicKey: String

    abstract fun navToShowQrFragment(title: String, publicKey: String)

    abstract fun navToViewPassphraseNavigation()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCopyButton()
        setupShowQrButton()
        setupViewPassphraseButton()
    }

    private fun setupCopyButton() {
        with(binding) {
            copyAddressLayout.setOnClickListener {
                onAccountAddressCopied(accountOptionsViewModel.getAccountAddress())
                navBack()
            }
            addressTextView.text = accountOptionsViewModel.getAccountAddress()
        }
    }

    private fun setupShowQrButton() {
        binding.showQrButton.setOnClickListener {
            navToShowQrFragment(getString(R.string.qr_code), publicKey)
        }
    }

    private fun setupViewPassphraseButton() {
        if (accountOptionsViewModel.canDisplayPassphrases()) {
            binding.viewPassphraseButton.apply {
                show()
                setOnClickListener { navToViewPassphraseNavigation() }
            }
        }
    }

    companion object {
        private const val ADDRESS_COPY_LABEL = "address"
    }
}
