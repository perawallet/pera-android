/*
 *  Copyright 2022 Pera Wallet, LDA
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.algorand.android.modules.assetinbox.send.domain.usecase

import com.algorand.algosdk.sdk.Sdk
import com.algorand.android.BuildConfig
import com.algorand.android.models.TransactionParams
import com.algorand.android.modules.assetinbox.send.domain.model.Arc59Transaction.Arc59SendTransaction
import com.algorand.android.modules.assetinbox.send.domain.model.Arc59TransactionPayload
import com.algorand.android.usecase.AccountDetailUseCase
import com.algorand.android.usecase.IsOnTestnetUseCase
import com.algorand.android.utils.toSuggestedParams
import com.algorand.android.utils.toUint64
import javax.inject.Inject

class CreateArc59SendTransactionUseCase @Inject constructor(
    private val accountDetailUseCase: AccountDetailUseCase,
    private val isOnTestnetUseCase: IsOnTestnetUseCase
) : CreateArc59SendTransaction {

    override fun invoke(txnParams: TransactionParams, payload: Arc59TransactionPayload): List<Arc59SendTransaction> {
        val senderAuthAddress = accountDetailUseCase.getAuthAddress(payload.senderAddress)
        val isSenderRekeyedToAnotherAccount = accountDetailUseCase.isAccountRekeyed(payload.senderAddress)
        val transactions = txnParams.createTransactions(payload)
        return transactions.map { transactionByteArray ->
            Arc59SendTransaction(
                transactionByteArray,
                payload.senderAddress,
                senderAuthAddress,
                isSenderRekeyedToAnotherAccount,
            )
        }
    }

    private fun TransactionParams.createTransactions(payload: Arc59TransactionPayload): MutableList<ByteArray> {
        val isTestnet = isOnTestnetUseCase.invoke()
        val appAddress = if (isTestnet) BuildConfig.ARC59_APP_ADDR_TESTNET else BuildConfig.ARC59_APP_ADDR_MAINNET
        val appId = if (isTestnet) BuildConfig.ARC59_APP_ID_TESTNET else BuildConfig.ARC59_APP_ID_MAINNET
        val transactions = with(payload) {
            Sdk.makeARC59SendTxn(
                senderAddress,
                receiverAddress,
                appAddress,
                inboxAddress,
                assetAmount.toUint64(),
                minimumBalanceRequirement.toUint64(),
                innerTxCount.toLong(),
                appId,
                assetId,
                toSuggestedParams(),
                payload.algoFundAmount.toUint64()
            )
        }
        return mutableListOf<ByteArray>().apply {
            repeat(transactions.length().toInt()) { index ->
                add(transactions.get(index.toLong()))
            }
        }
    }
}
