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

import androidx.lifecycle.ViewModel
import com.algorand.android.modules.addaccount.joint.creation.model.SelectedJointAccountItem
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateJointAccountViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>
) : ViewModel(),
    StateViewModel<CreateJointAccountViewModel.ViewState> by stateDelegate {

    init {
        stateDelegate.setDefaultState(ViewState())
    }

    fun addSelectedAccount(account: SelectedJointAccountItem): Boolean {
        val currentAccounts = state.value.selectedAccounts
        val addressExists = currentAccounts.any {
            it.accountDisplayName.accountAddress == account.accountDisplayName.accountAddress
        }
        if (addressExists) return false

        stateDelegate.updateState { currentState ->
            currentState.copy(selectedAccounts = currentState.selectedAccounts + account)
        }
        return true
    }

    fun updateAccountName(address: String, name: String) {
        if (name.isBlank()) return
        stateDelegate.updateState { currentState ->
            val updatedList = currentState.selectedAccounts.map { item ->
                if (item.accountDisplayName.accountAddress == address) {
                    val updatedDisplayName = item.accountDisplayName.copy(primaryDisplayName = name)
                    item.copy(accountDisplayName = updatedDisplayName)
                } else {
                    item
                }
            }
            currentState.copy(selectedAccounts = updatedList)
        }
    }

    fun removeSelectedAccount(address: String) {
        stateDelegate.updateState { currentState ->
            val updatedList = currentState.selectedAccounts.filterNot {
                it.accountDisplayName.accountAddress == address
            }
            currentState.copy(selectedAccounts = updatedList)
        }
    }

    fun getParticipantAddresses(): Array<String> = state.value.selectedAccounts
        .map { it.accountDisplayName.accountAddress }
        .toTypedArray()

    data class ViewState(
        val selectedAccounts: List<SelectedJointAccountItem> = emptyList()
    ) {
        val isContinueEnabled: Boolean
            get() = selectedAccounts.size >= MIN_PARTICIPANTS_COUNT
    }

    companion object {
        private const val MIN_PARTICIPANTS_COUNT = 2
    }
}
