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

package com.algorand.android.modules.onboarding.recoverypassphrase.importaddresses

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.algorand.android.core.BaseViewModel
import com.algorand.android.models.AccountCreation
import com.algorand.android.usecase.AccountAdditionUseCase
import com.algorand.android.utils.analytics.CreationType
import com.algorand.wallet.account.core.domain.model.CreateAccount.Type
import com.algorand.wallet.algosdk.model.RegisteredAlgorandAccount
import com.algorand.wallet.algosdk.transaction.sdk.PeraBip39Sdk
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class RecoverRegisteredAccountsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val aesPlatformManager: AESPlatformManager,
    private val bip39Sdk: PeraBip39Sdk,
    private val accountAdditionUseCase: AccountAdditionUseCase
) : BaseViewModel() {
    private val accountCreation: AccountCreation = savedStateHandle["accountCreation"]
        ?: error("Missing accountCreation argument")

    private val _registeredAccountsFlow = MutableStateFlow<List<RegisteredAlgorandAccount>>(emptyList())
    val registeredAccountsFlow: StateFlow<List<RegisteredAlgorandAccount>>
        get() = _registeredAccountsFlow

    init {
        viewModelScope.launch {
            accountCreation.let {
                with(accountCreation.toCreateAccount().type) {
                    when (this) {
                        is Type.HdKey -> {
                            var entropy = aesPlatformManager.decryptByteArray(this.encryptedEntropy)
                            val registeredAccounts = bip39Sdk.fetchRegisteredAccounts(entropy)
                            entropy = ByteArray(0) // clear secret from memory
                            registeredAccounts.let {
                                _registeredAccountsFlow.value = it
                            }
                        }
                        else -> {
                            _registeredAccountsFlow.value = emptyList()
                        }
                    }
                }
            }
        }
    }

    fun importRegisteredAccounts(selectedAddresses: Set<String>, registeredAccounts: List<RegisteredAlgorandAccount>) {
        viewModelScope.launch(Dispatchers.IO) {
            val encryptedEntropy = (accountCreation.type
                    as AccountCreation.Type.HdKey).encryptedEntropy
            var entropy = aesPlatformManager.decryptByteArray(encryptedEntropy)

            registeredAccounts.filter { selectedAddresses.contains(it.address) }
                .forEach { accountItem ->
                    bip39Sdk.getHdKeyAccount(
                        entropy,
                        accountItem.account,
                        accountItem.change,
                        accountItem.keyIndex
                    )?.let { account ->
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
                            creationType = CreationType.RECOVER
                        )
                        accountAdditionUseCase.addNewAccount(accountCreation)
                    }
                }
            entropy = ByteArray(0) // clear secret from memory
        }
    }
}
