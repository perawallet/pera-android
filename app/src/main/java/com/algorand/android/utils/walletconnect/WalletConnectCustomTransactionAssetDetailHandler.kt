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

package com.algorand.android.utils.walletconnect

import com.algorand.android.assetdetail.component.asset.domain.model.detail.Asset
import com.algorand.android.assetdetail.component.asset.domain.usecase.FetchAssetDetailFromNode
import com.algorand.android.assetdetail.component.asset.domain.usecase.FetchAssets
import com.algorand.android.assetdetail.component.asset.domain.usecase.GetAsset
import com.algorand.android.mapper.WalletConnectTransactionAssetDetailMapper
import com.algorand.android.models.WalletConnectTransactionAssetDetail
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

class WalletConnectCustomTransactionAssetDetailHandler @Inject constructor(
    private val walletConnectTransactionAssetDetailMapper: WalletConnectTransactionAssetDetailMapper,
    private val getAsset: GetAsset,
    private val fetchAssets: FetchAssets,
    private val fetchAssetDetailFromNode: FetchAssetDetailFromNode
) {

    /**
     * Stores asset detail that wallet connect request contains
     * to fasten the process for requests that contains same asset
     */
    private val assetCacheMap = mutableMapOf<Long, WalletConnectTransactionAssetDetail>()

    suspend fun getAssetParamsDefinedWCTransactionList(
        assetIdList: List<Long>,
        scope: CoroutineScope
    ): Map<Long, WalletConnectTransactionAssetDetail?> {
        val assetIdSet = assetIdList.toSet()
        val assetIdToBeFetched = checkAssetsInLocalCacheAndReturnNonExistingIds(assetIdSet)
        fetchAssetsFromIndexerAndUpdateCache(assetIdToBeFetched, scope)
        return assetCacheMap
    }

    private suspend fun checkAssetsInLocalCacheAndReturnNonExistingIds(assetIdSet: Set<Long>): List<Long> {
        return assetIdSet.mapNotNull { assetId ->
            val assetInWalletConnectCache = assetCacheMap.getOrDefault(assetId, null)
            if (assetInWalletConnectCache != null) return@mapNotNull null
            getAssetDetailIfAvailableInAssetDetailCache(assetId)?.let { cachedAssetDetail ->
                assetCacheMap[assetId] = cachedAssetDetail
                return@mapNotNull null
            }
            getAssetDetailIfAvailableInAssetDetailCache(assetId)?.let { cachedNftDetail ->
                assetCacheMap[assetId] = cachedNftDetail
                return@mapNotNull null
            }
            assetId
        }
    }

    private suspend fun fetchAssetsFromIndexerAndUpdateCache(assetIdList: List<Long>, scope: CoroutineScope) {
        val chunkedAssetIds = assetIdList.toSet().chunked(MAX_ASSET_TO_FETCH)
        chunkedAssetIds.map { assetIdChunk ->
            scope.async {
                fetchAssets(assetIdChunk).use(
                    onSuccess = { baseAssetDetails ->
                        baseAssetDetails.map { assetDetail ->
                            assetCacheMap[assetDetail.id] = mapAssetDetailToWcAssetDetail(assetDetail)
                        }
                    },
                    onFailed = { _, _ ->
                        fetchAssetsFromNodeAndUpdateCache(assetIdChunk, scope)
                    }
                )
            }
        }.awaitAll()
    }

    private suspend fun fetchAssetsFromNodeAndUpdateCache(assetIdList: List<Long>, scope: CoroutineScope) {
        assetIdList.map { assetId ->
            scope.async {
                fetchAssetDetailFromNode.invoke(assetId).use(
                    onSuccess = { assetDetail ->
                        assetCacheMap[assetId] = mapAssetDetailToWcAssetDetail(assetDetail)
                    },
                    onFailed = { _, _ ->
                        // Do nothing
                    }
                )
            }
        }.awaitAll()
    }

    fun clearAssetCacheMap() {
        assetCacheMap.clear()
    }

    private suspend fun getAssetDetailIfAvailableInAssetDetailCache(
        assetId: Long
    ): WalletConnectTransactionAssetDetail? {
        val cachedAssetDetail = getAsset(assetId)
        return with(cachedAssetDetail ?: return null) {
            walletConnectTransactionAssetDetailMapper.mapToWalletConnectTransactionAssetDetail(
                assetId = assetId,
                fullName = assetInfo?.name?.fullName,
                shortName = assetInfo?.name?.shortName,
                fractionDecimals = assetInfo?.decimals,
                verificationTier = verificationTier
            )
        }
    }

    private fun mapAssetDetailToWcAssetDetail(assetDetail: Asset): WalletConnectTransactionAssetDetail {
        return with(assetDetail) {
            walletConnectTransactionAssetDetailMapper.mapToWalletConnectTransactionAssetDetail(
                assetId = id,
                fullName = this.assetInfo?.name?.fullName,
                shortName = this.assetInfo?.name?.shortName,
                fractionDecimals = this.assetInfo?.decimals,
                verificationTier = verificationTier
            )
        }
    }

    private companion object {
        const val MAX_ASSET_TO_FETCH = 100
    }
}
