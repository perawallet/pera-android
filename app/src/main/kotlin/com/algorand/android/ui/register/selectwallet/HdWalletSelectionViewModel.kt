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

package com.algorand.android.ui.register.selectwallet

import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.models.AccountCreation
import com.algorand.android.ui.onboarding.creation.mapper.AccountCreationHdKeyTypeMapper
import com.algorand.android.ui.register.selectwallet.HdWalletSelectionViewModel.ViewEvent
import com.algorand.android.ui.register.selectwallet.HdWalletSelectionViewModel.ViewState
import com.algorand.android.utils.analytics.CreationType
import com.algorand.android.utils.launchIO
import com.algorand.wallet.account.local.domain.usecase.GetHdEntropy
import com.algorand.wallet.account.local.domain.usecase.GetHdWalletSummaries
import com.algorand.wallet.algosdk.bip39.model.HdKeyAddressIndex
import com.algorand.wallet.algosdk.bip39.sdk.Bip39WalletProvider
import com.algorand.wallet.encryption.domain.utils.clearFromMemory
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HdWalletSelectionViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val bip39WalletProvider: Bip39WalletProvider,
    private val getHdEntropy: GetHdEntropy,
    private val getHdWalletSummaries: GetHdWalletSummaries,
    private val accountCreationHdKeyTypeMapper: AccountCreationHdKeyTypeMapper
) : BaseViewModel(), StateViewModel<ViewState> by stateDelegate, EventViewModel<ViewEvent> by eventDelegate {

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun loadLocalWallets() {
        stateDelegate.updateState { ViewState.Loading }
        viewModelScope.launch {
            val walletItemPreviews = getHdWalletSummaries()?.map {
                WalletItemPreview(
                    seedId = it.seedId,
                    name = "Wallet #${it.seedId}",
                    numberOfAccounts = "${it.accountCount} account",
                    primaryValue = it.primaryValue,
                    secondaryValue = it.secondaryValue,
                    maxAccountIndex = it.maxAccountIndex
                )
            }.orEmpty()
            stateDelegate.updateState {
                ViewState.Content(
                    walletItemPreviews = walletItemPreviews,
                )
            }
        }
    }

    fun createNewHdWallet() {
        viewModelScope.launchIO {
            val wallet = bip39WalletProvider.createBip39Wallet()
            val hdKeyAddress = wallet.generateAddress(HdKeyAddressIndex())
            val hdKeyType = accountCreationHdKeyTypeMapper(wallet.getEntropy().value, hdKeyAddress, seedId = null)
            val accountCreation = AccountCreation(
                address = hdKeyAddress.address,
                customName = null,
                isBackedUp = false,
                type = hdKeyType,
                creationType = CreationType.CREATE
            )
            eventDelegate.sendEvent(ViewEvent.NavigateToCreateWalletNameRegistrationNavigation(accountCreation))
        }
    }

    fun createNewHdAccount(seedId: Int, maxAccountIndex: Int) {
        viewModelScope.launchIO {

            val entropy = getHdEntropy(seedId) ?: return@launchIO
            val nextHdAccountIndex = maxAccountIndex + 1
            val wallet = bip39WalletProvider.getBip39Wallet(entropy)
            val index = HdKeyAddressIndex(nextHdAccountIndex)
            val hdKeyAddress = wallet.generateAddress(index)
            val accountCreation = AccountCreation(
                address = hdKeyAddress.address,
                customName = null,
                isBackedUp = false,
                type = accountCreationHdKeyTypeMapper(entropy, hdKeyAddress, seedId),
                creationType = CreationType.CREATE
            )
            entropy.clearFromMemory()
            wallet.invalidate()
            eventDelegate.sendEvent(ViewEvent.NavigateToCreateAccountNameRegistrationNavigation(accountCreation))
        }
    }

    data class WalletItemPreview(
        val seedId: Int,
        val name: String,
        val numberOfAccounts: String,
        val primaryValue: String,
        val secondaryValue: String,
        val maxAccountIndex: Int
    )

    sealed interface ViewState {
        data object Idle : ViewState
        data object Loading : ViewState
        data class Content(
            val walletItemPreviews: List<WalletItemPreview> = emptyList(),
        ) : ViewState

        data class Error(val message: String) : ViewState
    }

    sealed interface ViewEvent {
        data class NavigateToCreateAccountNameRegistrationNavigation(
            val accountCreation: AccountCreation?
        ) : ViewEvent

        data class NavigateToCreateWalletNameRegistrationNavigation(
            val accountCreation: AccountCreation?
        ) : ViewEvent

        data object NavigateBack : ViewEvent
    }
}
