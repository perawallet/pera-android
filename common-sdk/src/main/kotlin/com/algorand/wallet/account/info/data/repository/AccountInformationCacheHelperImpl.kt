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

import com.algorand.wallet.account.info.data.cache.AccountInformationErrorCache
import com.algorand.wallet.account.info.data.database.dao.AccountInformationDao
import com.algorand.wallet.account.info.data.database.model.AccountInformationEntity
import com.algorand.wallet.account.info.data.mapper.entity.AccountInformationEntityMapper
import com.algorand.wallet.account.info.data.mapper.model.AccountInformationMapper
import com.algorand.wallet.account.info.data.model.AccountInformationResponse
import com.algorand.wallet.account.info.domain.model.AccountInformation
import com.algorand.wallet.account.info.domain.model.AssetHolding
import javax.inject.Inject

internal class AccountInformationCacheHelperImpl @Inject constructor(
    private val accountInformationEntityMapper: AccountInformationEntityMapper,
    private val accountInformationMapper: AccountInformationMapper,
    private val accountInformationDao: AccountInformationDao,
    private val assetHoldingCacheHelper: AssetHoldingCacheHelper,
    private val accountInformationErrorCache: AccountInformationErrorCache
) : AccountInformationCacheHelper {

    override suspend fun cacheAccountInformation(
        address: String,
        response: AccountInformationResponse
    ): AccountInformation? {
        val entity = accountInformationEntityMapper(response)
        return if (entity != null) {
            val assetHoldings = assetHoldingCacheHelper.cacheAssetHolding(
                address,
                response.accountInformation?.allAssetHoldingList.orEmpty()
            )
            accountInformationErrorCache.remove(address)
            cacheAccountInformation(entity, assetHoldings)
        } else {
            if (!accountInformationDao.isAddressExists(address)) {
                accountInformationErrorCache.put(address)
            }
            null
        }
    }

    private suspend fun cacheAccountInformation(
        entity: AccountInformationEntity,
        assetHoldings: List<AssetHolding>
    ): AccountInformation {
        accountInformationDao.insert(entity)
        return accountInformationMapper(entity, assetHoldings)
    }
}
