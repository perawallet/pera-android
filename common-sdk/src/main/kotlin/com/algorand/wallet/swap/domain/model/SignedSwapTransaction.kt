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

package com.algorand.wallet.swap.domain.model

import com.algorand.wallet.transaction.domain.model.SignedTransaction

data class SignedSwapTransaction(
    val signedTransaction: SignedTransaction,
    val type: Type
) {
    val txnByteArray: ByteArray
        get() = signedTransaction.signedTxn

    sealed interface Type {

        val isConfirmationRequired: Boolean
        val confirmationDelay: Long?

        data object OptIn : Type {
            override val isConfirmationRequired: Boolean = true
            override val confirmationDelay: Long = 1000L
        }

        data object Swap : Type {
            override val isConfirmationRequired: Boolean = true
            override val confirmationDelay: Long? = null
        }

        data object PeraFee : Type {
            override val isConfirmationRequired: Boolean = false
            override val confirmationDelay: Long? = null
        }
    }
}
