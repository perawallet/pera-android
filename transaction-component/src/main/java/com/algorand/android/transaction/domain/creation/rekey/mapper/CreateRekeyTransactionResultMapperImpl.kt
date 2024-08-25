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

package com.algorand.android.transaction.domain.creation.rekey.mapper

import com.algorand.android.transaction.domain.creation.model.CreateTransactionResult
import com.algorand.android.transaction.domain.creation.rekey.model.CreateRekeyTransactionResult
import com.algorand.android.transaction.domain.creation.rekey.model.CreateRekeyTransactionResult.AccountNotFound
import com.algorand.android.transaction.domain.creation.rekey.model.CreateRekeyTransactionResult.MinBalanceViolated
import com.algorand.android.transaction.domain.creation.rekey.model.CreateRekeyTransactionResult.NetworkError
import com.algorand.android.transaction.domain.creation.rekey.model.CreateRekeyTransactionResult.TransactionCreated
import javax.inject.Inject

internal class CreateRekeyTransactionResultMapperImpl @Inject constructor() : CreateRekeyTransactionResultMapper {

    override fun invoke(result: CreateRekeyTransactionResult): CreateTransactionResult {
        return when (result) {
            is AccountNotFound -> CreateTransactionResult.AccountNotFound(result.address)
            is MinBalanceViolated -> CreateTransactionResult.MinBalanceViolated(result.requiredBalance)
            is NetworkError -> CreateTransactionResult.NetworkError(result.message)
            is TransactionCreated -> CreateTransactionResult.TransactionCreated(result.transaction)
        }
    }
}
