/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License
 *
 */

package com.algorand.android.ui.accounts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetAlgo25SecretKey
import com.algorand.wallet.account.local.domain.usecase.GetHdEntropy
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccount
import com.algorand.wallet.algosdk.transaction.sdk.AlgoAccountSdk
import com.algorand.wallet.algosdk.transaction.sdk.PeraBip39Sdk
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ViewPassphraseViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getLocalAccount: GetLocalAccount,
    private val bip39Sdk: PeraBip39Sdk,
    private val algoAccountSdk: AlgoAccountSdk,
    private val getAlgo25SecretKey: GetAlgo25SecretKey,
    private val getHdEntropy: GetHdEntropy,
) : BaseViewModel() {

    private val accountAddress by lazy { savedStateHandle.get<String>(ACCOUNT_ADDRESS).orEmpty() }

    private val _state = MutableStateFlow(ViewPassphraseContract.State(isLoading = true))
    val state: StateFlow<ViewPassphraseContract.State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ViewPassphraseContract.Effect>()
    val effect: SharedFlow<ViewPassphraseContract.Effect> = _effect.asSharedFlow()

    init {
        processIntent(ViewPassphraseContract.Intent.LoadMnemonic)
    }

    fun processIntent(intent: ViewPassphraseContract.Intent) {
        when (intent) {
            is ViewPassphraseContract.Intent.LoadMnemonic -> loadMnemonic()
        }
    }

    private fun loadMnemonic() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isLoading = true) }

            try {
                val localAccount = getLocalAccount(accountAddress)
                val mnemonic = when (localAccount) {
                    is LocalAccount.Algo25 -> {
                        val secretKey = getAlgo25SecretKey(accountAddress)
                        secretKey?.let {
                            algoAccountSdk.getMnemonicFromAlgo25SecretKey(it)
                        }
                    }
                    is LocalAccount.HdKey -> {
                        val entropy = getHdEntropy(localAccount.seedId)
                        entropy?.let {
                            bip39Sdk.getMnemonicFromEntropy(it)
                        }
                    }
                    else -> null
                }

                if (mnemonic != null) {
                    _state.update { it.copy(mnemonic = mnemonic, isLoading = false) }
                } else {
                    _state.update { it.copy(error = "Could not retrieve passphrase", isLoading = false) }
                    _effect.emit(ViewPassphraseContract.Effect.NavigateBack)
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
                _effect.emit(ViewPassphraseContract.Effect.NavigateBack)
            }
        }
    }

    companion object {
        private const val ACCOUNT_ADDRESS = "accountAddress"
    }
}
