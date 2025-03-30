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
import com.algorand.android.core.AccountManager
import com.algorand.android.core.BaseViewModel
import com.algorand.android.models.AccountCreation
import com.algorand.android.modules.tracking.onboarding.register.OnboardingVerifyPassphraseEventTracker
import com.algorand.android.utils.launchIO
import com.algorand.wallet.account.custom.domain.usecase.SetAddressesBackedUp
import com.algorand.wallet.account.local.domain.usecase.GetAlgo25SecretKey
import com.algorand.wallet.algosdk.transaction.sdk.AlgoAccountSdk
import com.algorand.wallet.algosdk.transaction.sdk.PeraBip39Sdk
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class PassphraseValidationViewModel @Inject constructor(
    private val onboardingVerifyPassphraseEventTracker: OnboardingVerifyPassphraseEventTracker,
    private val accountManager: AccountManager,
    private val aesPlatformManager: AESPlatformManager,
    private val algoAccountSdk: AlgoAccountSdk,
    private val getAlgo25SecretKey: GetAlgo25SecretKey,
    private val peraBip39Sdk: PeraBip39Sdk,
    private val stateDelegate: StateDelegate<ViewState>,
    private val setAddressesBackedUp: SetAddressesBackedUp,
) : BaseViewModel(), StateViewModel<PassphraseValidationViewModel.ViewState> by stateDelegate {

    init {
        stateDelegate.setDefaultState(ViewState.Idle)
    }

    fun logOnboardingNextClickEvent() {
        viewModelScope.launch {
            onboardingVerifyPassphraseEventTracker.logOnboardingVerifyPassphraseEvent()
        }
    }

    fun updateAccountBackupState(address: String, isBackedUp: Boolean) {
        viewModelScope.launch {
            if (isBackedUp)
                setAddressesBackedUp.invoke(setOf(address))
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
        val encryptedEntropy = (args.accountCreation?.type as? AccountCreation.Type.HdKey)?.encryptedEntropy
        val passphrase = encryptedEntropy?.let {
            val entropy = aesPlatformManager.decryptByteArray(it)
            peraBip39Sdk.getMnemonicFromEntropy(entropy)
        } ?: run {
            val encryptedAlgo25Key =
                (args.accountCreation?.type as? AccountCreation.Type.Algo25)?.encryptedSecretKey

            val secretKey = encryptedAlgo25Key?.let {
                aesPlatformManager.decryptByteArray(encryptedAlgo25Key)
            } ?: getAlgo25SecretKey(args.accountToBackup)

            secretKey?.let {
                try {
                    val mnemonic = algoAccountSdk.getMnemonicFromSecretKey(it)
                        ?: throw Exception("Mnemonic cannot be null.")
                    mnemonic
                } catch (exception: Exception) {
                    null
                }
            } ?: run { null }
        }
        return passphrase?.split(" ") ?: emptyList()
    }

    sealed interface ViewState {
        data object Idle : ViewState
        data class DefaultState(val passphrase: List<String>) : ViewState
        data class RecreateState(val passphrase: List<String>) : ViewState
    }
}
