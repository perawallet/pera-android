package com.algorand.android.accountinfo.component.data.mapper.entity

import com.algorand.android.accountinfo.component.data.model.AccountInformationResponse
import com.algorand.android.encryption.EncryptionManager
import com.algorand.android.shared_db.accountinformation.model.AccountInformationEntity

internal class AccountInformationEntityMapperImpl(
    private val encryptionManager: EncryptionManager
) : AccountInformationEntityMapper {

    override fun invoke(response: AccountInformationResponse): AccountInformationEntity? {
        if (response.accountInformation?.address.isNullOrBlank()) return null
        return AccountInformationEntity(
            encryptedAddress = encryptionManager.encrypt(response.accountInformation?.address.orEmpty()),
            algoAmount = response.accountInformation?.amount ?: return null,
            lastFetchedRound = response.currentRound ?: return null,
            authAddress = response.accountInformation.rekeyAdminAddress,
            optedInAppsCount = response.accountInformation.totalAppsOptedIn ?: 0,
            totalCreatedAppsCount = response.accountInformation.totalCreatedApps ?: 0,
            totalCreatedAssetsCount = response.accountInformation.totalCreatedAssets ?: 0,
            appsTotalExtraPages = response.accountInformation.appsTotalExtraPages ?: 0,
            appStateNumByteSlice = response.accountInformation.appStateSchemaResponse?.numByteSlice,
            appStateSchemaUint = response.accountInformation.appStateSchemaResponse?.numUint,
            createdAtRound = response.accountInformation.createdAtRound
        )
    }
}
