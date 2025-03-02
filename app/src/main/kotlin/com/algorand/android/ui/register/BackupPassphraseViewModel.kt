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
import cash.z.ecc.android.bip39.Mnemonics
import com.algorand.algosdk.sdk.Sdk
import com.algorand.android.core.AccountManager
import com.algorand.android.core.BaseViewModel
import com.algorand.android.models.AccountCreation
import com.algorand.android.modules.tracking.onboarding.register.OnboardingCopyPassphraseEventTracker
import com.algorand.wallet.account.core.domain.usecase.GetHdWalletEntropyFromSeedId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class BackupPassphraseViewModel @Inject constructor(
    private val onboardingCopyPassphraseEventTracker: OnboardingCopyPassphraseEventTracker,
    private val accountManager: AccountManager,
    private val getHdWalletEntropyFromSeedId: GetHdWalletEntropyFromSeedId
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
        val seedId = (args.accountCreation?.type as? AccountCreation.Type.HdKey)?.seedId

        return seedId?.let {
            runBlocking(Dispatchers.IO) {
                val entropy = getHdWalletEntropyFromSeedId.invoke(it)
                entropy?.let {
                    val mnemonic = Mnemonics.MnemonicCode(entropy).words.joinToString(" ") { charArray ->
                        String(charArray)
                    }
                    mnemonic
                }
            }
        } ?: run {
            val secretKey = (args.accountCreation?.type as? AccountCreation.Type.Algo25)?.secretKey
                ?: getAccountSecretKey(args.publicKeyOfAccountToBackup)
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
