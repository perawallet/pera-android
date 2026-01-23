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

package com.algorand.android.modules.inbox.jointaccountinvitation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.algorand.android.HomeNavigationDirections
import com.algorand.android.R
import com.algorand.android.core.DaggerBaseFragment
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.modules.inbox.jointaccountinvitation.ui.model.JointAccountInvitationDetailNavArgs
import com.algorand.android.modules.inbox.jointaccountinvitation.ui.model.JointAccountInvitationInboxItem
import com.algorand.android.ui.compose.extensions.createComposeView
import com.algorand.android.ui.compose.theme.PeraTheme
import com.algorand.wallet.deviceregistration.domain.usecase.GetSelectedNodeDeviceId
import com.algorand.wallet.inbox.domain.repository.InboxApiRepository
import com.algorand.wallet.inbox.domain.usecase.RefreshInboxCache
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class JointAccountInvitationDetailFragment : DaggerBaseFragment(0),
    JointAccountInvitationDetailScreenListener {

    @Inject
    lateinit var getAccountDisplayName: GetAccountDisplayName

    @Inject
    lateinit var getAccountIconDrawablePreview: GetAccountIconDrawablePreview

    @Inject
    lateinit var getSelectedNodeDeviceId: GetSelectedNodeDeviceId

    @Inject
    @Named(InboxApiRepository.INJECTION_NAME)
    lateinit var inboxApiRepository: InboxApiRepository

    @Inject
    lateinit var refreshInboxCache: RefreshInboxCache

    private val args: JointAccountInvitationDetailFragmentArgs by navArgs()

    override val fragmentConfiguration = FragmentConfiguration()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val invitation = createInvitationFromArgs(args.invitationNavArgs)
        return createComposeView {
            var accountDisplayNames by remember {
                mutableStateOf<Map<String, AccountDisplayName>>(emptyMap())
            }
            var accountIcons by remember {
                mutableStateOf<Map<String, AccountIconDrawablePreview>>(emptyMap())
            }

            LaunchedEffect(Unit) {
                this@JointAccountInvitationDetailFragment.viewLifecycleOwner.lifecycleScope.launch {
                    val allAddresses = listOf(invitation.accountAddress) + invitation.participantAddresses
                    accountDisplayNames = allAddresses.associateWith { address ->
                        getAccountDisplayName(address)
                    }
                    accountIcons = allAddresses.associateWith { address ->
                        getAccountIconDrawablePreview(address)
                    }
                }
            }

            PeraTheme {
                JointAccountInvitationDetailScreen(
                    invitation = invitation,
                    accountDisplayNames = accountDisplayNames,
                    accountIcons = accountIcons,
                    listener = this@JointAccountInvitationDetailFragment
                )
            }
        }
    }

    private fun createInvitationFromArgs(
        navArgs: JointAccountInvitationDetailNavArgs
    ): JointAccountInvitationInboxItem {
        val creationTime = ZonedDateTime.now()
        return JointAccountInvitationInboxItem(
            id = "${navArgs.accountAddress}_${creationTime.toInstant().toEpochMilli()}",
            accountAddress = navArgs.accountAddress,
            accountAddressShortened = navArgs.accountAddressShortened,
            creationDateTime = creationTime,
            timeDifference = 0L,
            isRead = false,
            threshold = navArgs.threshold,
            participantAddresses = navArgs.participantAddresses
        )
    }

    override fun onBackClick() {
        navBack()
    }

    override fun onAcceptClick() {
        navToNameJointAccount(args.invitationNavArgs.threshold)
    }

    override fun onRejectClick() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val deviceId = getSelectedNodeDeviceId()?.toLongOrNull()
                if (deviceId != null) {
                    inboxApiRepository.deleteJointInvitationNotification(
                        deviceId,
                        args.invitationNavArgs.accountAddress
                    )
                }
                refreshInboxCache()
            } catch (e: Exception) {
                showGlobalError(getString(R.string.an_error_occurred))
            }
            navBack()
        }
    }

    override fun onCopyAddress(address: String) {
        onAccountAddressCopied(address)
    }

    private fun navToNameJointAccount(threshold: Int) {
        nav(
            HomeNavigationDirections.actionGlobalToNameJointAccountFragment(
                threshold = threshold,
                participantAddresses = args.invitationNavArgs.participantAddresses.toTypedArray()
            )
        )
    }
}
