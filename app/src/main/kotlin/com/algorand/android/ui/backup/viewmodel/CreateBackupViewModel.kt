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

package com.algorand.android.ui.backup.viewmodel

import android.util.Base64
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.algorand.android.R
import com.algorand.android.ui.backup.viewmodel.CreateBackupViewModel.ViewState
import com.algorand.backup.domain.security.SaltGenerator
import com.algorand.wallet.algosdk.bip39.sdk.Bip39WalletProvider
import com.algorand.wallet.algosdk.transaction.sdk.PeraBip39Sdk
import com.algorand.wallet.viewmodel.StateDelegate
import com.algorand.wallet.viewmodel.StateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateBackupViewModel @Inject constructor(
    private val stateDelegate: StateDelegate<ViewState>,
    private val bip39WalletProvider: Bip39WalletProvider,
    private val peraBip39Sdk: PeraBip39Sdk,
    private val saltGenerator: SaltGenerator
) : ViewModel(), StateViewModel<ViewState> by stateDelegate {

    init {
        initViewState()
    }

    private fun initViewState() {
        val wallet = bip39WalletProvider.create12WordBip39Wallet()
        val entropy = wallet.getEntropy().value
        val mnemonic = peraBip39Sdk.getMnemonicFromEntropy(entropy)
        val initialState = if (mnemonic == null) {
            ViewState.Error(R.string.backup_recovery_phrase_error)
        } else {
            ViewState.Content(
                mnemonic = mnemonic,
                encryptionKey = Base64.encodeToString(saltGenerator.generate(), Base64.NO_WRAP)
            )
        }
        stateDelegate.setDefaultState(initialState)
    }

    sealed interface ViewState {
        data class Content(val mnemonic: String, val encryptionKey: String) : ViewState
        data class Error(@param:StringRes val messageResId: Int) : ViewState
    }
}
