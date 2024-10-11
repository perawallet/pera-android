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

package com.algorand.android.modules.assetinbox.detail.receivedetail.ui

import com.algorand.android.R
import com.algorand.android.ledger.LedgerBleOperationManager
import com.algorand.android.ledger.LedgerBleSearchManager
import com.algorand.android.models.AnnotatedString
import com.algorand.android.models.SignedTransactionDetail
import com.algorand.android.modules.assetinbox.detail.receivedetail.domain.model.BaseArc59ClaimRejectTransaction
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionQueuingHelper
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignManager
import com.algorand.android.modules.transaction.signmanager.ExternalTransactionSignResult
import com.algorand.android.usecase.AccountDetailUseCase
import com.algorand.android.utils.flatten
import javax.inject.Inject
import kotlinx.coroutines.flow.map

class Arc59ClaimRejectTransactionSignManager @Inject constructor(
    ledgerBleSearchManager: LedgerBleSearchManager,
    ledgerBleOperationManager: LedgerBleOperationManager,
    externalTransactionQueuingHelper: ExternalTransactionQueuingHelper,
    accountDetailUseCase: AccountDetailUseCase
) : ExternalTransactionSignManager<BaseArc59ClaimRejectTransaction>(
    ledgerBleSearchManager,
    ledgerBleOperationManager,
    externalTransactionQueuingHelper,
    accountDetailUseCase
) {

    val arc59ClaimRejectTransactionSignResultFlow = signResultFlow.map { externalTransactionSignResult ->
        when (externalTransactionSignResult) {
            is ExternalTransactionSignResult.Success<*> -> mapSignedTransactions(
                externalTransactionSignResult.signedTransactionsByteArray
            )
            else -> externalTransactionSignResult
        }
    }

    private fun mapSignedTransactions(
        signedTransactions: List<ByteArray?>?
    ): ExternalTransactionSignResult {
        val transactionByteArray = signedTransactions?.filterNotNull()?.flatten()
        return if (transactionByteArray == null) {
            ExternalTransactionSignResult.Error.Defined(AnnotatedString(R.string.an_error_occured))
        } else {
            val txnDetail = listOf(SignedTransactionDetail.Arc59ClaimOrReject(transactionByteArray))
            ExternalTransactionSignResult.Success<SignedTransactionDetail>(txnDetail)
        }
    }
}
