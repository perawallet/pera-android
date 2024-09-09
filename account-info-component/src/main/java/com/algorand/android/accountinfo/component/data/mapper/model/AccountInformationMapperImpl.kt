package com.algorand.android.accountinfo.component.data.mapper.model

import com.algorand.android.accountinfo.component.data.model.AccountInformationResponse
import com.algorand.android.accountinfo.component.data.model.AccountInformationResponsePayloadResponse
import com.algorand.android.accountinfo.component.data.model.RekeyedAccountsResponse
import com.algorand.android.accountinfo.component.domain.model.AccountInformation
import com.algorand.android.accountinfo.component.domain.model.AssetHolding
import com.algorand.android.encryption.EncryptionManager
import com.algorand.android.shared_db.accountinformation.model.AccountInformationEntity

internal class AccountInformationMapperImpl(
    private val appStateSchemeMapper: AppStateSchemeMapper,
    private val assetHoldingMapper: AssetHoldingMapper,
    private val encryptionManager: EncryptionManager
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
            address = encryptionManager.decrypt(entity.encryptedAddress),
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
