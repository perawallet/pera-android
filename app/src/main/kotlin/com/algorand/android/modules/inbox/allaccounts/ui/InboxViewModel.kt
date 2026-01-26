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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.inbox.allaccounts.ui.model.InboxPreview
import com.algorand.android.modules.inbox.allaccounts.ui.usecase.InboxPreviewUseCase
import com.algorand.android.utils.Event
import com.algorand.android.utils.launchIO
import com.algorand.wallet.remoteconfig.domain.model.FeatureToggle
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class InboxViewModel @Inject constructor(
    private val inboxPreviewUseCase: InboxPreviewUseCase,
    private val isFeatureToggleEnabled: IsFeatureToggleEnabled,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val filterAccountAddress: String? = savedStateHandle[FILTER_ACCOUNT_ADDRESS_KEY]

    private val _viewStateFlow = MutableStateFlow(inboxPreviewUseCase.getInitialPreview())

    val viewStateFlow: StateFlow<InboxPreview> = _viewStateFlow.asStateFlow()

    private var refreshJob: Job? = null
    private var jointAccountAddressToOpen: String? = null
    private var isJointAccountImportHandled = false

    fun initializePreview(jointAccountAddress: String? = null) {
        jointAccountAddressToOpen = jointAccountAddress
        refreshJob?.cancel()
        refreshJob = viewModelScope.launchIO {
            inboxPreviewUseCase.setLastOpenedTime(ZonedDateTime.now())
            inboxPreviewUseCase.refreshInbox()
            fetchInboxPreview()
        }
    }

    private suspend fun fetchInboxPreview() {
        inboxPreviewUseCase.getInboxPreview(filterAccountAddress)
            .map { preview ->
                val isJointAccountEnabled = isFeatureToggleEnabled(FeatureToggle.JOINT_ACCOUNT.key)
                val filteredPreview = if (isJointAccountEnabled) {
                    preview
                } else {
                    preview.copy(
                        signatureRequestList = emptyList(),
                        jointAccountInvitationList = emptyList()
                    )
                }
                handleJointAccountDeepLinkIfNeeded(filteredPreview)
            }
            .distinctUntilChanged()
            .collectLatest { preview ->
                _viewStateFlow.value = preview
            }
    }

    private fun handleJointAccountDeepLinkIfNeeded(preview: InboxPreview): InboxPreview {
        if (isJointAccountImportHandled) return preview

        val addressToOpen = jointAccountAddressToOpen ?: return preview
        if (preview.isLoading) return preview

        isJointAccountImportHandled = true

        val invitation = preview.jointAccountInvitationList.firstOrNull {
            it.accountAddress == addressToOpen
        }

        return if (invitation != null) {
            preview.copy(jointAccountInvitationToOpen = Event(invitation))
        } else {
            preview.copy(jointAccountAddressToOpen = Event(addressToOpen))
        }
    }

    private companion object {
        const val FILTER_ACCOUNT_ADDRESS_KEY = "filterAccountAddress"
    }
}
