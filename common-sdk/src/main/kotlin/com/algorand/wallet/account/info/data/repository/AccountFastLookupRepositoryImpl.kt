/*
 * Copyright 2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.account.info.data.repository

import com.algorand.wallet.account.info.data.mapper.model.AccountFastLookupMapper
import com.algorand.wallet.account.info.data.service.AccountFastLookupApiService
import com.algorand.wallet.account.info.domain.model.AccountFastLookup
import com.algorand.wallet.account.info.domain.repository.AccountFastLookupRepository
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.foundation.network.utils.request
import javax.inject.Inject

internal class AccountFastLookupRepositoryImpl @Inject constructor(
    private val api: AccountFastLookupApiService,
    private val accountFastLookupMapper: AccountFastLookupMapper
) : AccountFastLookupRepository {

    override suspend fun fetchAccountFastLookup(accountAddress: String): PeraResult<AccountFastLookup> {
        return request { api.getAccountFastLookup(accountAddress) }
            .map { accountFastLookupMapper(it) }
    }
}
