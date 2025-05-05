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
import com.algorand.android.ui.register.selectwallet.SelectUniversalWalletViewModel.ViewEvent
import com.algorand.android.ui.register.selectwallet.SelectUniversalWalletViewModel.ViewState
import com.algorand.android.utils.analytics.CreationType
import com.algorand.android.utils.launchIO
import com.algorand.wallet.account.local.domain.usecase.GetAllHdSeeds
import com.algorand.wallet.account.local.domain.usecase.GetHdEntropy
import com.algorand.wallet.algosdk.transaction.sdk.AlgoAccountSdk
import com.algorand.wallet.algosdk.transaction.sdk.PeraBip39Sdk
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectUniversalWalletViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val aesPlatformManager: AESPlatformManager,
    private val algoAccountSdk: AlgoAccountSdk,
    private val bip39Sdk: PeraBip39Sdk,
    private val getHdEntropy: GetHdEntropy,
    private val getAllHdSeeds: GetAllHdSeeds
) : BaseViewModel(), StateViewModel<ViewState> by stateDelegate, EventViewModel<ViewEvent> by eventDelegate {

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun loadLocalWallets() {
        stateDelegate.updateState { ViewState.Loading }
        viewModelScope.launch {
            val walletItemPreviews = getAllHdSeeds().map {
                WalletItemPreview(
                    seedId = it.seedId,
                    name = "Wallet #${it.seedId}",
                    numberOfAccounts = "1 account",
                    primaryValue = "0,00",
                    secondaryValue = "0,00"
                )
            }
            stateDelegate.updateState {
                ViewState.Content(
                    walletItemPreviews = walletItemPreviews,
                )
            }
        }
    }


    fun createNewHdWallet() {
        viewModelScope.launchIO {
            val account = algoAccountSdk.createHdAccount()
                ?: return@launchIO

            val accountCreation = AccountCreation(
                address = account.address,
                customName = null,
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
                creationType = CreationType.CREATE
            )
            eventDelegate.sendEvent(ViewEvent.NavigateToCreateWalletNameRegistrationNavigation(accountCreation))
        }
    }

    fun createNewHdAccount(seedId: Int) {
        viewModelScope.launchIO {

            val entropy = getHdEntropy(seedId)
            val account = bip39Sdk.getHdKeyAccount(
                entropy = entropy ?: return@launchIO, accountIndex = 1, changeIndex = 0, keyIndex = 0
            ) ?: return@launchIO

            val accountCreation = AccountCreation(
                address = account.address, customName = null, isBackedUp = false, type = AccountCreation.Type.HdKey(
                    account.publicKey,
                    aesPlatformManager.encryptByteArray(account.privateKey),
                    aesPlatformManager.encryptByteArray(account.entropy),
                    account.account,
                    account.change,
                    account.keyIndex,
                    account.derivationType,
                    seedId
                ), creationType = CreationType.CREATE
            )
            eventDelegate.sendEvent(ViewEvent.NavigateToCreateAccountNameRegistrationNavigation(accountCreation))
        }
    }

    data class WalletItemPreview(
        val seedId: Int,
        val name: String,
        val numberOfAccounts: String,
        val primaryValue: String,
        val secondaryValue: String
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
