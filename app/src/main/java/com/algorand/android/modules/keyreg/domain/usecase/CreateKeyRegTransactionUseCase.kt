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

package com.algorand.android.modules.keyreg.domain.usecase

import com.algorand.algosdk.sdk.Sdk
import com.algorand.android.models.*
import com.algorand.android.models.Result.*
import com.algorand.android.modules.keyreg.domain.model.KeyRegTransaction
import com.algorand.android.modules.keyreg.ui.presentation.model.KeyRegTransactionDetail
import com.algorand.android.repository.TransactionsRepository
import com.algorand.android.usecase.AccountDetailUseCase
import com.algorand.android.utils.*
import java.math.BigInteger
import javax.inject.Inject

class CreateKeyRegTransactionUseCase @Inject constructor(
    private val accountDetailUseCase: AccountDetailUseCase,
    private val transactionsRepository: TransactionsRepository
) : CreateKeyRegTransaction {

    override suspend fun invoke(txnDetail: KeyRegTransactionDetail): Result<KeyRegTransaction> {
        return when (val params = transactionsRepository.getTransactionParams()) {
            is Success -> {
                val txnByteArray = createAlgoTxn(txnDetail.address, params.data)
                if (txnByteArray == null) {
                    Error(IllegalArgumentException())
                } else {
                    Success(createKeyRegTransactionResult(txnDetail, txnByteArray))
                }
            }
            is Error -> {
                Error(params.exception, params.code)
            }
        }
    }

    private fun createKeyRegTransactionResult(
        txnDetail: KeyRegTransactionDetail,
        txnByteArray: ByteArray
    ): KeyRegTransaction {
        val senderAuthAddress = accountDetailUseCase.getAuthAddress(txnDetail.address)
        val isSenderRekeyedToAnotherAccount = accountDetailUseCase.isAccountRekeyed(txnDetail.address)
        return KeyRegTransaction(
            transactionByteArray = txnByteArray,
            accountAddress = txnDetail.address,
            accountAuthAddress = senderAuthAddress,
            isRekeyedToAnotherAccount = isSenderRekeyedToAnotherAccount
        )
    }

    private fun createAlgoTxn(address: String, transactionParams: TransactionParams): ByteArray? {
        return try {
            Sdk.makePaymentTxn(
                address,
                address,
                BigInteger.valueOf(10000).toUint64(),
                null,
                "",
                transactionParams.toSuggestedParams()
            )
        } catch (e: Exception) {
            null
        }
    }
}
