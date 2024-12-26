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

package com.algorand.common.account.detail.domain.model

sealed interface AccountType {

    data object Algo25 : AccountType

    data object LedgerBle : AccountType

    data object Rekeyed : AccountType

    data object RekeyedAuth : AccountType

    data object NoAuth : AccountType

    data object Bip39 : AccountType

    companion object {
        fun AccountType.canSignTransaction(): Boolean {
            return this is Algo25 || this is LedgerBle || this is RekeyedAuth
        }
    }
}
