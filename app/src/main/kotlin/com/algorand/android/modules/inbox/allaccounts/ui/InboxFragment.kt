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
class InboxFragment : TransactionSignBaseFragment(0), InboxScreenListener {

    private val infoButton by lazy { IconButton(R.drawable.ic_info, onClick = ::onInfoClick) }

    private val toolbarConfiguration = ToolbarConfiguration(
        titleResId = R.string.inbox,
        startIconClick = ::navBack,
        startIconResId = R.drawable.ic_left_arrow
    )

    override val fragmentConfiguration: FragmentConfiguration =
        FragmentConfiguration(toolbarConfiguration = toolbarConfiguration)

    private val inboxViewModel: InboxViewModel by viewModels()

    private val args: InboxFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return createComposeView {
            PeraTheme {
                InboxScreen(
                    viewModel = inboxViewModel,
                    listener = this@InboxFragment
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        initObservers()
        inboxViewModel.initializePreview(args.jointAccountAddressToOpen)
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
                handleJointAccountDeepLinkNavigation(preview)
            }
        )
    }

    private fun handleJointAccountDeepLinkNavigation(preview: InboxPreview) {
        preview.jointAccountInvitationToOpen?.consume()?.let { invitation ->
            navToJointAccountInvitationDetail(invitation)
            return
        }
        preview.jointAccountAddressToOpen?.consume()?.let { address ->
            nav(HomeNavigationDirections.actionGlobalToJointAccountDetailFragment(accountAddress = address))
        }
    }

    override fun onAccountClick(accountAddress: String) {
        navToAssetInboxOneAccountNavigation(AssetInboxOneAccountNavArgs(accountAddress))
    }

    override fun onInfoClick() {
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

    override fun onSignatureRequestClick(signRequestId: String, canUserSign: Boolean) {
        if (canUserSign) {
            nav(HomeNavigationDirections.actionGlobalToJointAccountSignRequestFragment(signRequestId))
        } else {
            PendingSignaturesDialogFragment.newInstance(signRequestId)
                .show(childFragmentManager, PendingSignaturesDialogFragment.TAG)
        }
    }

    override fun onJointAccountInvitationClick(invitation: JointAccountInvitationInboxItem) {
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
