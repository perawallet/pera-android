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

package com.algorand.android.ui.onboarding.recoverypassphrase.importregisteredaddresses

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.models.AccountCreation
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreviewByType
import com.algorand.android.ui.onboarding.creation.mapper.AccountCreationHdKeyTypeMapper
import com.algorand.android.ui.onboarding.recoverypassphrase.importregisteredaddresses.RecoverRegisteredAccountsViewModel.ViewEvent
import com.algorand.android.ui.onboarding.recoverypassphrase.importregisteredaddresses.RecoverRegisteredAccountsViewModel.ViewState
import com.algorand.android.ui.onboarding.recoverypassphrase.importregisteredaddresses.RecoverRegisteredAccountsViewModel.ViewState.Content.ContentType
import com.algorand.android.ui.onboarding.recoverypassphrase.importregisteredaddresses.RecoverRegisteredAccountsViewModel.ViewState.Content.ContentType.LoadingRekeyedAddresses
import com.algorand.android.ui.rekeyedaccounts.model.RekeyedAccountSelectionNavArg
import com.algorand.android.usecase.AccountAdditionUseCase
import com.algorand.android.utils.analytics.CreationType
import com.algorand.android.utils.launchIO
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.account.core.domain.model.CreateAccount.Type
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.info.domain.model.RegisteredHdKey
import com.algorand.wallet.account.info.domain.usecase.FetchRekeyedAddresses
import com.algorand.wallet.account.info.domain.usecase.GetRegisteredHdKeys
import com.algorand.wallet.algosdk.bip39.model.HdKeyAddress
import com.algorand.wallet.algosdk.bip39.model.HdKeyAddressIndex
import com.algorand.wallet.algosdk.bip39.sdk.Bip39Wallet
import com.algorand.wallet.algosdk.bip39.sdk.Bip39WalletProvider
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import com.algorand.wallet.encryption.domain.utils.clearFromMemory
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

@Suppress("LongParameterList")
@HiltViewModel
class RecoverRegisteredAccountsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val aesPlatformManager: AESPlatformManager,
    private val bip39WalletProvider: Bip39WalletProvider,
    private val accountAdditionUseCase: AccountAdditionUseCase,
    private val getRegisteredHdKeys: GetRegisteredHdKeys,
    private val fetchRekeyedAddresses: FetchRekeyedAddresses,
    private val getAccountIconDrawablePreviewByType: GetAccountIconDrawablePreviewByType,
    private val accountCreationHdKeyTypeMapper: AccountCreationHdKeyTypeMapper,
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>
) : BaseViewModel(), StateViewModel<ViewState> by stateDelegate, EventViewModel<ViewEvent> by eventDelegate {

    private val accountCreation: AccountCreation = savedStateHandle["accountCreation"]
        ?: error("Missing accountCreation argument")

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun loadRegisteredAccounts() {
        stateDelegate.onState<ViewState.Idle> {
            stateDelegate.updateState { ViewState.Loading }
            viewModelScope.launchIO {
                val hdKey = accountCreation.toCreateAccount().type as? Type.HdKey
                if (hdKey == null) {
                    stateDelegate.updateState { ViewState.Content(registeredAccounts = emptyList()) }
                    return@launchIO
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
            stateDelegate.updateState { currentState.copy(type = LoadingRekeyedAddresses) }
            viewModelScope.launch(Dispatchers.IO) {
                val selectedAddresses = currentState.registeredAccounts.filter {
                    currentState.selectedAddresses.contains(it.address)
                }
                addSelectedAddresses(selectedAddresses)
                val rekeyedAddresses = fetchRekeyedAddresses(selectedAddresses)
                stateDelegate.updateState { currentState.copy(type = ContentType.Idle) }
                if (rekeyedAddresses.isNotEmpty()) {
                    eventDelegate.sendEvent(ViewEvent.NavigateToRekeyedAccountSelection(rekeyedAddresses))
                } else {
                    eventDelegate.sendEvent(ViewEvent.NavigateToHome)
                }
            }
        }
    }

    private suspend fun addSelectedAddresses(selectedAddresses: List<RegisteredHdKey>) {
        val encryptedEntropy = (accountCreation.type as AccountCreation.Type.HdKey).encryptedEntropy
        val entropy = aesPlatformManager.decryptByteArray(encryptedEntropy)
        val wallet = bip39WalletProvider.getBip39Wallet(entropy.copyOf())
        selectedAddresses.forEach { accountItem ->
            val hdKeyAccount = createHdKeyAddress(wallet, accountItem)
            val newAccountCreation = createAccountCreation(entropy, hdKeyAccount)
            accountAdditionUseCase.addNewAccount(newAccountCreation)
        }
        entropy.clearFromMemory()
        wallet.invalidate()
    }

    private suspend fun fetchRekeyedAddresses(
        selectedAddresses: List<RegisteredHdKey>
    ): List<RekeyedAccountSelectionNavArg> {
        return supervisorScope {
            val deferredFetchRekeyedAccounts = selectedAddresses.map {
                async {
                    val notImportedRekeyedAddresses = fetchRekeyedAddresses(it.address)
                        .getDataOrNull()
                        ?.notImportedAddresses
                        .orEmpty()
                    it.address to notImportedRekeyedAddresses
                }
            }
            deferredFetchRekeyedAccounts
                .awaitAll()
                .mapNotNull { (authAddress, rekeyedAddresses) ->
                    getRekeyedAccountSelectionNavArg(authAddress, rekeyedAddresses)
                }
        }
    }

    private fun getRekeyedAccountSelectionNavArg(
        authAddress: String,
        rekeyedAddresses: List<String>
    ): RekeyedAccountSelectionNavArg? {
        if (rekeyedAddresses.isEmpty()) return null
        return RekeyedAccountSelectionNavArg(
            authAddress = authAddress,
            authAddressIconDrawablePreview = getAccountIconDrawablePreviewByType(AccountType.HdKey),
            rekeyedAccountAddresses = rekeyedAddresses
        )
    }

    private fun createHdKeyAddress(bip39Wallet: Bip39Wallet, accountItem: RegisteredHdKey): HdKeyAddress {
        return with(accountItem) {
            val index = HdKeyAddressIndex(accountIndex = account, changeIndex = change, keyIndex = keyIndex)
            bip39Wallet.generateAddress(index)
        }
    }

    private fun createAccountCreation(entropy: ByteArray, hdKeyAddress: HdKeyAddress): AccountCreation {
        return with(hdKeyAddress) {
            val hdKeyType = accountCreationHdKeyTypeMapper(entropy, hdKeyAddress, seedId = null)
            AccountCreation(
                address = address,
                customName = address.toShortenedAddress(),
                isBackedUp = false,
                type = hdKeyType,
                creationType = CreationType.RECOVER
            )
        }
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data object Loading : ViewState

        data class Content(
            val registeredAccounts: List<RegisteredHdKey> = emptyList(),
            val registeredAddressesNotImported: Set<String> = emptySet(),
            val selectedAddresses: Set<String> = emptySet(),
            val type: ContentType = ContentType.Idle
        ) : ViewState {

            sealed interface ContentType {
                data object Idle : ContentType
                data object LoadingRekeyedAddresses : ContentType
            }
        }
    }

    sealed interface ViewEvent {
        data object NavigateToHome : ViewEvent
        data object NavigateBack : ViewEvent
        data class NavigateToRekeyedAccountSelection(val args: List<RekeyedAccountSelectionNavArg>) : ViewEvent
    }
}
