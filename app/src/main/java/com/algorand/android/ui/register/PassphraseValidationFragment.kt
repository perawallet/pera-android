/*
 *  Copyright 2022 Pera Wallet, LDA
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.ui.register

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.algorand.algosdk.sdk.Sdk
import com.algorand.android.R
import com.algorand.android.core.DaggerBaseFragment
import com.algorand.android.customviews.PassphraseValidationGroupView
import com.algorand.android.databinding.FragmentPassphraseValidationBinding
import com.algorand.android.models.*
import com.algorand.android.ui.register.PassphraseValidationFragmentDirections.Companion.actionPassphraseValidationFragmentToPassphraseVerifiedInfoFragment
import com.algorand.android.utils.singleVibrate
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PassphraseValidationFragment : DaggerBaseFragment(R.layout.fragment_passphrase_validation) {

    private val toolbarConfiguration = ToolbarConfiguration(
        backgroundColor = R.color.primary_background,
        startIconResId = R.drawable.ic_left_arrow,
        startIconClick = ::navBack
    )

    override val fragmentConfiguration = FragmentConfiguration(
        toolbarConfiguration = toolbarConfiguration
    )

    private val binding by viewBinding(FragmentPassphraseValidationBinding::bind)

    private val args: PassphraseValidationFragmentArgs by navArgs()

    private val passphraseValidationViewModel: PassphraseValidationViewModel by viewModels()

    private val passphraseValidationGroupListener = object : PassphraseValidationGroupView.Listener {
        override fun onInputUpdate(allWordsSelected: Boolean) {
            binding.nextButton.isEnabled = allWordsSelected
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPassphraseValidationView()
        binding.nextButton.setOnClickListener { onNextClick() }
    }

    private fun getPassphraseWords(): List<String> {
        val secretKey = (args.accountCreation as? CreateAccount.Algo25)?.secretKey
        return try {
            Sdk.mnemonicFromPrivateKey(secretKey).split(" ")
        } catch (exception: Exception) {
            emptyList()
        }
    }

    private fun setupPassphraseValidationView() {
        binding.passphraseValidationGroupView.setupUI(getPassphraseWords(), passphraseValidationGroupListener)
    }

    private fun onNextClick() {
        passphraseValidationViewModel.logOnboardingNextClickEvent()
        if (binding.passphraseValidationGroupView.isValidated()) {
            passphraseValidationViewModel.updateAccountBackupState(
                args.publicKeyOfAccountToBackup,
                isBackedUp = true
            )
            navToPassphraseVerifiedInfoFragment()
        } else {
            showGlobalError(errorMessage = getString(R.string.selected_words_are))
            binding.passphraseValidationGroupView.recreateUI(getPassphraseWords())
            context?.singleVibrate()
        }
    }

    private fun navToPassphraseVerifiedInfoFragment() {
        nav(actionPassphraseValidationFragmentToPassphraseVerifiedInfoFragment(args.accountCreation))
    }
}
