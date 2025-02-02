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

package com.algorand.wallet.account.core.domain.model

data class CreateAccount(
    val address: String,
    var customName: String?,
    val isBackedUp: Boolean,
    val type: Type
) {

    sealed interface Type {
        data class Algo25(val secretKey: ByteArray) : Type
        data class LedgerBle(val deviceMacAddress: String, val indexInLedger: Int, val bluetoothName: String?) : Type
        data object NoAuth : Type
    }
}
