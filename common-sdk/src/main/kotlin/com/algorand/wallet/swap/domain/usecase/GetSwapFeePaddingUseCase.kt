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

import com.algorand.wallet.remoteconfig.data.service.FirebaseRemoteConfigService
import java.math.BigDecimal
import javax.inject.Inject

internal class GetSwapFeePaddingUseCase @Inject constructor(
    private val firebaseRemoteConfigService: FirebaseRemoteConfigService
) : GetSwapFeePadding {

    override fun invoke(): BigDecimal {
        val feePadding = firebaseRemoteConfigService.getDouble(SWAP_FEE_PADDING_KEY)
        return BigDecimal.valueOf(feePadding)
    }

    private companion object {
        const val SWAP_FEE_PADDING_KEY = "swap_fee_padding"
    }
}
