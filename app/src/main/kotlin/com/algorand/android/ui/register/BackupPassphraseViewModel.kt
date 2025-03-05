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
import com.algorand.algosdk.sdk.Sdk
import com.algorand.android.core.AccountManager
import com.algorand.android.core.BaseViewModel
import com.algorand.android.models.AccountCreation
import com.algorand.android.modules.tracking.onboarding.register.OnboardingCopyPassphraseEventTracker
import com.algorand.wallet.algosdk.transaction.sdk.Bip39MnemonicGenerator
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class BackupPassphraseViewModel @Inject constructor(
    private val onboardingCopyPassphraseEventTracker: OnboardingCopyPassphraseEventTracker,
    private val accountManager: AccountManager,
    private val aesPlatformManager: AESPlatformManager,
    private val bip39MnemonicGenerator: Bip39MnemonicGenerator
) : BaseViewModel() {

    fun logOnboardingNextClickEvent() {
        viewModelScope.launch {
            onboardingCopyPassphraseEventTracker.logOnboardingCopyPassphraseEvent()
        }
    }

    fun getAccountSecretKey(publicKey: String): ByteArray? {
        return accountManager.getAccount(publicKey)?.getSecretKey()
    }

    fun getMnemonic(args: BackupPassphraseFragmentArgs): String? {
        val encryptedEntropy = (args.accountCreation?.type as? AccountCreation.Type.HdKey)?.encryptedEntropy
        return encryptedEntropy?.let {
            val entropy = aesPlatformManager.decryptByteArray(it)
            bip39MnemonicGenerator.getMnemonicFromEntropy(entropy)
        } ?: run {
            val encryptedAlgo25Key = (args.accountCreation?.type as? AccountCreation.Type.Algo25)?.encryptedSecretKey

            val secretKey = encryptedAlgo25Key?.let {
                aesPlatformManager.decryptByteArray(encryptedAlgo25Key)
            } ?: getAccountSecretKey(args.publicKeyOfAccountToBackup)

            secretKey?.let {
                try {
                    val mnemonic = Sdk.mnemonicFromPrivateKey(it) ?: throw Exception("Mnemonic cannot be null.")
                    mnemonic
                } catch (exception: Exception) {
                    null
                }
            } ?: run { null }
        }
    }
}
