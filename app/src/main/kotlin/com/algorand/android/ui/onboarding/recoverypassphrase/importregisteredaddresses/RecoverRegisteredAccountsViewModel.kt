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
import com.algorand.android.ui.onboarding.recoverypassphrase.importregisteredaddresses.RecoverRegisteredAccountsViewModel.ViewEvent
import com.algorand.android.ui.onboarding.recoverypassphrase.importregisteredaddresses.RecoverRegisteredAccountsViewModel.ViewState
import com.algorand.android.ui.onboarding.recoverypassphrase.importregisteredaddresses.RecoverRegisteredAccountsViewModel.ViewState.Content.ContentType
import com.algorand.android.ui.onboarding.recoverypassphrase.importregisteredaddresses.RecoverRegisteredAccountsViewModel.ViewState.Content.ContentType.LoadingRekeyedAddresses
import com.algorand.android.ui.rekeyedaccounts.model.RekeyedAccountSelectionNavArg
import com.algorand.android.usecase.AccountAdditionUseCase
import com.algorand.android.utils.analytics.CreationType
import com.algorand.android.utils.getOrThrow
import com.algorand.android.utils.launchIO
import com.algorand.android.utils.toShortenedAddress
import com.algorand.wallet.account.detail.domain.model.AccountType
import com.algorand.wallet.account.info.domain.model.RegisteredHdKey
import com.algorand.wallet.account.info.domain.usecase.FetchRekeyedAddresses
import com.algorand.wallet.account.info.domain.usecase.GetRegisteredHdKeys
import com.algorand.wallet.algosdk.domain.model.HdKeyAccount
import com.algorand.wallet.algosdk.transaction.sdk.PeraBip39Sdk
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
    private val bip39Sdk: PeraBip39Sdk,
    private val accountAdditionUseCase: AccountAdditionUseCase,
    private val getRegisteredHdKeys: GetRegisteredHdKeys,
    private val fetchRekeyedAddresses: FetchRekeyedAddresses,
    private val getAccountIconDrawablePreviewByType: GetAccountIconDrawablePreviewByType,
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
                val entropy = aesPlatformManager.decryptByteArray(encryptedEntropy)
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
                val addressesToImport = getAddressesToImport(selectedAddresses)
                addSelectedAddresses(addressesToImport)
                val rekeyedAddresses = fetchRekeyedAddresses(selectedAddresses)
                stateDelegate.updateState { currentState.copy(type = ContentType.Idle) }
                if (rekeyedAddresses.isNotEmpty()) {
                    eventDelegate.sendEvent(ViewEvent.NavigateToRekeyedAccountSelection(rekeyedAddresses))
                } else {
                    val isNewAccountAdded = addressesToImport.isNotEmpty()
                    eventDelegate.sendEvent(ViewEvent.NavigateToHome(isNewAccountAdded))
                }
            }
        }
    }

    private suspend fun addSelectedAddresses(selectedAddresses: List<HdKeyAccount>) {
        selectedAddresses.forEach { hdKeyAccount ->
            val newAccountCreation = createAccountCreation(hdKeyAccount)
            accountAdditionUseCase.addNewAccount(newAccountCreation)
        }
    }

    private fun getAddressesToImport(selectedAddresses: List<RegisteredHdKey>): List<HdKeyAccount> {
        val entropy = aesPlatformManager.decryptByteArray(encryptedEntropy)
        return selectedAddresses.mapNotNull { accountItem ->
            createHdKeyAccount(entropy, accountItem)
        }.also {
            entropy.clearFromMemory()
        }
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

    private fun createHdKeyAccount(entropy: ByteArray, accountItem: RegisteredHdKey): HdKeyAccount? {
        return with(accountItem) {
            bip39Sdk.getHdKeyAccount(entropy.copyOf(), account, change, keyIndex)
        }
    }

    private fun createAccountCreation(account: HdKeyAccount): AccountCreation {
        return with(account) {
            val hdKeyType = AccountCreation.Type.HdKey(
                publicKey,
                aesPlatformManager.encryptByteArray(privateKey),
                aesPlatformManager.encryptByteArray(entropy),
                account.account,
                change,
                keyIndex,
                derivationType,
            )
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
        data class NavigateToHome(val isNewAccountAdded: Boolean) : ViewEvent
        data object NavigateBack : ViewEvent
        data class NavigateToRekeyedAccountSelection(val args: List<RekeyedAccountSelectionNavArg>) : ViewEvent
    }
}
