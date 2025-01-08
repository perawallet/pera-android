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

package com.algorand.android.modules.currency.domain.usecase

import com.algorand.android.modules.parity.domain.repository.ParityRepository
import com.algorand.android.utils.CacheResult.Success
import javax.inject.Inject

internal class GetPrimaryCurrencyNameUseCase @Inject constructor(
    private val parityRepository: ParityRepository,
    private val getPrimaryCurrencyId: GetPrimaryCurrencyId
) : GetPrimaryCurrencyName {

    override fun invoke(): String {
        val currencySymbol = (parityRepository.getCachedSelectedCurrencyDetail() as? Success)?.data?.currencySymbol
        return currencySymbol ?: getPrimaryCurrencyId()
    }
}
