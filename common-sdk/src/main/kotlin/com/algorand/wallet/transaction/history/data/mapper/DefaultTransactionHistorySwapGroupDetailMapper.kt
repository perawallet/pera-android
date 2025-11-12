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

package com.algorand.wallet.transaction.history.data.mapper

import com.algorand.wallet.transaction.history.data.model.TransactionHistorySwapGroupDetailResponse
import com.algorand.wallet.transaction.history.domain.model.TransactionHistorySwapGroupDetail
import com.algorand.wallet.utils.date.TimeProvider
import com.algorand.wallet.utils.formatToBigDecimal
import javax.inject.Inject

internal class DefaultTransactionHistorySwapGroupDetailMapper @Inject constructor(
    private val transactionHistoryMapper: TransactionHistoryMapper,
    private val timeProvider: TimeProvider
) : TransactionHistorySwapGroupDetailMapper {

    override fun invoke(
        address: String,
        response: TransactionHistorySwapGroupDetailResponse
    ): TransactionHistorySwapGroupDetail? {
        return with(response) {
            TransactionHistorySwapGroupDetail(
                id = swapId ?: return null,
                groupId = groupId ?: return null,
                provider = provider ?: return null,
                status = status ?: return null,
                assetInId = assetIn?.id ?: return null,
                assetInUnitName = assetIn.unitName.orEmpty(),
                amountIn = amountInWithSlippage.formatToBigDecimal(assetIn.decimals) ?: return null,
                assetOutId = assetOut?.id ?: return null,
                assetOutUnitName = assetOut.unitName.orEmpty(),
                amountOut = amountOutWithSlippage.formatToBigDecimal(assetOut.decimals) ?: return null,
                transactions = transactions?.mapNotNull { transactionHistoryMapper(address, it) } ?: return null,
                confirmedRound = confirmedRound,
                time = timeProvider.getZonedDateTimeFromSeconds(roundTime ?: return null)
            )
        }
    }
}
