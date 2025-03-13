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

    protected val accountOptionsViewModel: AccountOptionsViewModel by viewModels()

    abstract val accountAddress: String

    abstract fun navToShowQrFragment(title: String, accountAddress: String)

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
                onAccountAddressCopied(accountOptionsViewModel.accountAddress)
                navBack()
            }
            addressTextView.text = accountOptionsViewModel.accountAddress
        }
    }

    private fun setupShowQrButton() {
        binding.showQrButton.setOnClickListener {
            navToShowQrFragment(getString(R.string.qr_code), accountOptionsViewModel.accountAddress)
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
}
