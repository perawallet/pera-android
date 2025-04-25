/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.ui.register

import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.models.AccountCreation
import com.algorand.android.modules.tracking.onboarding.register.OnboardingVerifyPassphraseEventTracker
import com.algorand.android.utils.launchIO
import com.algorand.wallet.account.custom.domain.usecase.SetAddressesBackedUp
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetAlgo25SecretKey
import com.algorand.wallet.account.local.domain.usecase.GetHdEntropy
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccount
import com.algorand.wallet.algosdk.transaction.sdk.AlgoAccountSdk
import com.algorand.wallet.algosdk.transaction.sdk.PeraBip39Sdk
import com.algorand.wallet.analytics.domain.service.PeraExceptionLogger
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import com.algorand.wallet.viewmodel.EventDelegate
import com.algorand.wallet.viewmodel.EventViewModel
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@Suppress("LongParameterList")
@HiltViewModel
class PassphraseValidationViewModel @Inject constructor(
    private val onboardingVerifyPassphraseEventTracker: OnboardingVerifyPassphraseEventTracker,
    private val aesPlatformManager: AESPlatformManager,
    private val algoAccountSdk: AlgoAccountSdk,
    private val getAlgo25SecretKey: GetAlgo25SecretKey,
    private val peraBip39Sdk: PeraBip39Sdk,
    private val stateDelegate: StateDelegate<ViewState>,
    private val eventDelegate: EventDelegate<ViewEvent>,
    private val setAddressesBackedUp: SetAddressesBackedUp,
    private val getLocalAccount: GetLocalAccount,
    private val getHdEntropy: GetHdEntropy,
    private val peraExceptionLogger: PeraExceptionLogger
) : BaseViewModel(),
    EventViewModel<PassphraseValidationViewModel.ViewEvent> by eventDelegate,
    StateViewModel<PassphraseValidationViewModel.ViewState> by stateDelegate {

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun logOnboardingNextClickEvent() {
        viewModelScope.launch {
            onboardingVerifyPassphraseEventTracker.logOnboardingVerifyPassphraseEvent()
        }
    }

    fun setAccountBackedUp(address: String) {
        viewModelScope.launchIO {
            setAddressesBackedUp.invoke(setOf(address))
            eventDelegate.sendEvent(ViewEvent.PassphraseVerifiedComplete)
        }
    }

    fun setupPassphraseValidationView(args: PassphraseValidationFragmentArgs) {
        viewModelScope.launchIO {
            val passphrase = getMnemonic(args)
            stateDelegate.updateState {
                ViewState.DefaultState(passphrase)
            }
        }
    }

    fun recreatePassphraseValidationView(args: PassphraseValidationFragmentArgs) {
        viewModelScope.launchIO {
            val passphrase = getMnemonic(args)
            stateDelegate.updateState { ViewState.RecreateState(passphrase) }
        }
    }

    private suspend fun getMnemonic(args: PassphraseValidationFragmentArgs): List<String> {

        args.accountToBackup?.let { accountAddress ->
            getLocalAccount(accountAddress)?.let { return handleLocalAccountMnemonic(it) }
        }

        args.accountCreation?.type?.let { accountType ->
            return handleAccountCreationMnemonic(accountType, args.accountCreation)
        }

        return emptyList()
    }

    private suspend fun handleLocalAccountMnemonic(localAccount: LocalAccount): List<String> =
        when (localAccount) {
            is LocalAccount.HdKey -> {
                getHdEntropy(seedId = localAccount.seedId)?.let { entropy ->
                    peraBip39Sdk.getMnemonicFromEntropy(entropy)?.split(" ") ?: emptyList()
                } ?: emptyList()
            }
            is LocalAccount.Algo25 -> {
                getAlgo25SecretKey(address = localAccount.algoAddress)?.let { secretKey ->
                    try {
                        algoAccountSdk.getMnemonicFromAlgo25SecretKey(secretKey)?.split(" ") ?: emptyList()
                    } catch (e: Exception) {
                        peraExceptionLogger.logException(e)
                        emptyList()
                    }
                } ?: emptyList()
            }
            else -> emptyList()
        }

    private fun handleAccountCreationMnemonic(
        accountType: AccountCreation.Type,
        accountCreation: AccountCreation?
    ): List<String> =
        when (accountType) {
            is AccountCreation.Type.HdKey -> {
                accountType.encryptedEntropy.let { encryptedEntropy ->
                    aesPlatformManager.decryptByteArray(encryptedEntropy).let { entropy ->
                        peraBip39Sdk.getMnemonicFromEntropy(entropy)?.split(" ") ?: emptyList()
                    }
                }
            }
            is AccountCreation.Type.Algo25 -> {
                (accountCreation?.type as? AccountCreation.Type.Algo25)?.encryptedSecretKey?.let { encryptedAlgo25Key ->
                    aesPlatformManager.decryptByteArray(encryptedAlgo25Key).let { secretKey ->
                        try {
                            algoAccountSdk.getMnemonicFromAlgo25SecretKey(secretKey)?.split(" ") ?: emptyList()
                        } catch (e: Exception) {
                            peraExceptionLogger.logException(e)
                            emptyList()
                        }
                    }
                } ?: emptyList()
            }
            else -> emptyList()
        }

    sealed interface ViewState {
        data object Idle : ViewState
        data class DefaultState(val passphrase: List<String>) : ViewState
        data class RecreateState(val passphrase: List<String>) : ViewState
    }

    sealed interface ViewEvent {
        data object PassphraseVerifiedComplete : ViewEvent
    }
}
