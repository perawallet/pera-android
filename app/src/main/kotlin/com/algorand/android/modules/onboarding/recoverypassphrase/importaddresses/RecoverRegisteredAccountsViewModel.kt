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

package com.algorand.android.modules.onboarding.recoverypassphrase.importaddresses

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.models.AccountCreation
import com.algorand.android.usecase.AccountAdditionUseCase
import com.algorand.android.utils.analytics.CreationType
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.account.core.domain.model.CreateAccount.Type
import com.algorand.wallet.algosdk.transaction.sdk.PeraBip39Sdk
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class RecoverRegisteredAccountsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val aesPlatformManager: AESPlatformManager,
    private val bip39Sdk: PeraBip39Sdk,
    private val accountAdditionUseCase: AccountAdditionUseCase
) : BaseViewModel() {

    private val accountCreation: AccountCreation = savedStateHandle["accountCreation"]
        ?: error("Missing accountCreation argument")

    private val _state = MutableStateFlow(RecoverRegisteredAccountsState())
    val state: StateFlow<RecoverRegisteredAccountsState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<RecoverRegisteredAccountsEffect>()
    val effect: SharedFlow<RecoverRegisteredAccountsEffect> = _effect.asSharedFlow()

    init {
        processIntent(RecoverRegisteredAccountsIntent.LoadRegisteredAccounts)
    }

    fun processIntent(intent: RecoverRegisteredAccountsIntent) {
        when (intent) {
            is RecoverRegisteredAccountsIntent.LoadRegisteredAccounts -> loadRegisteredAccounts()
            is RecoverRegisteredAccountsIntent.ToggleAccountSelection -> toggleAccountSelection(
                intent.address,
                intent.isSelected
            )
            is RecoverRegisteredAccountsIntent.SelectAllAccounts -> selectAllAccounts()
            is RecoverRegisteredAccountsIntent.UnselectAllAccounts -> unselectAllAccounts()
            is RecoverRegisteredAccountsIntent.ImportSelectedAccounts -> importSelectedAccounts()
            is RecoverRegisteredAccountsIntent.NavigateToHome -> navigateToHome()
        }
    }

    private fun loadRegisteredAccounts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                with(accountCreation.toCreateAccount().type) {
                    when (this) {
                        is Type.HdKey -> {
                            var entropy = aesPlatformManager.decryptByteArray(this.encryptedEntropy)
                            val registeredAccounts = bip39Sdk.fetchRegisteredAccounts(entropy)
                            entropy = ByteArray(0) // clear secret from memory
                            _state.update {
                                it.copy(
                                    registeredAccounts = registeredAccounts,
                                    isLoading = false
                                )
                            }
                        }
                        else -> {
                            _state.update {
                                it.copy(
                                    registeredAccounts = emptyList(),
                                    isLoading = false
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun toggleAccountSelection(address: String, isSelected: Boolean) {
        _state.update { currentState ->
            val updatedSelection = if (isSelected) {
                currentState.selectedAddresses + address
            } else {
                currentState.selectedAddresses - address
            }
            currentState.copy(selectedAddresses = updatedSelection)
        }
    }

    private fun selectAllAccounts() {
        _state.update { currentState ->
            val registeredAddressesNotImported = currentState.registeredAccounts
                .filter { !it.isImportedToDB }
                .map { it.address }
                .toSet()

            val newSelectedAddresses = if (registeredAddressesNotImported.all {
                currentState.selectedAddresses.contains(it)
            }) {
                currentState.selectedAddresses
            } else {
                registeredAddressesNotImported
            }

            currentState.copy(
                selectedAddresses = newSelectedAddresses,
                registeredAddressesNotImported = registeredAddressesNotImported
            )
        }
    }

    private fun unselectAllAccounts() {
        _state.update { it.copy(selectedAddresses = emptySet()) }
    }

    private fun importSelectedAccounts() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isLoading = true) }

            try {
                val encryptedEntropy = (accountCreation.type as AccountCreation.Type.HdKey).encryptedEntropy
                var entropy = aesPlatformManager.decryptByteArray(encryptedEntropy)
                val currentState = _state.value

                currentState.registeredAccounts
                    .filter { currentState.selectedAddresses.contains(it.address) }
                    .forEach { accountItem ->
                        bip39Sdk.getHdKeyAccount(
                            entropy,
                            accountItem.account,
                            accountItem.change,
                            accountItem.keyIndex
                        )?.let { account ->
                            val accountCreation = AccountCreation(
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
                            accountAdditionUseCase.addNewAccount(accountCreation)
                        }
                    }

                entropy = ByteArray(0) // clear secret from memory

                _state.update { it.copy(isImportDone = true, isLoading = false) }
                _effect.emit(RecoverRegisteredAccountsEffect.NavigateToHome)
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun navigateToHome() {
        viewModelScope.launch {
            _effect.emit(RecoverRegisteredAccountsEffect.NavigateToHome)
        }
    }
}
