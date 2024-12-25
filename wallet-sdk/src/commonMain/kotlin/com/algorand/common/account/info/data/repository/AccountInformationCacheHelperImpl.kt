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

package com.algorand.common.account.info.data.repository

import com.algorand.common.account.info.data.database.dao.AccountInformationDao
import com.algorand.common.account.info.data.database.model.AccountInformationEntity
import com.algorand.common.account.info.data.mapper.AccountInformationEntityMapper
import com.algorand.common.account.info.data.mapper.AccountInformationErrorEntityMapper
import com.algorand.common.account.info.data.mapper.AccountInformationMapper
import com.algorand.common.account.info.data.model.AccountInformationResponse
import com.algorand.common.account.info.domain.model.AccountInformation
import com.algorand.common.account.info.domain.model.AssetHolding

internal class AccountInformationCacheHelperImpl (
    private val accountInformationEntityMapper: AccountInformationEntityMapper,
    private val accountInformationMapper: AccountInformationMapper,
    private val accountInformationDao: AccountInformationDao,
    private val accountInformationErrorEntityMapper: AccountInformationErrorEntityMapper,
    private val assetHoldingCacheHelper: AssetHoldingCacheHelper
) : AccountInformationCacheHelper {

    override suspend fun cacheAccountInformation(
        address: String,
        response: AccountInformationResponse
    ): AccountInformation? {
        val entity = accountInformationEntityMapper(response)
        return if (entity != null) {
            val assetHoldings =
                assetHoldingCacheHelper.cacheAssetHolding(address, response.accountInformation?.allAssetHoldingList)
            cacheAccountInformation(entity, assetHoldings)
        } else {
            cacheErrorAccountInformation(address)
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

    private suspend fun cacheErrorAccountInformation(address: String) {
        val errorEntity = accountInformationErrorEntityMapper(address)
        accountInformationDao.insert(errorEntity)
    }
}
