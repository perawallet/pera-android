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

package com.algorand.android.modules.keyreg.domain

import com.algorand.android.R
import com.algorand.android.core.transaction.external.ExternalTransactionSignManager
import com.algorand.android.ledger.LedgerBleOperationManager
import com.algorand.android.ledger.LedgerBleSearchManager
import com.algorand.android.models.AnnotatedString
import com.algorand.android.modules.keyreg.domain.model.KeyRegTransaction
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionQueuingHelper
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult.Error
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult.Success
import com.algorand.wallet.account.core.domain.usecase.GetTransactionSigner
import com.algorand.wallet.account.local.domain.usecase.GetAlgo25SecretKey
import com.algorand.wallet.account.local.domain.usecase.GetHdSeed
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccount
import com.algorand.wallet.algosdk.transaction.sdk.SignHdKeyTransaction
import javax.inject.Inject
import kotlinx.coroutines.flow.map

class KeyRegTransactionSignManager @Inject constructor(
    ledgerBleSearchManager: LedgerBleSearchManager,
    ledgerBleOperationManager: LedgerBleOperationManager,
    externalTransactionQueuingHelper: ExternalTransactionQueuingHelper,
    getTransactionSigner: GetTransactionSigner,
    getAlgo25SecretKey: GetAlgo25SecretKey,
    getHdSeed: GetHdSeed,
    getLocalAccount: GetLocalAccount,
    signHdKeyTransaction: SignHdKeyTransaction
) : ExternalTransactionSignManager<KeyRegTransaction>(
    ledgerBleSearchManager,
    ledgerBleOperationManager,
    externalTransactionQueuingHelper,
    getTransactionSigner,
    getAlgo25SecretKey,
    getHdSeed,
    getLocalAccount,
    signHdKeyTransaction
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
