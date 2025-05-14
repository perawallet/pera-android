/*
 * Copyright 2022-2025 Pera Wallet, LDA
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
import com.algorand.android.R
import com.algorand.android.core.BaseBottomSheet
import com.algorand.android.databinding.BottomSheetAccountStatusDetailBinding
import com.algorand.android.models.AccountCreation
import com.algorand.android.modules.accountdetail.accountstatusdetail.ui.AccountStatusDetailViewModel.ViewEvent
import com.algorand.android.modules.accountdetail.accountstatusdetail.ui.AccountStatusDetailViewModel.ViewState
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.ui.compose.widget.AddressCard
import com.algorand.android.utils.AccountIconDrawable
import com.algorand.android.utils.browser.openUrl
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.getCustomClickableSpan
import com.algorand.android.utils.getXmlStyledString
import com.algorand.android.utils.setDrawable
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountStatusDetailBottomSheet :
    BaseBottomSheet(R.layout.bottom_sheet_account_status_detail) {

    private val viewModel by viewModels<AccountStatusDetailViewModel>()
    private val binding by viewBinding(BottomSheetAccountStatusDetailBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initObservers()
        viewModel.loadAccountStatusDetail()
    }

    private fun initUi() {
        with(binding) {
            accountStateDescriptionTextView.apply {
                highlightColor = ContextCompat.getColor(context, R.color.transparent)
                movementMethod = LinkMovementMethod.getInstance()
            }
            rekeyToStandardAccountButton.setOnClickListener { onNavigateToRekeyToStandardAccount() }
            rekeyToLedgerAccountButton.setOnClickListener { onNavigateToRekeyToLedgerAccount() }
        }
    }

    private val viewStateCollector: suspend (ViewState) -> Unit = { state ->
        when (state) {
            is ViewState.Idle, is ViewState.Loading, is ViewState.Error -> {
                // Handle these states later when scren is in compose
            }

            is ViewState.Content -> {
                renderContentState(state)
            }
        }
    }

    private val viewEventCollector: suspend (ViewEvent) -> Unit = { event ->
        when (event) {
            is ViewEvent.CopyAccountAddressToClipboard -> {
                onCopyAccountAddressToClipboard(event.address)
            }

            is ViewEvent.NavigateToUndoRekey -> {
                onNavigateToUndoRekey()
            }

            is ViewEvent.NavigateToRekeyToStandardAccount -> {
                onNavigateToRekeyToStandardAccount()
            }

            is ViewEvent.NavigateToRekeyToLedgerAccount -> {
                onNavigateToRekeyToLedgerAccount()
            }

            is ViewEvent.NavigateToHdScanNewAddresses -> {
                onNavigateToHdScanNewAddresses(event.accountCreation)
            }
        }
    }

    private fun initObservers() {
        collectLatestOnLifecycle(viewModel.viewEvent, viewEventCollector, Lifecycle.State.CREATED)
        collectLatestOnLifecycle(viewModel.state, viewStateCollector)
    }

    private fun renderContentState(state: ViewState.Content) {
        setupOriginalAccountDetails(state)
        setupAccountTypeInfo(state)
        setupAuthAccountDetails(state)
        setupDescriptionText(state)
        setupVisibility(state)
        setupButtons(state)
    }

    private fun setupOriginalAccountDetails(state: ViewState.Content) {
        with(binding) {
            state.accountOriginalTypeDisplayName?.let { displayName ->
                accountItemView.setContent {
                    PeraTheme {
                        AddressCard(
                            name = displayName.primaryDisplayName,
                            address = displayName.accountAddress,
                            hdWallet = state.isHdWallet == true,
                            rekey = state.isRekeyGroupVisible == true,
                            onCopyClick = { onCopyAccountAddressToClipboard(viewModel.accountAddress) },
                            onHdScanNewAddressesClick = { viewModel.navToHdScanNewAddresses() },
                        )
                    }
                }
                accountItemView.apply {
                    setOnLongClickListener {
                        onCopyAccountAddressToClipboard(displayName.accountAddress)
                        true
                    }
                }
            }
        }
    }

    private fun setupAccountTypeInfo(state: ViewState.Content) {
        with(binding) {
            accountTypeTextView.text = state.titleString
            accountStateTextView.text = state.accountTypeString

            state.accountTypeDrawablePreview?.let { drawablePreview ->
                val drawable = AccountIconDrawable.create(
                    requireContext(),
                    R.dimen.spacing_xxxxlarge,
                    drawablePreview
                )
                accountStateTextView.setDrawable(start = drawable)
            }
        }
    }

    private fun setupAuthAccountDetails(state: ViewState.Content) {
        with(binding) {
            state.authAccountDisplayName?.let { displayName ->
                authAccountItemView.apply {
                    setTitleText(displayName.primaryDisplayName)
                    setDescriptionText(displayName.secondaryDisplayName)
                    setOnLongClickListener {
                        onCopyAccountAddressToClipboard(displayName.accountAddress)
                        true
                    }
                    visibility = if (state.isRekeyGroupVisible ?: false) View.VISIBLE else View.GONE
                }
            }

            state.authAccountIconDrawablePreview?.let { drawablePreview ->
                val drawable = AccountIconDrawable.create(
                    requireContext(),
                    R.dimen.spacing_xxxxlarge,
                    drawablePreview
                )
                authAccountItemView.setStartIconDrawable(drawable)
            }
        }
    }

    private fun setupDescriptionText(state: ViewState.Content) {
        with(binding) {
            state.descriptionDetail.let { descriptionDetail ->
                descriptionDetail.annotatedString.let { annotatedString ->
                    val linkTextColor = ContextCompat.getColor(root.context, R.color.link_primary)
                    val clickSpannable = getCustomClickableSpan(
                        clickableColor = linkTextColor,
                        onClick = {
                            context?.openUrl(descriptionDetail.hyperlinkUrl)
                        }
                    )
                    val clickableAnnotatedString = annotatedString.copy(
                        customAnnotationList = listOf("learn_more" to clickSpannable)
                    )
                    accountStateDescriptionTextView.text =
                        context?.getXmlStyledString(clickableAnnotatedString)
                }
            }
        }
    }

    private fun setupVisibility(state: ViewState.Content) {
        with(binding) {
            rekeyToLedgerAccountButton.isVisible = state.isRekeyToLedgerAccountVisible == true
            rekeyToStandardAccountButton.isVisible = state.isRekeyToStandardAccountVisible == true
        }
    }

    private fun setupButtons(state: ViewState.Content) {
        with(binding) {
            state.authAccountActionButton?.let { buttonState ->
                authAccountItemView.setButtonState(buttonState)
                authAccountItemView.setActionTextButtonClickListener { onNavigateToUndoRekey() }
            }
        }
    }

    private fun onNavigateToRekeyToLedgerAccount() {
        nav(
            AccountStatusDetailBottomSheetDirections
                .actionAccountStatusDetailBottomSheetToRekeyLedgerNavigation(viewModel.accountAddress)
        )
    }

    private fun onNavigateToRekeyToStandardAccount() {
        nav(
            AccountStatusDetailBottomSheetDirections
                .actionAccountStatusDetailBottomSheetToRekeyToStandardAccountNavigation(viewModel.accountAddress)
        )
    }

    private fun onNavigateToUndoRekey() {
        nav(
            AccountStatusDetailBottomSheetDirections
                .actionAccountStatusDetailBottomSheetToRekeyUndoNavigation(viewModel.accountAddress)
        )
    }

    private fun onNavigateToHdScanNewAddresses(accountCreation: AccountCreation) {
        nav(
            AccountStatusDetailBottomSheetDirections
                .actionAccountStatusDetailBottomSheetToRecoverRegisteredAccountsFragment(accountCreation)
        )
    }
}
