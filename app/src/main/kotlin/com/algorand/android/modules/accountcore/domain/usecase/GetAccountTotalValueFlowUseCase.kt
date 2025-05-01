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
import com.algorand.android.modules.accounts.lite.domain.model.AccountLiteCacheStatus
import com.algorand.android.modules.accounts.lite.domain.usecase.GetAccountLiteCacheFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

internal class GetAccountTotalValueFlowUseCase @Inject constructor(
    private val getAccountLiteCacheFlow: GetAccountLiteCacheFlow,
    private val getAccountTotalValue: GetAccountTotalValue
) : GetAccountTotalValueFlow {

    override fun invoke(address: String, includeAlgo: Boolean): Flow<AccountTotalValue> {
        return getAccountLiteCacheFlow().mapNotNull {
            val accountLite = (it as? AccountLiteCacheStatus.Data)?.accountLites?.get(address) ?: return@mapNotNull null
            getAccountTotalValue(accountLite, includeAlgo)
        }
    }
}
