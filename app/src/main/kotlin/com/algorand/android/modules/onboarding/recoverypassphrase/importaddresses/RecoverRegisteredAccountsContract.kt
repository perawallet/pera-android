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

import com.algorand.wallet.algosdk.model.RegisteredAlgorandAccount

data class RecoverRegisteredAccountsState(
    val registeredAccounts: List<RegisteredAlgorandAccount> = emptyList(),
    val selectedAddresses: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val isImportDone: Boolean = false,
    val error: String? = null
) {
    val canImport: Boolean
        get() = selectedAddresses.isNotEmpty() && !isLoading
}

sealed class RecoverRegisteredAccountsIntent {
    object LoadRegisteredAccounts : RecoverRegisteredAccountsIntent()
    data class ToggleAccountSelection(val address: String, val isSelected: Boolean) : RecoverRegisteredAccountsIntent()
    object SelectAllAccounts : RecoverRegisteredAccountsIntent()
    object UnselectAllAccounts : RecoverRegisteredAccountsIntent()
    object ImportSelectedAccounts : RecoverRegisteredAccountsIntent()
    object NavigateToHome : RecoverRegisteredAccountsIntent()
}

sealed class RecoverRegisteredAccountsEffect {
    object NavigateToHome : RecoverRegisteredAccountsEffect()
}
