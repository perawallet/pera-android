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

package com.algorand.android.modules.keyreg.domain

import com.algorand.android.R
import com.algorand.android.ledger.LedgerBleOperationManager
import com.algorand.android.ledger.LedgerBleSearchManager
import com.algorand.android.models.AnnotatedString
import com.algorand.android.modules.keyreg.domain.model.KeyRegTransaction
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionQueuingHelper
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignManager
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult.Error
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult.Success
import com.algorand.android.usecase.AccountDetailUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.map

class KeyRegTransactionSignManager @Inject constructor(
    ledgerBleSearchManager: LedgerBleSearchManager,
    ledgerBleOperationManager: LedgerBleOperationManager,
    externalTransactionQueuingHelper: ExternalTransactionQueuingHelper,
    accountDetailUseCase: AccountDetailUseCase
) : ExternalTransactionSignManager<KeyRegTransaction>(
    ledgerBleSearchManager,
    ledgerBleOperationManager,
    externalTransactionQueuingHelper,
    accountDetailUseCase
) {

    private var unsignedTransaction: KeyRegTransaction? = null

    val keyRegTransactionSignResultFlow = signResultFlow.map {
        when (it) {
            is Success<*> -> mapSignedTransaction(unsignedTransaction, it.signedTransactionsByteArray)
            else -> it
        }
    }

    fun signKeyRegTransaction(keyRegTransaction: KeyRegTransaction) {
        unsignedTransaction = keyRegTransaction
        signTransaction(listOf(keyRegTransaction))
    }

    private fun mapSignedTransaction(
        transaction: KeyRegTransaction?,
        signedTransactions: List<ByteArray?>?
    ): ExternalTransactionSignResult {
        if (transaction == null) return Error.Defined(AnnotatedString(R.string.an_error_occured))
        val signedTransaction = signedTransactions?.firstOrNull()
        return if (signedTransaction == null) {
            Error.Defined(AnnotatedString(R.string.an_error_occured))
        } else {
            Success(listOf(signedTransaction))
        }
    }
}
