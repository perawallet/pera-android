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

package com.algorand.android.modules.addaccount.joint.creation.ui.createaccount.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.modules.addaccount.joint.creation.domain.exception.JointAccountValidationException
import com.algorand.android.modules.addaccount.joint.creation.model.SelectedJointAccountItem
import com.algorand.android.repository.ContactRepository
import com.algorand.wallet.foundation.cache.PersistentCache
import com.algorand.wallet.foundation.cache.PersistentCacheProvider
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateJointAccountViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val contactRepository: ContactRepository,
    persistentCacheProvider: PersistentCacheProvider,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(),
    StateViewModel<CreateJointAccountViewModel.ViewState> by stateDelegate,
    EventViewModel<CreateJointAccountViewModel.ViewEvent> by eventDelegate {

    private val disclaimerSeenCache: PersistentCache<Boolean> =
        persistentCacheProvider.getPersistentCache(
            Boolean::class.java,
            DISCLAIMER_SEEN_KEY
        )

    private var editingAccountIndex: Int?
        get() = savedStateHandle.get<Int>(EDITING_ACCOUNT_INDEX_KEY)
        set(value) {
            if (value != null) {
                savedStateHandle[EDITING_ACCOUNT_INDEX_KEY] = value
            } else {
                savedStateHandle.remove<Int>(EDITING_ACCOUNT_INDEX_KEY)
                Unit
            }
        }

    init {
        stateDelegate.setDefaultState(ViewState.Content())
    }

    fun addSelectedAccount(account: SelectedJointAccountItem) {
        stateDelegate.updateState { currentState ->
            val content = currentState as? ViewState.Content ?: return@updateState currentState
            content.copy(selectedAccounts = content.selectedAccounts + account)
        }
    }

    fun setEditingAccountIndex(index: Int) {
        editingAccountIndex = index
    }

    fun updateAccountNameFromResult(name: String) {
        val index = editingAccountIndex ?: return
        if (name.isBlank()) return
        var addressToUpdate: String? = null
        stateDelegate.updateState { currentState ->
            val content = currentState as? ViewState.Content ?: return@updateState currentState
            if (index !in content.selectedAccounts.indices) return@updateState currentState
            val updatedList = content.selectedAccounts.toMutableList()
            val item = updatedList[index]
            if (item.isContact) {
                addressToUpdate = item.accountDisplayName.accountAddress
            }
            val updatedDisplayName = item.accountDisplayName.copy(primaryDisplayName = name)
            updatedList[index] = item.copy(accountDisplayName = updatedDisplayName)
            content.copy(selectedAccounts = updatedList)
        }
        addressToUpdate?.let { address ->
            viewModelScope.launch {
                val contact = contactRepository.getContactByAddress(address) ?: return@launch
                contactRepository.updateContact(contact.copy(name = name))
            }
        }
        editingAccountIndex = null
    }

    fun removeEditingAccount() {
        val index = editingAccountIndex ?: return
        removeSelectedAccount(index)
        editingAccountIndex = null
    }

    fun removeSelectedAccount(index: Int) {
        stateDelegate.updateState { currentState ->
            val content = currentState as? ViewState.Content ?: return@updateState currentState
            val updatedList = content.selectedAccounts.toMutableList().apply {
                removeAt(index)
            }
            content.copy(selectedAccounts = updatedList)
        }
    }

    fun onContinueClick() {
        val hasSeenDisclaimer = disclaimerSeenCache.get() == true
        if (hasSeenDisclaimer) {
            eventDelegate.sendEvent(viewModelScope, ViewEvent.NavigateToSetThreshold)
        } else {
            stateDelegate.updateState { currentState ->
                val content = currentState as? ViewState.Content ?: return@updateState currentState
                content.copy(showDisclaimer = true)
            }
        }
    }

    fun onDisclaimerProceed() {
        disclaimerSeenCache.put(true)
        stateDelegate.updateState { currentState ->
            val content = currentState as? ViewState.Content ?: return@updateState currentState
            content.copy(showDisclaimer = false)
        }
        eventDelegate.sendEvent(viewModelScope, ViewEvent.NavigateToSetThreshold)
    }

    fun onDisclaimerGoBack() {
        stateDelegate.updateState { currentState ->
            val content = currentState as? ViewState.Content ?: return@updateState currentState
            content.copy(showDisclaimer = false)
        }
    }

    fun getParticipantAddresses(): Array<String> {
        val content = state.value as? ViewState.Content ?: return emptyArray()
        return content.selectedAccounts.map {
            it.accountDisplayName.accountAddress
        }.toTypedArray()
    }

    sealed interface ViewState {
        data class Content(
            val selectedAccounts: List<SelectedJointAccountItem> = emptyList(),
            val showDisclaimer: Boolean = false
        ) : ViewState {
            val isContinueEnabled: Boolean
                get() = selectedAccounts.size >= JointAccountValidationException.MIN_PARTICIPANTS
        }
    }

    sealed interface ViewEvent {
        data object NavigateToSetThreshold : ViewEvent
    }

    companion object {
        private const val DISCLAIMER_SEEN_KEY = "joint_account_disclaimer_seen"
        private const val EDITING_ACCOUNT_INDEX_KEY = "editingAccountIndex"
    }
}
