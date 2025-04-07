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

package com.algorand.android.ui.register

import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.models.AccountCreation
import com.algorand.android.modules.tracking.onboarding.register.OnboardingCopyPassphraseEventTracker
import com.algorand.android.ui.register.BackupPassphraseViewModel.ViewState.Idle
import com.algorand.android.utils.launchIO
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetAlgo25SecretKey
import com.algorand.wallet.account.local.domain.usecase.GetHdEntropy
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccount
import com.algorand.wallet.algosdk.transaction.sdk.AlgoAccountSdk
import com.algorand.wallet.algosdk.transaction.sdk.PeraBip39Sdk
import com.algorand.wallet.analytics.domain.service.PeraExceptionLogger
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class BackupPassphraseViewModel @Inject constructor(
    private val onboardingCopyPassphraseEventTracker: OnboardingCopyPassphraseEventTracker,
    private val getAlgo25SecretKey: GetAlgo25SecretKey,
    private val aesPlatformManager: AESPlatformManager,
    private val algoAccountSdk: AlgoAccountSdk,
    private val peraBip39Sdk: PeraBip39Sdk,
    private val getLocalAccount: GetLocalAccount,
    private val getHdEntropy: GetHdEntropy,
    private val peraExceptionLogger: PeraExceptionLogger,
    private val stateDelegate: StateDelegate<ViewState>
) : BaseViewModel(), StateViewModel<BackupPassphraseViewModel.ViewState> by stateDelegate {

    init {
        stateDelegate.setDefaultState(Idle)
    }

    fun logOnboardingNextClickEvent() {
        viewModelScope.launch {
            onboardingCopyPassphraseEventTracker.logOnboardingCopyPassphraseEvent()
        }
    }

    fun getMnemonic(args: BackupPassphraseFragmentArgs) {
        viewModelScope.launchIO {
            var passphrase: List<String> = emptyList()

            args.accountCreation?.let {
                passphrase = handleAccountCreationMnemonic(it.type, it)
            }
            if (passphrase.isEmpty()) {
                args.accountToBackup?.let { accountAddress ->
                    getLocalAccount(accountAddress)?.let {
                        passphrase = handleLocalAccountMnemonic(it)
                    }
                }
            }

            stateDelegate.updateState {
                ViewState.DefaultState(passphrase.joinToString(" "))
            }
        }
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
        data class DefaultState(
            val passphrase: String?
        ) : ViewState
    }
}
