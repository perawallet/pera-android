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

package com.algorand.wallet.swap.data.mapper

import com.algorand.wallet.swap.data.model.SwapHistoryResponse
import com.algorand.wallet.swap.domain.model.SwapHistory
import com.algorand.wallet.utils.date.parser.DateTimeParser

internal class DefaultSwapHistoryMapper(
    private val dateTimeParser: DateTimeParser
) : SwapHistoryMapper {

    override fun invoke(response: SwapHistoryResponse): SwapHistory? {
        return SwapHistory(
            id = response.id ?: return null,
            assetInDetail = SwapHistory.AssetDetail(
                shortName = response.assetIn?.shortName,
                logoUrl = response.assetIn?.logoUrl,
                decimal = response.assetIn?.fractionDecimals ?: 0,
                amount = response.amountIn ?: return null
            ),
            assetOutDetail = SwapHistory.AssetDetail(
                shortName = response.assetOut?.shortName,
                logoUrl = response.assetOut?.logoUrl,
                decimal = response.assetOut?.fractionDecimals ?: 0,
                amount = response.amountOut ?: return null
            ),
            datetime = dateTimeParser.parseOffsetDateTime(response.completedDatetime.orEmpty()),
            txnGroupId = response.txnGroupId
        )
    }
}
