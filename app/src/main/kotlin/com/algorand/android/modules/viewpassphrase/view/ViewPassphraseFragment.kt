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

package com.algorand.android.modules.viewpassphrase.view

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.algorand.android.R
import com.algorand.android.core.DaggerBaseFragment
import com.algorand.android.databinding.FragmentViewPassphraseBinding
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.utils.disableScreenCapture
import com.algorand.android.utils.enableScreenCapture
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.extensions.show
import com.algorand.android.utils.viewbinding.viewBinding
import com.algorand.wallet.ui.accountdetail.viewpassphrase.ViewPassphraseViewModel
import com.algorand.wallet.ui.accountdetail.viewpassphrase.ViewPassphraseViewModel.ViewEvent.NavigateBack
import com.algorand.wallet.ui.accountdetail.viewpassphrase.ViewPassphraseViewModel.ViewEvent.ShowGenericError
import com.algorand.wallet.ui.accountdetail.viewpassphrase.ViewPassphraseViewModel.ViewState.Content
import com.algorand.wallet.ui.accountdetail.viewpassphrase.ViewPassphraseViewModel.ViewState.Idle
import com.algorand.wallet.ui.accountdetail.viewpassphrase.ViewPassphraseViewModel.ViewState.Loading
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewPassphraseFragment : DaggerBaseFragment(R.layout.fragment_view_passphrase) {

    private val toolbarConfiguration = ToolbarConfiguration(
        titleResId = R.string.passphrase,
        startIconResId = R.drawable.ic_left_arrow,
        startIconClick = ::navBack
    )

    override val fragmentConfiguration = FragmentConfiguration()

    private val binding by viewBinding(FragmentViewPassphraseBinding::bind)

    private val viewPassphraseViewModel: ViewPassphraseViewModel by viewModels()

    private var isScreenCaptureEnablingAllowed = true

    private val onWindowFocusChangeListener = ViewTreeObserver.OnWindowFocusChangeListener { hasFocus ->
        isScreenCaptureEnablingAllowed = hasFocus
    }

    private val viewStateCollector: suspend (ViewPassphraseViewModel.ViewState) -> Unit = {
        updateViewState(it)
    }

    private val viewEventCollector: suspend (ViewPassphraseViewModel.ViewEvent) -> Unit = {
        when (it) {
            ShowGenericError -> showGlobalError(getString(R.string.an_error_occured), tag = baseActivityTag)
            NavigateBack -> navBack()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewPassphraseToolbar.configure(toolbarConfiguration)
        initObserver()
    }

    private fun initObserver() {
        collectLatestOnLifecycle(viewPassphraseViewModel.viewEvent, viewEventCollector, Lifecycle.State.CREATED)
        collectLatestOnLifecycle(viewPassphraseViewModel.state, viewStateCollector)
    }

    override fun onResume() {
        super.onResume()
        view?.viewTreeObserver?.addOnWindowFocusChangeListener(onWindowFocusChangeListener)
        activity?.disableScreenCapture()
        val address = requireArguments().getString(ACCOUNT_ADDRESS).orEmpty()
        viewPassphraseViewModel.initViewState(address)
    }

    override fun onStop() {
        if (isScreenCaptureEnablingAllowed) {
            activity?.enableScreenCapture()
        }
        view?.viewTreeObserver?.removeOnWindowFocusChangeListener(onWindowFocusChangeListener)
        super.onStop()
    }

    private fun updateViewState(state: ViewPassphraseViewModel.ViewState) {
        when (state) {
            Idle -> Unit
            Loading -> binding.progressBar.show()
            is Content -> showMnemonics(state.mnemonicWords)
        }
    }

    private fun showMnemonics(mnemonicWords: List<String>) {
        binding.progressBar.hide()
        binding.passphraseBoxView.apply {
            setPassphrases(mnemonicWords)
            show()
        }
    }

    private companion object {
        const val ACCOUNT_ADDRESS = "accountAddress"
    }
}
