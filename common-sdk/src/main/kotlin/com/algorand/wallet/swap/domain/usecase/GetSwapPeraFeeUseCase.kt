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

package com.algorand.wallet.swap.domain.usecase

import com.algorand.wallet.asset.domain.util.AssetConstants.ALGO_DECIMALS
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.swap.domain.repository.SwapRepository
import java.math.BigDecimal
import javax.inject.Inject

internal class GetSwapPeraFeeUseCase @Inject constructor(private val swapRepository: SwapRepository) : GetSwapPeraFee {

    override suspend fun invoke(assetInId: Long, amount: BigDecimal, fractionDecimals: Int): PeraResult<BigDecimal> {
        val amountAsMicro = amount.movePointRight(fractionDecimals).toBigInteger()
        return swapRepository.getPeraFee(assetInId, amountAsMicro).map {
            it.fee?.toBigDecimal()?.movePointLeft(ALGO_DECIMALS) ?: BigDecimal.ZERO
        }
    }
}
