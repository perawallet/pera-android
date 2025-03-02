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

package com.algorand.android.usecase

import com.algorand.android.mapper.AccountAssetDataMapper
import com.algorand.android.models.AccountDetail
import com.algorand.android.models.AccountInformation
import com.algorand.android.models.AssetDetail
import com.algorand.android.models.AssetStatus.OWNED_BY_ACCOUNT
import com.algorand.android.models.AssetStatus.PENDING_FOR_ADDITION
import com.algorand.android.models.AssetStatus.PENDING_FOR_REMOVAL
import com.algorand.android.models.AssetStatus.PENDING_FOR_SENDING
import com.algorand.android.models.BaseAccountAssetData
import com.algorand.android.models.BaseAccountAssetData.BaseOwnedAssetData.OwnedAssetData
import com.algorand.android.utils.extensions.getAssetHoldingList
import javax.inject.Inject
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest

class AccountAssetDataUseCase @Inject constructor(
    private val accountDetailUseCase: AccountDetailUseCase,
    private val accountAssetAmountUseCase: AccountAssetAmountUseCase,
    private val accountAlgoAmountUseCase: AccountAlgoAmountUseCase,
    private val accountAssetDataMapper: AccountAssetDataMapper
) {

    fun getNonCachedAccountAssetData(accountDetail: AccountDetail, includeAlgo: Boolean): List<OwnedAssetData> {
        return createNonCachedAccountAssetData(accountDetail, includeAlgo)
    }

    fun getAccountOwnedAssetData(publicKey: String, includeAlgo: Boolean): List<OwnedAssetData> {
        val accountDetail = accountDetailUseCase.getCachedAccountDetail(publicKey)?.data ?: return emptyList()
        return createAccountOwnedAssetData(accountDetail, includeAlgo)
    }

    fun fetchAccountOwnedAssetData(
        publicKey: String,
        includeAlgo: Boolean
    ) = channelFlow<List<OwnedAssetData>> {
        accountDetailUseCase.fetchAndCacheAccountDetail(publicKey).collectLatest {
            send(getAccountOwnedAssetData(publicKey, includeAlgo))
        }
    }

    private fun createAccountOwnedAssetData(account: AccountDetail, includeAlgo: Boolean): List<OwnedAssetData> {
        val accountOwnedAssetList = getAccountOwnedCachedAssetList(account)
        return createAssetDataList(account, includeAlgo, accountOwnedAssetList).filterIsInstance<OwnedAssetData>()
    }

    private fun createAssetDataList(
        account: AccountDetail,
        includeAlgo: Boolean,
        cachedAssetList: List<AssetDetail>
    ): List<BaseAccountAssetData> {
        val assetDataList = mutableListOf<BaseAccountAssetData>()
        if (includeAlgo) assetDataList.add(accountAlgoAmountUseCase.getAccountAlgoAmount(account.account.address))
        account.getAssetHoldingList().forEach { assetHolding ->
            cachedAssetList.firstOrNull { it.assetId == assetHolding.assetId }?.let { assetItem ->
                val accountAssetData = when (assetHolding.status) {
                    OWNED_BY_ACCOUNT -> accountAssetAmountUseCase.getAssetAmount(assetHolding, assetItem)
                    // We shouldn't show pending state if sending asset is ASA
                    PENDING_FOR_SENDING -> accountAssetAmountUseCase.getAssetAmount(assetHolding, assetItem)
                    PENDING_FOR_REMOVAL -> accountAssetDataMapper.mapToPendingRemovalAssetData(assetItem)
                    PENDING_FOR_ADDITION -> accountAssetDataMapper.mapToPendingAdditionAssetData(assetItem)
                }
                assetDataList.add(accountAssetData)
            }
        }
        return assetDataList
    }

    @Deprecated("Cache is always empty")
    private fun getAccountOwnedCachedAssetList(account: AccountDetail): List<AssetDetail> {
//        return assetDetailUseCase.getCachedAssetDetail(getAccountOwnedAssetIdList(account)).mapNotNull { it.data }
        return emptyList()
    }

    private fun getAccountOwnedAssetIdList(account: AccountDetail): List<Long> {
        return account.getAssetHoldingList().mapNotNull { assetHolding ->
            assetHolding.assetId.takeIf { assetHolding.status == OWNED_BY_ACCOUNT }
        }
    }

    private fun createNonCachedAccountAssetData(account: AccountDetail, includeAlgo: Boolean): List<OwnedAssetData> {
        return mutableListOf<OwnedAssetData>().apply {
            if (includeAlgo) {
                add(accountAlgoAmountUseCase.getAccountAlgoAmount(account))
            }
            addAll(createAccountOtherAssetsData(account.accountInformation))
        }
    }

    private fun createAccountOtherAssetsData(accountInformation: AccountInformation): List<OwnedAssetData> {
        val cachedAssetList = getCachedAssetList(accountInformation)
        return mutableListOf<OwnedAssetData>().apply {
            accountInformation.getAssetHoldingList().forEach { assetHolding ->
                cachedAssetList.firstOrNull { it.assetId == assetHolding.assetId }?.let { assetItem ->
                    val accountAssetData = accountAssetAmountUseCase.getAssetAmount(assetHolding, assetItem)
                    add(accountAssetData)
                }
            }
        }
    }

    @Deprecated("Cache is always empty")
    private fun getCachedAssetList(accountInformation: AccountInformation): List<AssetDetail> {
        val accountAssetHoldingList = accountInformation.getAllAssetIds()
//        return assetDetailUseCase.getCachedAssetDetail(accountAssetHoldingList).mapNotNull { it.data }
        return emptyList()
    }
}
