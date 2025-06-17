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

package com.algorand.android.ui.onboarding.recoverypassphrase.importregisteredaddresses.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.models.AccountCreation
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreviewByType
import com.algorand.android.ui.onboarding.creation.mapper.AccountCreationHdKeyTypeMapper
import com.algorand.android.ui.onboarding.recoverypassphrase.importregisteredaddresses.model.RegisteredHdKeyItem
import com.algorand.android.ui.onboarding.recoverypassphrase.importregisteredaddresses.viewmodel.RecoverRegisteredAccountsViewModel.ViewEvent
import com.algorand.android.ui.onboarding.recoverypassphrase.importregisteredaddresses.viewmodel.RecoverRegisteredAccountsViewModel.ViewState
import com.algorand.android.ui.onboarding.recoverypassphrase.importregisteredaddresses.viewmodel.RecoverRegisteredAccountsViewModel.ViewState.Content.ContentType
import com.algorand.android.ui.onboarding.recoverypassphrase.importregisteredaddresses.viewmodel.RecoverRegisteredAccountsViewModel.ViewState.Content.ContentType.LoadingRekeyedAddresses
import com.algorand.android.ui.rekeyedaccounts.model.RekeyedAccountSelectionNavArg
import com.algorand.android.usecase.AccountAdditionUseCase
import com.algorand.android.utils.analytics.CreationType
import com.algorand.android.utils.getOrThrow
import com.algorand.android.utils.launchIO
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.info.domain.usecase.FetchRekeyedAddresses
import com.algorand.wallet.algosdk.bip39.model.HdKeyAddress
import com.algorand.wallet.algosdk.bip39.model.HdKeyAddressIndex
import com.algorand.wallet.algosdk.bip39.sdk.Bip39Wallet
import com.algorand.wallet.algosdk.bip39.sdk.Bip39WalletProvider
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import com.algorand.wallet.encryption.domain.manager.Base64Manager
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
    private val registeredAccountsProcessor: RecoverRegisteredAccountsAccountProcessor,
    private val fetchRekeyedAddresses: FetchRekeyedAddresses,
    private val getAccountIconDrawablePreviewByType: GetAccountIconDrawablePreviewByType,
    private val accountCreationHdKeyTypeMapper: AccountCreationHdKeyTypeMapper,
    private val base64Manager: Base64Manager,
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>
) : BaseViewModel(), StateViewModel<ViewState> by stateDelegate, EventViewModel<ViewEvent> by eventDelegate {

    private val encryptedEntropy: ByteArray = savedStateHandle.getOrThrow<String>("encryptedEntropyBase64").let {
        base64Manager.decode(it)
    }

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun loadRegisteredAccounts() {
        stateDelegate.onState<ViewState.Idle> {
            stateDelegate.updateState { ViewState.Loading }
            viewModelScope.launchIO {
                val registeredAccounts = registeredAccountsProcessor.getRegisteredHdKeyItems(encryptedEntropy)
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
                val entropy = aesPlatformManager.decryptByteArray(encryptedEntropy)
                val addressesToImport = getAddressesToImport(entropy, selectedAddresses)
                addSelectedAddresses(entropy, addressesToImport)
                val rekeyedAddresses = fetchRekeyedAddresses(selectedAddresses)
                stateDelegate.updateState { currentState.copy(type = ContentType.Idle) }
                if (rekeyedAddresses.isNotEmpty()) {
                    eventDelegate.sendEvent(ViewEvent.NavigateToRekeyedAccountSelection(rekeyedAddresses))
                } else {
                    if (addressesToImport.size == 1) {
                        eventDelegate.sendEvent(ViewEvent.NavigateToAddressNaming(addressesToImport.single().address))
                    } else {
                        val isNewAccountAdded = addressesToImport.isNotEmpty()
                        eventDelegate.sendEvent(ViewEvent.NavigateToHome(isNewAccountAdded))
                    }
                }
                entropy.clearFromMemory()
            }
        }
    }

    private suspend fun addSelectedAddresses(entropy: ByteArray, selectedAddresses: List<HdKeyAddress>) {
        selectedAddresses.forEach { hdKeyAccount ->
            val newAccountCreation = createAccountCreation(entropy, hdKeyAccount)
            accountAdditionUseCase.addNewAccount(newAccountCreation)
        }
    }

    private fun getAddressesToImport(
        entropy: ByteArray,
        selectedAddresses: List<RegisteredHdKeyItem>
    ): List<HdKeyAddress> {
        val wallet = bip39WalletProvider.getBip39Wallet(entropy.copyOf())
        return selectedAddresses.map { accountItem ->
            createHdKeyAddress(wallet, accountItem)
        }.also {
            wallet.invalidate()
        }
    }

    private suspend fun fetchRekeyedAddresses(
        selectedAddresses: List<RegisteredHdKeyItem>
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

    private fun createHdKeyAddress(bip39Wallet: Bip39Wallet, accountItem: RegisteredHdKeyItem): HdKeyAddress {
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
            val registeredAccounts: List<RegisteredHdKeyItem> = emptyList(),
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
        data class NavigateToHome(val isNewAccountAdded: Boolean) : ViewEvent
        data class NavigateToAddressNaming(val address: String) : ViewEvent
        data object NavigateBack : ViewEvent
        data class NavigateToRekeyedAccountSelection(val args: List<RekeyedAccountSelectionNavArg>) : ViewEvent
    }
}
