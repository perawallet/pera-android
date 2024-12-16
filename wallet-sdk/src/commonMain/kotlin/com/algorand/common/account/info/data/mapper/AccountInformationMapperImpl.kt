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

package com.algorand.common.account.info.data.mapper

import com.algorand.common.account.info.data.database.model.AccountInformationEntity
import com.algorand.common.account.info.data.model.AccountInformationResponse
import com.algorand.common.account.info.data.model.AccountInformationResponsePayloadResponse
import com.algorand.common.account.info.data.model.RekeyedAccountsResponse
import com.algorand.common.account.info.domain.model.AccountInformation
import com.algorand.common.account.info.domain.model.AssetHolding
import com.algorand.common.encryption.AddressEncryptionManager

internal class AccountInformationMapperImpl(
    private val appStateSchemeMapper: AppStateSchemeMapper,
    private val assetHoldingMapper: AssetHoldingMapper,
    private val addressEncryptionManager: AddressEncryptionManager
) : AccountInformationMapper {

    override fun invoke(response: AccountInformationResponse): AccountInformation? {
        if (response.accountInformation == null) return null
        return mapToAccountInformation(response.accountInformation, response.currentRound ?: return null)
    }

    override fun invoke(response: RekeyedAccountsResponse): List<AccountInformation> {
        return response.accountInformationList?.mapNotNull {
            mapToAccountInformation(it, response.currentRound ?: return emptyList())
        }.orEmpty()
    }

    private fun mapToAccountInformation(
        response: AccountInformationResponsePayloadResponse,
        currentRound: Long
    ): AccountInformation? {
        val assetHoldingList = response.allAssetHoldingList.orEmpty().map {
            assetHoldingMapper(it)
        }
        if (assetHoldingList.any { it == null }) return null
        return AccountInformation(
            address = response.address ?: return null,
            amount = response.amount ?: return null,
            lastFetchedRound = currentRound,
            rekeyAdminAddress = response.rekeyAdminAddress,
            totalAppsOptedIn = response.totalAppsOptedIn ?: 0,
            totalAssetsOptedIn = response.totalAssetsOptedIn ?: 0,
            totalCreatedApps = response.totalCreatedApps ?: 0,
            totalCreatedAssets = response.totalCreatedAssets ?: 0,
            appsTotalExtraPages = response.appsTotalExtraPages ?: 0,
            appsTotalSchema = appStateSchemeMapper(response.appStateSchemaResponse),
            assetHoldings = assetHoldingList.filterNotNull(),
            createdAtRound = response.createdAtRound
        )
    }

    override fun invoke(entity: AccountInformationEntity, assetHoldingList: List<AssetHolding>): AccountInformation {
        return AccountInformation(
            address = addressEncryptionManager.decrypt(entity.encryptedAddress),
            amount = entity.algoAmount,
            lastFetchedRound = entity.lastFetchedRound,
            rekeyAdminAddress = entity.authAddress,
            totalAppsOptedIn = entity.optedInAppsCount,
            totalAssetsOptedIn = assetHoldingList.size,
            totalCreatedApps = entity.totalCreatedAppsCount,
            totalCreatedAssets = entity.totalCreatedAssetsCount,
            appsTotalExtraPages = entity.appsTotalExtraPages,
            appsTotalSchema = appStateSchemeMapper(entity.appStateNumByteSlice, entity.appStateSchemaUint),
            assetHoldings = assetHoldingList,
            createdAtRound = entity.createdAtRound
        )
    }
}
