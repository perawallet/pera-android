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

package com.algorand.android.modules.assetinbox.detail.receivedetail.domain.usecase

import com.algorand.algosdk.sdk.Sdk
import com.algorand.android.BuildConfig
import com.algorand.android.models.Result
import com.algorand.android.models.TransactionParams
import com.algorand.android.modules.assetinbox.detail.receivedetail.domain.model.Arc59ClaimTransactionPayload
import com.algorand.android.modules.assetinbox.detail.receivedetail.domain.model.BaseArc59ClaimRejectTransaction.Arc59ClaimTransaction
import com.algorand.android.repository.TransactionsRepository
import com.algorand.android.usecase.AccountDetailUseCase
import com.algorand.android.usecase.IsOnTestnetUseCase
import com.algorand.android.utils.toSuggestedParams
import javax.inject.Inject

class CreateArc59ClaimTransactionUseCase @Inject constructor(
    private val accountDetailUseCase: AccountDetailUseCase,
    private val transactionsRepository: TransactionsRepository,
    private val isOnTestnetUseCase: IsOnTestnetUseCase
) : CreateArc59ClaimTransaction {

    override suspend fun invoke(payload: Arc59ClaimTransactionPayload): Result<List<Arc59ClaimTransaction>> {
        val isAccountRekeyed = accountDetailUseCase.isAccountRekeyed(payload.receiverAddress)
        val authAddress = accountDetailUseCase.getAuthAddress(payload.receiverAddress)
        return transactionsRepository.getTransactionParams().map { transactionParams ->
            createTransactions(payload, transactionParams).map { transactionByteArray ->
                Arc59ClaimTransaction(
                    transactionByteArray,
                    payload.receiverAddress,
                    authAddress,
                    isAccountRekeyed
                )
            }
        }
    }

    private fun isReceiverOptedInToAsset(address: String, assetId: Long): Boolean {
        return accountDetailUseCase.getCachedAccountDetail(address)?.data?.accountInformation?.hasAsset(assetId) == true
    }

    private fun createTransactions(
        payload: Arc59ClaimTransactionPayload,
        transactionParams: TransactionParams
    ): List<ByteArray> {
        val isTestnet = isOnTestnetUseCase.invoke()
        val appID = if (isTestnet) BuildConfig.ARC59_APP_ID_TESTNET else BuildConfig.ARC59_APP_ID_MAINNET
        val transactions = with(payload) {
            Sdk.makeARC59ClaimTxn(
                receiverAddress,
                inboxAccountAddress,
                appID,
                assetId,
                transactionParams.toSuggestedParams(),
                isReceiverOptedInToAsset(payload.receiverAddress, assetId),
                payload.isClaimingAlgo
            )
        }
        return mutableListOf<ByteArray>().apply {
            repeat(transactions.length().toInt()) { index ->
                add(transactions.get(index.toLong()))
            }
        }
    }
}
