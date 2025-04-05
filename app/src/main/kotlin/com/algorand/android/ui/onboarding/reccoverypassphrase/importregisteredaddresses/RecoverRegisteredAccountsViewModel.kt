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

package com.algorand.android.ui.onboarding.reccoverypassphrase.importregisteredaddresses

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.models.AccountCreation
import com.algorand.android.ui.onboarding.reccoverypassphrase.importregisteredaddresses.RecoverRegisteredAccountsViewModel.ViewEvent
import com.algorand.android.ui.onboarding.reccoverypassphrase.importregisteredaddresses.RecoverRegisteredAccountsViewModel.ViewState
import com.algorand.android.usecase.AccountAdditionUseCase
import com.algorand.android.utils.analytics.CreationType
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.account.core.domain.model.CreateAccount.Type
import com.algorand.wallet.account.info.domain.model.RegisteredHdKey
import com.algorand.wallet.account.info.domain.usecase.GetRegisteredHdKeys
import com.algorand.wallet.algosdk.transaction.sdk.PeraBip39Sdk
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import com.algorand.wallet.encryption.domain.utils.clearFromMemory
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class RecoverRegisteredAccountsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val aesPlatformManager: AESPlatformManager,
    private val bip39Sdk: PeraBip39Sdk,
    private val accountAdditionUseCase: AccountAdditionUseCase,
    private val getRegisteredHdKeys: GetRegisteredHdKeys,
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>
) : BaseViewModel(), StateViewModel<ViewState> by stateDelegate, EventViewModel<ViewEvent> by eventDelegate {

    private val accountCreation: AccountCreation = savedStateHandle["accountCreation"]
        ?: error("Missing accountCreation argument")

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun loadRegisteredAccounts() {
        stateDelegate.updateState { ViewState.Loading }
        viewModelScope.launch {
            val hdKey = accountCreation.toCreateAccount().type as? Type.HdKey
            if (hdKey == null) {
                stateDelegate.updateState { ViewState.Content(registeredAccounts = emptyList()) }
                return@launch
            }

            val entropy = aesPlatformManager.decryptByteArray(hdKey.encryptedEntropy)
            val registeredAccounts = getRegisteredHdKeys(entropy.copyOf())
            entropy.clearFromMemory()
            val notImportedAddresses = registeredAccounts.mapNotNull {
                it.takeIf { !it.isImportedToDB }?.address
            }.toSet()
            stateDelegate.updateState {
                ViewState.Content(
                    registeredAccounts = registeredAccounts,
                    registeredAddressesNotImported = notImportedAddresses
                )
            }
        }
    }

    fun toggleAccountSelection(address: String, isSelected: Boolean) {
        stateDelegate.onState<ViewState.Content> { currentState ->
            val updatedSelection = if (isSelected) {
                currentState.selectedAddresses + address
            } else {
                currentState.selectedAddresses - address
            }
            stateDelegate.updateState {
                currentState.copy(selectedAddresses = updatedSelection)
            }
        }
    }

    fun selectAllAccounts() {
        stateDelegate.onState<ViewState.Content> { currentState ->
            stateDelegate.updateState {
                currentState.copy(
                    selectedAddresses = currentState.registeredAddressesNotImported
                )
            }
        }
    }

    fun unselectAllAccounts() {
        stateDelegate.onState<ViewState.Content> { currentState ->
            stateDelegate.updateState {
                currentState.copy(selectedAddresses = emptySet())
            }
        }
    }

    fun importSelectedAccounts() {
        stateDelegate.onState<ViewState.Content> { currentState ->
            stateDelegate.updateState { ViewState.Loading }
            viewModelScope.launch(Dispatchers.IO) {

                try {
                    val encryptedEntropy =
                        (accountCreation.type as AccountCreation.Type.HdKey).encryptedEntropy
                    val entropy = aesPlatformManager.decryptByteArray(encryptedEntropy)

                    currentState.registeredAccounts
                        .filter { currentState.selectedAddresses.contains(it.address) }
                        .forEach { accountItem ->
                            bip39Sdk.getHdKeyAccount(
                                entropy.copyOf(),
                                accountItem.account,
                                accountItem.change,
                                accountItem.keyIndex
                            )?.let { account ->
                                val newAccountCreation = AccountCreation(
                                    address = account.address,
                                    customName = account.address.toShortenedAddress(),
                                    isBackedUp = false,
                                    type = AccountCreation.Type.HdKey(
                                        account.publicKey,
                                        aesPlatformManager.encryptByteArray(account.privateKey),
                                        aesPlatformManager.encryptByteArray(account.entropy),
                                        account.account,
                                        account.change,
                                        account.keyIndex,
                                        account.derivationType,
                                    ),
                                    creationType = CreationType.RECOVER
                                )
                                accountAdditionUseCase.addNewAccount(newAccountCreation)
                            }
                        }

                    entropy.clearFromMemory()
                    eventDelegate.sendEvent(ViewEvent.NavigateToHome)
                } catch (e: Exception) {
                    stateDelegate.updateState {
                        ViewState.Error(e.message ?: "Unknown error")
                    }
                }
            }
        }
    }

    fun triggerEvent(event: ViewEvent) {
        viewModelScope.launch {
            eventDelegate.sendEvent(event)
        }
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data object Loading : ViewState
        data class Content(
            val registeredAccounts: List<RegisteredHdKey> = emptyList(),
            val registeredAddressesNotImported: Set<String> = emptySet(),
            val selectedAddresses: Set<String> = emptySet()
        ) : ViewState

        data class Error(val message: String) : ViewState
    }

    sealed interface ViewEvent {
        data object NavigateToHome : ViewEvent
        data object NavigateBack : ViewEvent
    }
}
