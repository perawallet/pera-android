/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.module.transaction.component.domain.creation.rekey.model

import com.algorand.android.module.algosdk.transaction.model.Transaction
import java.math.BigInteger

sealed interface CreateRekeyTransactionResult {

    data class NetworkError(val message: String?) : CreateRekeyTransactionResult

    data class MinBalanceViolated(val requiredBalance: BigInteger) : CreateRekeyTransactionResult

    data class AccountNotFound(val address: String) : CreateRekeyTransactionResult

    data class TransactionCreated(val transaction: Transaction) : CreateRekeyTransactionResult
}
