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

package com.algorand.android.ui.accounts

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.algorand.android.R
import com.algorand.android.core.DaggerBaseFragment
import com.algorand.android.databinding.FragmentViewPassphraseBinding
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.utils.disableScreenCapture
import com.algorand.android.utils.enableScreenCapture
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ViewPassphraseFragment : DaggerBaseFragment(R.layout.fragment_view_passphrase) {

    private val toolbarConfiguration = ToolbarConfiguration(
        titleResId = R.string.passphrase,
        startIconResId = R.drawable.ic_left_arrow,
        startIconClick = ::navBack
    )

    override val fragmentConfiguration = FragmentConfiguration()

    private val binding by viewBinding(FragmentViewPassphraseBinding::bind)

    private val viewModel: ViewPassphraseViewModel by viewModels()

    private var isScreenCaptureEnablingAllowed = true

    private val onWindowFocusChangeListener = ViewTreeObserver.OnWindowFocusChangeListener { hasFocus ->
        isScreenCaptureEnablingAllowed = hasFocus
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewPassphraseToolbar.configure(toolbarConfiguration)
        observeState()
        observeEffects()

        viewModel.processIntent(ViewPassphraseContract.Intent.LoadMnemonic)
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    handleState(state)
                }
            }
        }
    }

    private fun handleState(state: ViewPassphraseContract.State) {
        when {
            state.isLoading -> {
                // show a loading indicator here if we want
            }
            state.error != null -> {
                showError(state.error)
            }
            state.mnemonic != null -> {
                binding.passphraseBoxView.setPassphrases(state.mnemonic)
            }
            else -> {
                navBack()
            }
        }
    }

    private fun showError(error: String) {
        showGlobalError(errorMessage = error)
    }

    private fun observeEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effect.collectLatest { effect ->
                    when (effect) {
                        is ViewPassphraseContract.Effect.NavigateBack -> navBack()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        view?.viewTreeObserver?.addOnWindowFocusChangeListener(onWindowFocusChangeListener)
        activity?.disableScreenCapture()
    }

    override fun onStop() {
        if (isScreenCaptureEnablingAllowed) {
            activity?.enableScreenCapture()
        }
        view?.viewTreeObserver?.removeOnWindowFocusChangeListener(onWindowFocusChangeListener)
        super.onStop()
    }
}
