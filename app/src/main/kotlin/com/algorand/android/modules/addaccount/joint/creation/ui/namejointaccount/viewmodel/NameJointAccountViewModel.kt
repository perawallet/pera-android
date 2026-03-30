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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.android.R
import com.algorand.android.deviceregistration.domain.usecase.DeviceIdUseCase
import com.algorand.android.modules.addaccount.joint.core.JointAccountConstants
import com.algorand.android.modules.addaccount.joint.creation.domain.exception.JointAccountValidationException
import com.algorand.android.modules.addaccount.joint.creation.usecase.GetNextJointAccountNumber
import com.algorand.wallet.account.core.domain.usecase.AddJointAccount
import com.algorand.wallet.account.custom.domain.usecase.GetAllAccountOrderIndexes
import com.algorand.wallet.jointaccount.creation.domain.usecase.CreateJointAccount
import com.algorand.wallet.jointaccount.domain.usecase.GetJointAccount
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class NameJointAccountViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val createJointAccount: CreateJointAccount,
    private val getNextJointAccountNumber: GetNextJointAccountNumber,
    private val getAllAccountOrderIndexes: GetAllAccountOrderIndexes,
    private val addJointAccount: AddJointAccount,
    private val getJointAccount: GetJointAccount,
    private val inboxCleanup: NameJointAccountInboxCleanup,
    private val deviceIdUseCase: DeviceIdUseCase
) : ViewModel(),
    StateViewModel<NameJointAccountViewModel.ViewState> by stateDelegate,
    EventViewModel<NameJointAccountViewModel.ViewEvent> by eventDelegate {

    private val threshold: Int = savedStateHandle.get<Int>(THRESHOLD_KEY) ?: 0
    private val participantAddresses: List<String> =
        savedStateHandle.get<Array<String>>(PARTICIPANT_ADDRESSES_KEY)?.toList().orEmpty()

    init {
        stateDelegate.setDefaultState(ViewState.Idle())
        loadDefaultJointAccountNumber()
    }

    private fun loadDefaultJointAccountNumber() {
        viewModelScope.launch {
            val number = getNextJointAccountNumber()
            stateDelegate.updateState { currentState ->
                when (currentState) {
                    is ViewState.Idle -> currentState.copy(defaultJointAccountNumber = number)
                    else -> currentState
                }
            }
        }
    }

    fun onAccountNameChanged(accountName: String) {
        stateDelegate.updateState { currentState ->
            when (currentState) {
                is ViewState.Idle -> currentState.copy(accountName = accountName)
                is ViewState.Loading -> currentState.copy(accountName = accountName)
                else -> currentState
            }
        }
    }

    fun onFinishClick() {
        val currentState = state.value
        if (currentState is ViewState.Loading) return

        val accountName = (currentState as? ViewState.Idle)?.accountName ?: return
        val trimmedName = accountName.trim()
        if (!isValidAccountName(trimmedName)) {
            emitError(R.string.an_error_occurred)
            return
        }

        val deviceId = deviceIdUseCase.getSelectedNodeDeviceId()
        if (deviceId.isNullOrBlank()) {
            emitError(R.string.an_error_occurred)
            return
        }

        stateDelegate.updateState { ViewState.Loading(accountName = trimmedName) }
        viewModelScope.launch {
            createJointAccount(
                participantAddresses = participantAddresses,
                threshold = threshold,
                version = JointAccountConstants.CURRENT_VERSION,
                deviceId = deviceId
            ).use(
                onSuccess = { jointAccountDTO ->
                    handleJointAccountCreationSuccess(
                        jointAccountAddress = jointAccountDTO.address,
                        version = jointAccountDTO.version ?: JointAccountConstants.CURRENT_VERSION,
                        accountName = trimmedName
                    )
                },
                onFailed = { exception, _ ->
                    val errorResId = mapExceptionToErrorResId(exception)
                    revertToIdle()
                    emitError(errorResId)
                }
            )
        }
    }

    private fun mapExceptionToErrorResId(exception: Throwable?): Int {
        return when (exception) {
            is JointAccountValidationException -> R.string.joint_account_validation_insufficient_participants
            is IOException -> R.string.the_internet_connection
            else -> R.string.an_error_occurred
        }
    }

    private fun isValidAccountName(name: String): Boolean {
        return name.isNotBlank()
    }

    private suspend fun handleJointAccountCreationSuccess(
        jointAccountAddress: String?,
        version: Int,
        accountName: String
    ) {
        if (jointAccountAddress == null) {
            revertToIdle()
            emitError(R.string.an_error_occurred)
            return
        }

        if (isAccountAlreadyExists(jointAccountAddress)) {
            deleteInboxNotification(jointAccountAddress)
            revertToIdle()
            emitError(R.string.this_account_already_exists)
            return
        }

        saveJointAccount(jointAccountAddress, version, accountName)
    }

    private suspend fun saveJointAccount(
        jointAccountAddress: String,
        version: Int,
        accountName: String
    ) {
        val result = addJointAccount(
            address = jointAccountAddress,
            participantAddresses = participantAddresses,
            threshold = threshold,
            version = version,
            customName = accountName.takeIf { it.isNotBlank() },
            orderIndex = calculateNextOrderIndex()
        )

        if (result.isSuccess) {
            deleteInboxNotification(jointAccountAddress)
            stateDelegate.updateState { ViewState.Success }
            eventDelegate.sendEvent(ViewEvent.AccountCreatedSuccessfully)
        } else {
            revertToIdle()
            emitError(R.string.an_error_occurred)
        }
    }

    private suspend fun isAccountAlreadyExists(address: String): Boolean {
        return getJointAccount(address) != null
    }

    private suspend fun calculateNextOrderIndex(): Int {
        val orderIndexes = getAllAccountOrderIndexes()
        return if (orderIndexes.isEmpty()) 0 else (orderIndexes.maxOfOrNull { it.index } ?: -1) + 1
    }

    private suspend fun deleteInboxNotification(jointAccountAddress: String) {
        try {
            val deviceId = inboxCleanup.getDeviceConfig().deviceId.toLongOrNull() ?: return
            inboxCleanup.deleteInboxJointInvitationNotification(deviceId, jointAccountAddress)
        } catch (_: Exception) {
            // Best-effort cleanup; inbox notification removal is non-critical
        }
    }

    private fun revertToIdle() {
        stateDelegate.updateState { current ->
            val accountName = (current as? ViewState.Loading)?.accountName.orEmpty()
            ViewState.Idle(accountName = accountName)
        }
    }

    private fun emitError(errorResId: Int) {
        eventDelegate.sendEvent(viewModelScope, ViewEvent.ShowError(errorResId))
    }

    sealed interface ViewState {
        data class Idle(
            val defaultJointAccountNumber: Int? = null,
            val accountName: String = ""
        ) : ViewState

        data class Loading(val accountName: String = "") : ViewState
        data object Success : ViewState
    }

    sealed interface ViewEvent {
        data object AccountCreatedSuccessfully : ViewEvent
        data class ShowError(val messageResId: Int) : ViewEvent
    }

    companion object {
        private const val THRESHOLD_KEY = "threshold"
        private const val PARTICIPANT_ADDRESSES_KEY = "participantAddresses"
    }
}
