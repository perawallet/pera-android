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

import com.algorand.android.models.Result
import com.algorand.android.models.Result.Error
import com.algorand.android.models.Result.Success
import com.algorand.android.models.TransactionParams
import com.algorand.android.modules.accounts.domain.usecase.GetAuthAddressOfAnAccount
import com.algorand.android.modules.accounts.domain.usecase.IsSenderRekeyedToAnotherAccount
import com.algorand.android.modules.algosdk.domain.model.OnlineKeyRegTransactionPayload
import com.algorand.android.modules.algosdk.domain.usecase.BuildKeyRegOfflineTransaction
import com.algorand.android.modules.algosdk.domain.usecase.BuildKeyRegOnlineTransaction
import com.algorand.android.modules.keyreg.domain.model.KeyRegTransaction
import com.algorand.android.modules.keyreg.ui.presentation.model.KeyRegTransactionDetail
import com.algorand.android.modules.transaction.domain.GetTransactionParams
import javax.inject.Inject

fun interface CreateKeyRegTransaction {
    suspend operator fun invoke(txnDetail: KeyRegTransactionDetail): Result<KeyRegTransaction>
}

internal class CreateKeyRegTransactionUseCase @Inject constructor(
    private val isSenderRekeyedToAnotherAccount: IsSenderRekeyedToAnotherAccount,
    private val getAuthAddressOfAnAccount: GetAuthAddressOfAnAccount,
    private val getTransactionParams: GetTransactionParams,
    private val buildKeyRegOfflineTransaction: BuildKeyRegOfflineTransaction,
    private val buildKeyRegOnlineTransaction: BuildKeyRegOnlineTransaction
) : CreateKeyRegTransaction {

    override suspend fun invoke(txnDetail: KeyRegTransactionDetail): Result<KeyRegTransaction> {
        return when (val params = getTransactionParams()) {
            is Success -> {
                val txnByteArray = createTransactionByteArray(txnDetail, params.data)
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

    private fun createTransactionByteArray(txnDetail: KeyRegTransactionDetail, params: TransactionParams): ByteArray? {
        return if (txnDetail.isOnlineKeyRegTxn()) {
            buildKeyRegOnlineTransaction(txnDetail.toOnlineTxnPayload(params))
        } else {
            buildKeyRegOfflineTransaction(txnDetail.address, params)
        }
    }

    private fun createKeyRegTransactionResult(
        txnDetail: KeyRegTransactionDetail,
        txnByteArray: ByteArray
    ): KeyRegTransaction {
        return KeyRegTransaction(
            transactionByteArray = txnByteArray,
            accountAddress = txnDetail.address,
            accountAuthAddress = getAuthAddressOfAnAccount(txnDetail.address),
            isRekeyedToAnotherAccount = isSenderRekeyedToAnotherAccount(txnDetail.address)
        )
    }

    private fun KeyRegTransactionDetail.toOnlineTxnPayload(params: TransactionParams): OnlineKeyRegTransactionPayload {
        return OnlineKeyRegTransactionPayload(
            senderAddress = address,
            selectionPublicKey = selectionPublicKey.orEmpty(),
            voteKey = voteKey.orEmpty(),
            voteFirstRound = voteFirstRound.orEmpty(),
            voteLastRound = voteLastRound.orEmpty(),
            voteKeyDilution = voteKeyDilution.orEmpty(),
            txnParams = params
        )
    }

    private fun KeyRegTransactionDetail.isOnlineKeyRegTxn(): Boolean {
        return !voteKey.isNullOrBlank() && !selectionPublicKey.isNullOrBlank() && !voteFirstRound.isNullOrBlank() &&
            !voteLastRound.isNullOrBlank() && !voteKeyDilution.isNullOrBlank()
    }
}
