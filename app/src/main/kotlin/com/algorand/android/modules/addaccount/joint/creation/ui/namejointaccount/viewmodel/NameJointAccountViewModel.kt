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

package com.algorand.android.modules.addaccount.joint.creation.ui.namejointaccount.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.R
import com.algorand.android.modules.addaccount.joint.core.JointAccountConstants
import com.algorand.wallet.jointaccount.creation.domain.usecase.CreateJointAccount
import com.algorand.android.modules.addaccount.joint.creation.usecase.GetDefaultJointAccountName
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NameJointAccountViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val createJointAccount: CreateJointAccount,
    private val getDefaultJointAccountName: GetDefaultJointAccountName,
    private val processor: NameJointAccountProcessor
) : ViewModel(),
    StateViewModel<NameJointAccountViewModel.ViewState> by stateDelegate,
    EventViewModel<NameJointAccountViewModel.ViewEvent> by eventDelegate {

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    suspend fun getDefaultAccountName(): String = getDefaultJointAccountName()

    fun createJointAccount(accountName: String, threshold: Int, participantAddresses: List<String>) {
        val trimmedName = accountName.trim()
        if (!isValidAccountName(trimmedName)) {
            stateDelegate.updateState { ViewState.Error(R.string.an_error_occurred) }
            return
        }

        viewModelScope.launch {
            stateDelegate.updateState { ViewState.Loading }

            createJointAccount(
                participantAddresses = participantAddresses,
                threshold = threshold,
                version = JointAccountConstants.CURRENT_VERSION
            ).use(
                onSuccess = { jointAccountDTO ->
                    handleJointAccountCreationSuccess(
                        jointAccountAddress = jointAccountDTO.address,
                        participantAddresses = participantAddresses,
                        threshold = threshold,
                        version = jointAccountDTO.version ?: JointAccountConstants.CURRENT_VERSION,
                        accountName = trimmedName
                    )
                },
                onFailed = { exception, _ ->
                    val errorResId = processor.mapExceptionToErrorResId(exception)
                    stateDelegate.updateState { ViewState.Error(errorResId) }
                }
            )
        }
    }

    private fun isValidAccountName(name: String): Boolean {
        return name.isNotBlank()
    }

    private suspend fun handleJointAccountCreationSuccess(
        jointAccountAddress: String?,
        participantAddresses: List<String>,
        threshold: Int,
        version: Int,
        accountName: String
    ) {
        if (jointAccountAddress == null) {
            stateDelegate.updateState { ViewState.Error(R.string.an_error_occurred) }
            return
        }

        when (val result = processor.createLocalAccount(
            jointAccountAddress = jointAccountAddress,
            participantAddresses = participantAddresses,
            threshold = threshold,
            version = version,
            accountName = accountName
        )) {
            is NameJointAccountProcessor.CreateLocalAccountResult.Success -> {
                stateDelegate.updateState { ViewState.Success }
                eventDelegate.sendEvent(ViewEvent.AccountCreatedSuccessfully)
            }
            is NameJointAccountProcessor.CreateLocalAccountResult.AlreadyExists -> {
                stateDelegate.updateState { ViewState.Error(R.string.this_account_already_exists) }
            }
            is NameJointAccountProcessor.CreateLocalAccountResult.Error -> {
                stateDelegate.updateState { ViewState.Error(result.messageResId) }
            }
        }
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data object Loading : ViewState
        data object Success : ViewState
        data class Error(val messageResId: Int) : ViewState
    }

    sealed interface ViewEvent {
        data object AccountCreatedSuccessfully : ViewEvent
    }
}
