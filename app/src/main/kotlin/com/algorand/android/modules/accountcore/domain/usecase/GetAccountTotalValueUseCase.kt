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

package com.algorand.android.modules.accountcore.domain.usecase

import com.algorand.android.modules.accountcore.domain.model.AccountTotalValue
import com.algorand.android.modules.accounts.lite.domain.model.AccountLite
import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLite
import java.math.BigDecimal
import javax.inject.Inject

internal class GetAccountTotalValueUseCase @Inject constructor(
    private val getAccountLite: GetAccountLite
) : GetAccountTotalValue {

    override suspend fun invoke(address: String, includeAlgo: Boolean): AccountTotalValue {
        val accountLite = getAccountLite(address)
        return invoke(accountLite, includeAlgo)
    }

    override suspend fun invoke(accountLite: AccountLite?, includeAlgo: Boolean): AccountTotalValue {
        val cachedInfo = accountLite?.cachedInfo
        return if (cachedInfo != null) {
            AccountTotalValue(
                primaryAccountValue = getPrimaryAccountValue(cachedInfo, includeAlgo),
                secondaryAccountValue = getSecondaryAccountValue(cachedInfo, includeAlgo),
                assetCount = if (includeAlgo) cachedInfo.assetCount + 1 else cachedInfo.assetCount,
            )
        } else {
            getDefaultAccountValue()
        }
    }

    private fun getPrimaryAccountValue(cachedInfo: AccountLite.CachedInfo, includeAlgo: Boolean): BigDecimal {
        return if (includeAlgo) {
            cachedInfo.primaryAccountValue
        } else {
            cachedInfo.getPrimaryAccountValueWithoutAlgo()
        }
    }

    private fun getSecondaryAccountValue(cachedInfo: AccountLite.CachedInfo, includeAlgo: Boolean): BigDecimal {
        return if (includeAlgo) {
            cachedInfo.secondaryAccountValue
        } else {
            cachedInfo.getSecondaryAccountValueWithoutAlgo()
        }
    }

    private fun getDefaultAccountValue(): AccountTotalValue {
        return AccountTotalValue(BigDecimal.ZERO, BigDecimal.ZERO, 0)
    }
}
