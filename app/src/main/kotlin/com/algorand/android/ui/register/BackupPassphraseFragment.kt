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

package com.algorand.android.ui.register

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.algorand.android.R
import com.algorand.android.core.DaggerBaseFragment
import com.algorand.android.customviews.toolbar.buttoncontainer.model.TextButton
import com.algorand.android.databinding.FragmentBackupPassphraseBinding
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.modules.tracking.core.PeraClickEvent
import com.algorand.android.ui.register.BackupPassphraseFragmentDirections.Companion.actionBackupPassphraseFragmentToBackupPassphraseAccountNameNavigation
import com.algorand.android.utils.disableScreenCapture
import com.algorand.android.utils.enableScreenCapture
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BackupPassphraseFragment : DaggerBaseFragment(R.layout.fragment_backup_passphrase) {

    private val viewStateCollector: suspend (BackupPassphraseViewModel.ViewState) -> Unit = { state ->
        when (state) {
            is BackupPassphraseViewModel.ViewState.Idle -> Unit
            is BackupPassphraseViewModel.ViewState.DefaultState -> setupPassphrase(state.passphrase)
        }
    }

    private val toolbarConfiguration = ToolbarConfiguration(
        startIconResId = R.drawable.ic_left_arrow,
        startIconClick = ::navBack,
        backgroundColor = R.color.tertiary_background
    )

    override val fragmentConfiguration = FragmentConfiguration(
        toolbarConfiguration = toolbarConfiguration
    )

    private val binding by viewBinding(FragmentBackupPassphraseBinding::bind)

    private val backupPassphraseViewModel: BackupPassphraseViewModel by viewModels()

    private val args: BackupPassphraseFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        customizeToolbar()
        initObservers()

        backupPassphraseViewModel.getMnemonic(args)
        binding.nextButton.setOnClickListener { onNextClick() }
    }

    override fun onResume() {
        super.onResume()
        activity?.disableScreenCapture()
    }

    private fun initObservers() {
        collectLatestOnLifecycle(
            flow = backupPassphraseViewModel.state,
            collection = viewStateCollector
        )
    }

    private fun customizeToolbar() {
        if (args.accountCreation != null) {
            getAppToolbar()?.setEndButton(button = TextButton(R.string.skip, onClick = ::onSkipClick))
        }
    }

    override fun onStop() {
        super.onStop()
        if (view?.hasWindowFocus() == true) {
            activity?.enableScreenCapture()
        }
    }

    private fun setupPassphrase(passphrase: String?) {
        if (passphrase.isNullOrEmpty()) {
            return navBack()
        }
        binding.passphraseBoxView.setPassphrases(passphrase)
    }

    private fun onNextClick() {
        navToPassphraseValidationFragment()
    }

    private fun onSkipClick() {
        backupPassphraseViewModel.logEvent(PeraClickEvent.TAP_ONBOARDING_RECOVER_PASSPHRASE_SKIP)
        navToBackupPassphraseAccountNameNavigation()
    }

    private fun navToPassphraseValidationFragment() {
        backupPassphraseViewModel.logOnboardingNextClickEvent()
        nav(
            BackupPassphraseFragmentDirections.actionBackupPassphraseFragmentToPassphraseValidationFragment(
                args.accountToBackup,
                args.accountCreation
            )
        )
    }

    private fun navToBackupPassphraseAccountNameNavigation() {
        backupPassphraseViewModel.logOnboardingNextClickEvent()
        args.accountCreation?.let { accountCreation ->
            nav(actionBackupPassphraseFragmentToBackupPassphraseAccountNameNavigation(accountCreation))
        }
    }
}
