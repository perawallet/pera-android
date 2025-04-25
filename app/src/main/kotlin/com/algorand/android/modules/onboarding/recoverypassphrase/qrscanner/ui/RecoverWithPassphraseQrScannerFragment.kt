/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.onboarding.recoverypassphrase.qrscanner.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.algorand.android.R
import com.algorand.android.modules.onboarding.recoverypassphrase.qrscanner.ui.RecoverWithPassphraseQrScannerFragmentDirections.Companion.actionRecoverWithPassphraseQrScannerFragmentToRecoverWithPassphraseNavigation
import com.algorand.android.modules.qrscanning.BaseQrScannerFragment
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecoverWithPassphraseQrScannerFragment : BaseQrScannerFragment(R.id.recoverWithPassphraseQrScannerFragment) {

    private val viewEventCollector: suspend (RecoverWithPassphraseQrScannerViewModel.ViewEvent) -> Unit = { event ->
        when (event) {
            is RecoverWithPassphraseQrScannerViewModel.ViewEvent.NavToRecoverWithPassphraseNavigation ->
                navToRecoverWithPassphraseNavigation(event.mnemonic)

            is RecoverWithPassphraseQrScannerViewModel.ViewEvent.ShowMaxAccountLimitExceededError ->
                showMaxAccountLimitExceededError()
        }
    }

    private val recoverWithPassphraseQrScannerViewModel: RecoverWithPassphraseQrScannerViewModel by viewModels()

    override fun onImportAccountDeepLink(mnemonic: String): Boolean {
        return true.also {
            recoverWithPassphraseQrScannerViewModel.onImportAccountDeepLink(mnemonic)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
    }

    override fun initObservers() {
        super.initObservers()
        viewLifecycleOwner.collectLatestOnLifecycle(
            recoverWithPassphraseQrScannerViewModel.viewEvent,
            viewEventCollector
        )
    }

    private fun navToRecoverWithPassphraseNavigation(mnemonic: String) {
        nav(actionRecoverWithPassphraseQrScannerFragmentToRecoverWithPassphraseNavigation(mnemonic))
    }
}
