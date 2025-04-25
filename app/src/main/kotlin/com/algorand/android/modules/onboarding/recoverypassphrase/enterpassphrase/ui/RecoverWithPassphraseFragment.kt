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

package com.algorand.android.modules.onboarding.recoverypassphrase.enterpassphrase.ui

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.algorand.android.MainNavigationDirections
import com.algorand.android.R
import com.algorand.android.core.DaggerBaseFragment
import com.algorand.android.customviews.LoadingDialogFragment
import com.algorand.android.customviews.PassphraseWordSuggestor
import com.algorand.android.customviews.passphraseinput.PassphraseInputGroup.Listener
import com.algorand.android.customviews.passphraseinput.model.PassphraseInputConfiguration
import com.algorand.android.customviews.passphraseinput.model.PassphraseInputGroupConfiguration
import com.algorand.android.customviews.toolbar.buttoncontainer.model.IconButton
import com.algorand.android.databinding.FragmentRecoverWithPassphraseBinding
import com.algorand.android.models.AccountCreation
import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.modules.onboarding.recoverypassphrase.options.ui.RecoverOptionsBottomSheet
import com.algorand.android.modules.onboarding.recoverypassphrase.options.ui.RecoverOptionsBottomSheet.Companion.RESULT_KEY
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.AnimationLoader
import com.algorand.android.ui.rekeyedaccounts.model.RekeyedAccountSelectionNavArg
import com.algorand.android.utils.Event
import com.algorand.android.utils.delegation.keyboardvisibility.KeyboardHandlerDelegation
import com.algorand.android.utils.delegation.keyboardvisibility.KeyboardHandlerDelegationImpl
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.getTextFromClipboard
import com.algorand.android.utils.getXmlStyledString
import com.algorand.android.utils.hideKeyboard
import com.algorand.android.utils.startSavedStateListener
import com.algorand.android.utils.useSavedStateValue
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class RecoverWithPassphraseFragment : DaggerBaseFragment(R.layout.fragment_recover_with_passphrase),
    KeyboardHandlerDelegation by KeyboardHandlerDelegationImpl(),
    LoadingDialogFragment.DismissListener {

    private val toolbarConfiguration = ToolbarConfiguration(
        startIconResId = R.drawable.ic_left_arrow,
        startIconClick = ::navBack
    )

    override val fragmentConfiguration =
        FragmentConfiguration(toolbarConfiguration = toolbarConfiguration)

    private val args by navArgs<RecoverWithPassphraseFragmentArgs>()

    private val recoverWithPassphraseViewModel: RecoverWithPassphraseViewModel by viewModels()

    private val binding by viewBinding(FragmentRecoverWithPassphraseBinding::bind)

    private val recoverPassphraseTitleHeight by lazy { binding.recoverPassphraseTitle.height }

    private val passphraseInputGroupListener = object : Listener {
        override fun onInputFocus(itemOrder: Int, yCoordinate: Int) {
            recoverWithPassphraseViewModel.onFocusedViewChanged(itemOrder)
            if (isKeyboardVisible) {
                scrollToFocusedInput()
            }
        }

        override fun onFocusedWordChanged(itemOrder: Int, word: String) {
            recoverWithPassphraseViewModel.onFocusedInputChanged(value = word)
        }

        override fun onDoneClick(itemOrder: Int) {
            view?.hideKeyboard()
            recoverWithPassphraseViewModel.onRecoverButtonClick()
        }

        override fun onNextClick(itemOrder: Int) {
            binding.passphraseInputGroup.safeFocusNextItem(itemOrder)
        }

        override fun onClipboardTextPasted(clipboardData: String) {
            recoverWithPassphraseViewModel.onClipboardTextPasted(clipboardData)
        }
    }

    private fun hideComposeAnimationLoader() {
        binding.scrollView.visibility = View.VISIBLE
        binding.passphraseWordSuggestor.visibility = View.VISIBLE
        binding.recoverButton.visibility = View.VISIBLE
        binding.loadingAnimationCompose.visibility = View.GONE
    }

    private fun showComposeAnimationLoader() {
        binding.scrollView.visibility = View.GONE
        binding.passphraseWordSuggestor.visibility = View.GONE
        binding.recoverButton.visibility = View.GONE
        binding.loadingAnimationCompose.visibility = View.VISIBLE
    }

    private val wordSuggestorListener = PassphraseWordSuggestor.Listener { word ->
        recoverWithPassphraseViewModel.onFocusedInputChanged(value = word)
        binding.passphraseInputGroup.focusNextItem()
    }

    private val onKeyboardOpenedListener = KeyboardHandlerDelegationImpl.OnKeyboardOpenedListener {
        scrollToFocusedInput()
    }

    private val showLoadingDialogEventCollector: suspend (Event<Unit>?) -> Unit = { event ->
        event?.consume()?.run { showLoadingFragment() }
    }

    private val suggestedWordsCollector: suspend (List<String>) -> Unit = { suggestedWords ->
        binding.passphraseWordSuggestor.setSuggestedWords(suggestedWords)
    }

    private val recoveryStateCollector: suspend (Boolean) -> Unit = { isEnabled ->
        binding.recoverButton.isEnabled = isEnabled
    }

    private val focusedPassphraseItemCollector: suspend (PassphraseInputConfiguration?) -> Unit = {
        if (it != null) binding.passphraseInputGroup.updatePassphraseInputsConfiguration(it)
    }

    private val unfocusedPassphraseItemCollector: suspend (PassphraseInputConfiguration?) -> Unit =
        {
            if (it != null) binding.passphraseInputGroup.updatePassphraseInputsConfiguration(it)
        }

    private val globalErrorEventCollector: suspend (Event<Int>?) -> Unit = {
        it?.consume()?.run {
            hideLoadingFragment()
            showGlobalError(getString(this))
        }
    }

    private val restorePassphraseInputGroupEventCollector: suspend (
        Event<PassphraseInputGroupConfiguration>?
    ) -> Unit = {
        it?.consume()?.run { restorePassphraseInputGroup(this) }
    }

    private val accountNotFoundEventCollector: suspend (Event<AnnotatedString>?) -> Unit = {
        it?.consume()?.run {
            hideLoadingFragment()
            showErrorBottomSheet(this)
        }
    }

    private val navToNameRegistrationScreenEventCollector: suspend (Event<AccountCreation>?) -> Unit =
        {
            it?.consume()?.run { navigateToSuccess(this) }
        }

    private val navToImportRekeyedAccountEventCollector: suspend (
        Event<Pair<AccountCreation, RekeyedAccountSelectionNavArg>>?
    ) -> Unit = { event ->
        event?.consume()?.let { (accountCreation, rekeyedAccountSelectionNavArg) ->
            navToImportRekeyedAccount(accountCreation, rekeyedAccountSelectionNavArg)
        }
    }

    private val showErrorEventCollector: suspend (Event<AnnotatedString>?) -> Unit = { errorEvent ->
        errorEvent?.consume()?.let { errorAnnotatedString ->
            val errorMessage = context?.getXmlStyledString(errorAnnotatedString)
            showGlobalError(errorMessage = errorMessage, tag = baseActivityTag)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerKeyboardHandlerDelegation(
            baseFragment = this,
            onKeyboardOpenedListener = onKeyboardOpenedListener
        )
        binding.loadingAnimationCompose.setContent {
            PeraTheme {
                Box {
                    AnimationLoader(
                        modifier = Modifier.align(alignment = Alignment.Center),
                        start = ImageVector.vectorResource(R.drawable.ic_ledger_old_export),
                        end = ImageVector.vectorResource(R.drawable.ic_phone_new),
                        lottie = LottieCompositionSpec.RawRes(resId = R.raw.loading_dots),
                        description = "Searching your accounts"
                    )
                }
            }
        }
        initUi()
        loadData()
        initObservers()
        customizeToolbar()
    }

    override fun onStart() {
        super.onStart()
        initSavedStateListener()
    }

    private fun initUi() {
        initPassphraseInputGroup(recoverWithPassphraseViewModel.getInitialPassphraseInputGroupConfiguration())
        with(binding) {
            passphraseInputGroup.setListener(passphraseInputGroupListener)
            passphraseWordSuggestor.listener = wordSuggestorListener
            recoverButton.setOnClickListener {
                recoverWithPassphraseViewModel.onRecoverButtonClick()
            }
        }
    }

    private fun loadData() {
        args.mnemonic?.let { mnemonic ->
            recoverWithPassphraseViewModel.onClipboardTextPasted(mnemonic)
        }
    }

    private fun initSavedStateListener() {
        startSavedStateListener(R.id.recoverWithPassphraseFragment) {
            useSavedStateValue<RecoverOptionsBottomSheet.OptionResult>(RESULT_KEY) { optionResult ->
                when (optionResult) {
                    RecoverOptionsBottomSheet.OptionResult.PASTE -> pasteClipboard()
                }
            }
        }
    }

    private fun initObservers() {
        with(recoverWithPassphraseViewModel.recoverWithPassphrasePreviewFlow) {
            collectLatestOnLifecycle(
                flow = map { it.suggestedWords }.distinctUntilChanged(),
                collection = suggestedWordsCollector
            )
            collectLatestOnLifecycle(
                flow = map { it.isRecoveryEnabled }.distinctUntilChanged(),
                collection = recoveryStateCollector
            )
            collectLatestOnLifecycle(
                flow = map { it.passphraseInputGroupConfiguration.focusedPassphraseItem }.distinctUntilChanged(),
                collection = focusedPassphraseItemCollector
            )
            collectLatestOnLifecycle(
                flow = map { it.passphraseInputGroupConfiguration.unfocusedPassphraseItem }.distinctUntilChanged(),
                collection = unfocusedPassphraseItemCollector
            )
            collectLatestOnLifecycle(
                flow = map { it.onGlobalErrorEvent }.distinctUntilChanged(),
                collection = globalErrorEventCollector
            )
            collectLatestOnLifecycle(
                flow = map { it.onRestorePassphraseInputGroupEvent }.distinctUntilChanged(),
                collection = restorePassphraseInputGroupEventCollector
            )
            collectLatestOnLifecycle(
                flow = map { it.navToNameRegistrationEvent }.distinctUntilChanged(),
                collection = navToNameRegistrationScreenEventCollector
            )
            collectLatestOnLifecycle(
                flow = map { it.onAccountNotFoundEvent }.distinctUntilChanged(),
                collection = accountNotFoundEventCollector
            )
            collectLatestOnLifecycle(
                flow = map { it.showLoadingDialogEvent }.distinctUntilChanged(),
                collection = showLoadingDialogEventCollector
            )
            collectLatestOnLifecycle(
                flow = map { it.navToImportRekeyedAccountEvent }.distinctUntilChanged(),
                collection = navToImportRekeyedAccountEventCollector
            )
            collectLatestOnLifecycle(
                flow = map { it.showErrorEvent }.distinctUntilChanged(),
                collection = showErrorEventCollector
            )
        }
    }

    override fun onPause() {
        super.onPause()
        view?.hideKeyboard()
    }

    private fun customizeToolbar() {
        getAppToolbar()?.setEndButton(
            button = IconButton(
                R.drawable.ic_more,
                onClick = ::onOptionsClick
            )
        )
    }

    private fun pasteClipboard() {
        val pastedPassphrase = context?.getTextFromClipboard().toString()
        recoverWithPassphraseViewModel.onClipboardTextPasted(pastedPassphrase)
    }

    private fun navigateToSuccess(accountCreation: AccountCreation) {
        when (accountCreation.type) {
            is AccountCreation.Type.Algo25 -> {
                nav(
                    RecoverWithPassphraseFragmentDirections
                        .actionRecoverWithPassphraseFragmentToRecoverAccountNameRegistrationFragment(
                            accountCreation = accountCreation
                        )
                )
            }

            is AccountCreation.Type.HdKey -> {
                nav(
                    RecoverWithPassphraseFragmentDirections
                        .actionRecoverWithPassphraseFragmentToRecoverRegisteredAccountsFragment(
                            accountCreation = accountCreation
                        )
                )
            }
            else -> {}
        }
    }

    private fun showErrorBottomSheet(descriptionString: AnnotatedString) {
        nav(
            MainNavigationDirections.actionGlobalSingleButtonBottomSheet(
                titleAnnotatedString = AnnotatedString(R.string.wrong_passphrase),
                drawableResId = R.drawable.ic_error,
                drawableTintResId = R.color.error_tint_color,
                descriptionAnnotatedString = descriptionString,
                isResultNeeded = false,
            )
        )
    }

    private fun onOptionsClick() {
        nav(RecoverWithPassphraseFragmentDirections.actionRecoverWithPassphraseFragmentToRecoverOptionsBottomSheet())
    }

    private fun scrollToFocusedInput() {
        with(binding) {
            val focusedInput = passphraseInputGroup.focusedChild ?: return
            scrollView.smoothScrollTo(
                0,
                (focusedInput.y - focusedInput.height + recoverPassphraseTitleHeight).toInt()
            )
        }
    }

    private fun restorePassphraseInputGroup(passphraseInputGroupConfiguration: PassphraseInputGroupConfiguration) {
        binding.passphraseInputGroup.updatePassphraseInputsConfiguration(
            passphraseInputConfigurationList = passphraseInputGroupConfiguration.passphraseInputConfigurationList
        )
    }

    private fun initPassphraseInputGroup(passphraseInputGroupConfiguration: PassphraseInputGroupConfiguration) {
        binding.passphraseInputGroup.initPassphraseInputGroup(
            passphraseInputConfigurationList = passphraseInputGroupConfiguration.passphraseInputConfigurationList
        )
    }

    override fun onLoadingDialogDismissed() {
        recoverWithPassphraseViewModel.cancelAccountRecoveryJob()
        hideLoadingFragment()
    }

    private fun showLoadingFragment() {
        hideLoadingFragment()
        showComposeAnimationLoader()
    }

    private fun hideLoadingFragment() {
        hideComposeAnimationLoader()
    }

    private fun navToImportRekeyedAccount(
        accountCreation: AccountCreation,
        navArg: RekeyedAccountSelectionNavArg
    ) {
        nav(
            RecoverWithPassphraseFragmentDirections
                .actionRecoverWithPassphraseFragmentToRecoverSingleAddressRekeyedAccountSelectionFragment(
                    accountCreation = accountCreation,
                    rekeyedAccountSelectionNavArg = navArg
                )
        )
    }
}
