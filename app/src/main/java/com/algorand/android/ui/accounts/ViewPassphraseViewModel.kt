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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algorand.algosdk.sdk.Sdk
import com.algorand.android.module.account.local.domain.usecase.GetSecretKey
import com.algorand.android.utils.launchIO
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class ViewPassphraseViewModel @Inject constructor(
    private val getSecretKey: GetSecretKey,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val accountPublicKeyArg by lazy { savedStateHandle.get<String>(PUBLIC_KEY).orEmpty() }

    private val _passphraseFlow = MutableStateFlow<String?>("")
    val passphraseFlow
        get() = _passphraseFlow.asStateFlow()

    fun initializePassphrase() {
        viewModelScope.launchIO {
            val secretKey = getSecretKey(accountPublicKeyArg)
            try {
                val mnemonic = Sdk.mnemonicFromPrivateKey(secretKey) ?: throw Exception("Mnemonic cannot be null.")
                _passphraseFlow.value = mnemonic
            } catch (exception: Exception) {
                _passphraseFlow.value = null
            }
        }
    }

    companion object {
        private const val PUBLIC_KEY = "publicKey"
    }
}
