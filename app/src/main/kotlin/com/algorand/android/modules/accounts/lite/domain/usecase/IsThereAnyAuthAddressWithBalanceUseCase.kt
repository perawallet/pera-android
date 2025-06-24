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

package com.algorand.android.modules.accounts.lite.domain.usecase

import com.algorand.android.utils.isGreaterThan
import com.algorand.wallet.account.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.wallet.account.info.domain.usecase.IsThereAnyAuthAddressWithBalance
import java.math.BigDecimal
import javax.inject.Inject

internal class IsThereAnyAuthAddressWithBalanceUseCase @Inject constructor(
    private val getAccountLiteCacheData: GetAccountLiteCacheData
) : IsThereAnyAuthAddressWithBalance {

    override suspend fun invoke(): Boolean {
        val accountLiteCacheData = getAccountLiteCacheData() ?: return false
        return accountLiteCacheData.accountLites.any {
            val cachedInfo = it.value.cachedInfo ?: return@any false
            cachedInfo.type.canSignTransaction() && cachedInfo.primaryAccountValue.isGreaterThan(BigDecimal.ZERO)
        }
    }
}
