/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 *
 */

package com.algorand.android.modules.inbox.allaccounts.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.algorand.android.HomeNavigationDirections
import com.algorand.android.R
import com.algorand.android.core.transaction.TransactionSignBaseFragment
import com.algorand.android.customviews.toolbar.buttoncontainer.model.IconButton
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.modules.addaccount.joint.transaction.ui.PendingSignaturesDialogFragment
import com.algorand.android.modules.assetinbox.assetinboxoneaccount.ui.model.AssetInboxOneAccountNavArgs
import com.algorand.android.modules.inbox.allaccounts.ui.model.InboxPreview
import com.algorand.android.modules.inbox.jointaccountinvitation.ui.model.JointAccountInvitationInboxItem
import com.algorand.android.ui.compose.extensions.createComposeView
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InboxFragment : TransactionSignBaseFragment(0) {

    private val infoButton by lazy { IconButton(R.drawable.ic_info, onClick = ::onInfoButtonClick) }

    private val toolbarConfiguration = ToolbarConfiguration(
        titleResId = R.string.inbox,
        startIconClick = ::navBack,
        startIconResId = R.drawable.ic_left_arrow
    )

    override val fragmentConfiguration: FragmentConfiguration =
        FragmentConfiguration(toolbarConfiguration = toolbarConfiguration)

    private val inboxViewModel: InboxViewModel by viewModels()

    private val args: InboxFragmentArgs by navArgs()

    private var isJointAccountImportHandled = false

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return createComposeView {
            PeraTheme {
                InboxScreen(
                    viewModel = inboxViewModel,
                    listener = object : InboxScreenListener {
                        override fun onAccountClick(accountAddress: String) {
                            onAccountClicked(accountAddress)
                        }

                        override fun onSignatureRequestClick(signRequestId: String, canUserSign: Boolean) {
                            onSignatureRequestClicked(signRequestId, canUserSign)
                        }

                        override fun onJointAccountInvitationClick(invitation: JointAccountInvitationInboxItem) {
                            onJointAccountInvitationClicked(invitation)
                        }

                        override fun onInfoClick() {
                            onInfoButtonClick()
                        }
                    }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        initObservers()
        inboxViewModel.initializePreview()
    }

    private fun setupToolbar() {
        getAppToolbar()?.run {
            setEndButton(button = infoButton)
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.collectLatestOnLifecycle(
            flow = inboxViewModel.viewStateFlow,
            collection = { preview ->
                preview.showError?.consume()?.let { error ->
                    context?.let { showGlobalError(error.parseError(it), tag = baseActivityTag) }
                }
                handleJointAccountImportDeepLinkIfNeeded(preview)
            }
        )
    }

    private fun handleJointAccountImportDeepLinkIfNeeded(preview: InboxPreview) {
        if (isJointAccountImportHandled) return

        val jointAccountAddressToOpen = args.jointAccountAddressToOpen ?: return
        if (preview.isLoading) return

        val invitation = preview.jointAccountInvitationList.firstOrNull {
            it.accountAddress == jointAccountAddressToOpen
        }

        isJointAccountImportHandled = true

        if (invitation != null) {
            navToJointAccountInvitationDetail(invitation)
        } else {
            // Navigate to joint account detail - will fetch invitation data from API
            nav(
                HomeNavigationDirections.actionGlobalToJointAccountDetailFragment(
                    accountAddress = jointAccountAddressToOpen
                )
            )
        }
    }

    private fun onAccountClicked(publicKey: String) {
        navToAssetInboxOneAccountNavigation(AssetInboxOneAccountNavArgs(publicKey))
    }

    private fun onInfoButtonClick() {
        navToAssetInboxInfoNavigation()
    }

    private fun navToAssetInboxInfoNavigation() {
        nav(
            InboxFragmentDirections
                .actionInboxFragmentToAssetInboxInfoNavigation()
        )
    }

    private fun navToAssetInboxOneAccountNavigation(assetInboxOneAccountNavArgs: AssetInboxOneAccountNavArgs) {
        nav(
            InboxFragmentDirections
                .actionInboxFragmentToAssetInboxOneAccountNavigation(
                    assetInboxOneAccountNavArgs
                )
        )
    }

    private fun onSignatureRequestClicked(signRequestId: String, canUserSign: Boolean) {
        if (canUserSign) {
            nav(HomeNavigationDirections.actionGlobalToJointAccountSignRequestFragment(signRequestId))
        } else {
            // Show pending signatures bottom sheet if user can't sign
            PendingSignaturesDialogFragment.newInstance(signRequestId)
                .show(childFragmentManager, PendingSignaturesDialogFragment.TAG)
        }
    }

    private fun onJointAccountInvitationClicked(invitation: JointAccountInvitationInboxItem) {
        navToJointAccountInvitationDetail(invitation)
    }

    private fun navToJointAccountInvitationDetail(invitation: JointAccountInvitationInboxItem) {
        nav(
            HomeNavigationDirections.actionGlobalToJointAccountDetailFragment(
                accountAddress = invitation.accountAddress,
                threshold = invitation.threshold,
                participantAddresses = invitation.participantAddresses.toTypedArray()
            )
        )
    }
}
