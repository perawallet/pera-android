/*
 * Copyright 2022-2025 Pera Wallet, LDA
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

package com.algorand.android.models

import android.os.Parcelable
import com.algorand.android.utils.analytics.CreationType
import com.algorand.wallet.account.core.domain.model.CreateAccount
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccountCreation(
    val address: String,
    var customName: String?,
    var orderIndex: Int = Int.MAX_VALUE,
    val isBackedUp: Boolean,
    val type: Type,
    val creationType: CreationType
) : Parcelable {

    sealed interface Type : Parcelable {
        @Parcelize
        data class HdKey(
            val publicKey: ByteArray,
            val encryptedPrivateKey: ByteArray,
            val encryptedEntropy: ByteArray,
            val account: Int,
            val change: Int,
            val keyIndex: Int,
            val derivationType: Int
        ) : Type

        @Parcelize
        data class Algo25(val encryptedSecretKey: ByteArray) : Type

        @Parcelize
        data class LedgerBle(val deviceMacAddress: String, val indexInLedger: Int, val bluetoothName: String?) : Type

        @Parcelize
        data object NoAuth : Type
    }

    fun toCreateAccount(): CreateAccount {
        return CreateAccount(
            address = address,
            customName = customName,
            orderIndex = orderIndex,
            isBackedUp = isBackedUp,
            type = when (type) {
                is Type.HdKey -> CreateAccount.Type.HdKey(
                    type.publicKey,
                    type.encryptedPrivateKey,
                    type.encryptedEntropy,
                    type.account,
                    type.change,
                    type.keyIndex,
                    type.derivationType,
                )
                is Type.Algo25 -> CreateAccount.Type.Algo25(type.encryptedSecretKey)
                is Type.NoAuth -> CreateAccount.Type.NoAuth
                is Type.LedgerBle -> CreateAccount.Type.LedgerBle(
                    type.deviceMacAddress,
                    type.indexInLedger,
                    type.bluetoothName
                )
            }
        )
    }
}
