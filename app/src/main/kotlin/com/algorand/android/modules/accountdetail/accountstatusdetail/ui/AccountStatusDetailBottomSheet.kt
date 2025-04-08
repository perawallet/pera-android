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

package com.algorand.android.modules.accountdetail.accountstatusdetail.ui

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.algorand.android.R
import com.algorand.android.core.BaseBottomSheet
import com.algorand.android.databinding.BottomSheetAccountStatusDetailBinding
import com.algorand.android.modules.accountdetail.accountstatusdetail.ui.AccountStatusDetailViewModel.ViewEvent
import com.algorand.android.modules.accountdetail.accountstatusdetail.ui.AccountStatusDetailViewModel.ViewState
import com.algorand.android.utils.AccountIconDrawable
import com.algorand.android.utils.browser.ACCOUNT_SUPPORT_URL
import com.algorand.android.utils.browser.LEDGER_HELP_WEB_URL
import com.algorand.android.utils.browser.REKEY_SUPPORT_URL
import com.algorand.android.utils.browser.WATCH_SUPPORT_URL
import com.algorand.android.utils.browser.openUrl
import com.algorand.android.utils.getCustomClickableSpan
import com.algorand.android.utils.getXmlStyledString
import com.algorand.android.utils.setDrawable
import com.algorand.android.utils.viewbinding.viewBinding
import com.algorand.wallet.account.detail.domain.model.AccountType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountStatusDetailBottomSheet : BaseBottomSheet(R.layout.bottom_sheet_account_status_detail) {

    private val viewModel by viewModels<AccountStatusDetailViewModel>()
    private val binding by viewBinding(BottomSheetAccountStatusDetailBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initObservers()
    }

    private fun initUi() {
        with(binding) {
            accountStateDescriptionTextView.apply {
                highlightColor = ContextCompat.getColor(context, R.color.transparent)
                movementMethod = LinkMovementMethod.getInstance()
            }
            rekeyToStandardAccountButton.setOnClickListener { viewModel.onRekeyToStandardAccountClicked() }
            rekeyToLedgerAccountButton.setOnClickListener { viewModel.onRekeyToLedgerAccountClicked() }
        }
    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    renderState(state)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewEvent.collectLatest { event ->
                    handleEvent(event)
                }
            }
        }
    }

    private fun renderState(state: ViewState) {
        when (state) {
            is ViewState.Idle, is ViewState.Loading, is ViewState.Error -> {
                // Handle these states later when scren is in compose
            }
            is ViewState.Content -> {
                renderContentState(state)
            }
        }
    }

    @Suppress("LongMethod")
    private fun renderContentState(state: ViewState.Content) {
        with(binding) {
            state.accountOriginalTypeDisplayName?.let { displayName ->
                accountItemView.apply {
                    setTitleText(displayName.primaryDisplayName)
                    setDescriptionText(displayName.secondaryDisplayName)
                    setOnLongClickListener {
                        onAccountAddressCopied(displayName.accountAddress)
                        true
                    }
                }
            }

            state.accountOriginalTypeIconDrawablePreview?.let { drawablePreview ->
                accountItemView.apply {
                    val drawable = AccountIconDrawable.create(context, R.dimen.spacing_xxxxlarge, drawablePreview)
                    setStartIconDrawable(drawable)
                }
            }

            accountTypeTextView.text = state.titleString
            accountStateTextView.text = state.accountTypeString

            state.accountTypeDrawablePreview?.let { drawablePreview ->
                accountStateTextView.apply {
                    val drawable = AccountIconDrawable.create(context, R.dimen.spacing_xxxxlarge, drawablePreview)
                    setDrawable(start = drawable)
                }
            }

            state.authAccountDisplayName?.let { displayName ->
                authAccountItemView.apply {
                    setTitleText(displayName.primaryDisplayName)
                    setDescriptionText(displayName.secondaryDisplayName)
                    setOnLongClickListener {
                        onAccountAddressCopied(displayName.accountAddress)
                        true
                    }
                }
            }

            state.authAccountIconDrawablePreview?.let { drawablePreview ->
                authAccountItemView.apply {
                    val drawable = AccountIconDrawable.create(context, R.dimen.spacing_xxxxlarge, drawablePreview)
                    setStartIconDrawable(drawable)
                }
            }

            state.descriptionAnnotatedString?.let { annotatedString ->
                val linkTextColor = ContextCompat.getColor(binding.root.context, R.color.link_primary)
                val clickSpannable = getCustomClickableSpan(
                    clickableColor = linkTextColor,
                    onClick = {
                        when (state.accountDetail?.accountType) {
                            AccountType.Algo25 -> context?.openUrl(ACCOUNT_SUPPORT_URL)
                            AccountType.HdKey -> context?.openUrl(ACCOUNT_SUPPORT_URL)
                            AccountType.LedgerBle -> context?.openUrl(LEDGER_HELP_WEB_URL)
                            AccountType.NoAuth -> context?.openUrl(WATCH_SUPPORT_URL)
                            AccountType.Rekeyed -> context?.openUrl(REKEY_SUPPORT_URL)
                            AccountType.RekeyedAuth -> context?.openUrl(REKEY_SUPPORT_URL)
                            null -> context?.openUrl(ACCOUNT_SUPPORT_URL)
                        }
                    }
                )
                val clickableAnnotatedString = annotatedString.copy(
                    customAnnotationList = listOf("learn_more" to clickSpannable)
                )
                accountStateDescriptionTextView.text = context?.getXmlStyledString(clickableAnnotatedString)
            }

            rekeyGroup.isVisible = state.isRekeyGroupVisible == true
            rekeyToLedgerAccountButton.isVisible = state.isRekeyToLedgerAccountVisible == true
            rekeyToStandardAccountButton.isVisible = state.isRekeyToStandardAccountVisible == true

            state.accountOriginalActionButton?.let { buttonState ->
                accountItemView.apply {
                    setButtonState(buttonState)
                    setActionButtonClickListener { viewModel.onAccountActionButtonClicked() }
                }
            }

            state.authAccountActionButton?.let { buttonState ->
                authAccountItemView.apply {
                    setButtonState(buttonState)
                    setActionTextButtonClickListener { viewModel.onAuthAccountActionButtonClicked() }
                }
            }
        }
    }

    private fun handleEvent(event: ViewEvent) {
        when (event) {
            is ViewEvent.CopyAccountAddressToClipboard -> {
                onAccountAddressCopied(event.address)
            }
            is ViewEvent.NavigateToUndoRekey -> {
                navToUndoRekeyNavigation()
            }
            is ViewEvent.NavigateToRekeyToStandardAccount -> {
                navToRekeyToStandardAccountNavigation()
            }
            is ViewEvent.NavigateToRekeyToLedgerAccount -> {
                navToRekeyToLedgerAccountNavigation()
            }
        }
    }

    private fun navToRekeyToLedgerAccountNavigation() {
        nav(
            AccountStatusDetailBottomSheetDirections
                .actionAccountStatusDetailBottomSheetToRekeyLedgerNavigation(viewModel.accountAddress)
        )
    }

    private fun navToRekeyToStandardAccountNavigation() {
        nav(
            AccountStatusDetailBottomSheetDirections
                .actionAccountStatusDetailBottomSheetToRekeyToStandardAccountNavigation(viewModel.accountAddress)
        )
    }

    private fun navToUndoRekeyNavigation() {
        nav(
            AccountStatusDetailBottomSheetDirections
                .actionAccountStatusDetailBottomSheetToRekeyUndoNavigation(viewModel.accountAddress)
        )
    }
}
