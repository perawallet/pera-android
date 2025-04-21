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

package com.algorand.android.modules.accounts.ui.model

import com.algorand.android.modules.parity.domain.model.SelectedCurrencyDetail
import com.algorand.android.utils.CacheResult
import com.algorand.wallet.account.local.domain.model.LocalAccount

sealed interface AccountsInitializationStatus {
    data object Loading : AccountsInitializationStatus
    data class ReadyForInitialization(val accounts: List<LocalAccount>) : AccountsInitializationStatus
    data object EmptyAccounts : AccountsInitializationStatus
    data class CurrencyDetailError(val error: CacheResult.Error<SelectedCurrencyDetail>?) : AccountsInitializationStatus
}
