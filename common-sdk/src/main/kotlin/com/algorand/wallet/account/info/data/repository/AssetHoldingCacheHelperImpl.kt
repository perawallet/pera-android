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

import com.algorand.wallet.account.info.data.database.dao.AssetHoldingDao
import com.algorand.wallet.account.info.data.database.model.AssetHoldingEntity
import com.algorand.wallet.account.info.data.database.model.AssetStatusEntity
import com.algorand.wallet.account.info.data.mapper.entity.AssetHoldingEntityMapper
import com.algorand.wallet.account.info.data.mapper.model.AssetHoldingMapper
import com.algorand.wallet.account.info.data.model.AssetHoldingResponse
import com.algorand.wallet.account.info.domain.model.AssetHolding
import com.algorand.wallet.account.info.domain.model.AssetStatus
import javax.inject.Inject

internal class AssetHoldingCacheHelperImpl @Inject constructor(
    private val assetHoldingDao: AssetHoldingDao,
    private val assetHoldingEntityMapper: AssetHoldingEntityMapper,
    private val assetHoldingMapper: AssetHoldingMapper
) : AssetHoldingCacheHelper {

    override suspend fun cacheAssetHolding(
        address: String,
        assetHoldings: List<AssetHoldingResponse>
    ): List<AssetHolding> {
        val updatedAssetHoldingEntities = getUpdatedAssetHoldings(address, assetHoldings)
        assetHoldingDao.updateAssetHoldings(address, updatedAssetHoldingEntities)
        return assetHoldingMapper(updatedAssetHoldingEntities)
    }

    private suspend fun getUpdatedAssetHoldings(
        address: String,
        assetHoldings: List<AssetHoldingResponse>
    ): List<AssetHoldingEntity> {
        val updatedAssetHoldings = mutableListOf<AssetHoldingEntity>()
        val cachedAssetHoldings = assetHoldingDao.getAssetsByAddress(address).toMutableList()

        assetHoldings.forEach { response ->
            val cachedAssetIndex = cachedAssetHoldings.indexOfFirst { it.assetId == response.assetId }

            val isNewAsset = cachedAssetIndex == -1
            if (isNewAsset) {
                assetHoldingEntityMapper(address, response, AssetStatus.OWNED_BY_ACCOUNT)?.let {
                    updatedAssetHoldings.add(it)
                }
                return@forEach
            }

            val cachedAsset = cachedAssetHoldings[cachedAssetIndex]
            val updatedStatus = getUpdatedAssetStatus(cachedAsset.assetStatusEntity)
            assetHoldingEntityMapper(address, response, updatedStatus)?.let {
                updatedAssetHoldings.add(it)
                cachedAssetHoldings.removeAt(cachedAssetIndex)
            }
        }

        cachedAssetHoldings.forEach {
            if (it.assetStatusEntity == AssetStatusEntity.PENDING_FOR_ADDITION) {
                updatedAssetHoldings.add(it)
            }
        }
        return updatedAssetHoldings
    }

    private fun getUpdatedAssetStatus(currentStatus: AssetStatusEntity): AssetStatus {
        return when (currentStatus) {
            AssetStatusEntity.PENDING_FOR_ADDITION -> AssetStatus.OWNED_BY_ACCOUNT
            AssetStatusEntity.PENDING_FOR_REMOVAL -> AssetStatus.PENDING_FOR_REMOVAL
            AssetStatusEntity.OWNED_BY_ACCOUNT -> AssetStatus.OWNED_BY_ACCOUNT
        }
    }
}
