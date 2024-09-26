package com.algorand.android.module.account.info.data.mapper.entity

import com.algorand.android.shared_db.accountinformation.model.AccountInformationEntity
import com.algorand.android.encryption.EncryptionManager
import java.math.BigInteger
import javax.inject.Inject

internal class AccountInformationErrorEntityMapperImpl @Inject constructor(
    private val deterministicEncryptionManager: EncryptionManager
) : AccountInformationErrorEntityMapper {
    override fun invoke(address: String): AccountInformationEntity {
        return AccountInformationEntity(
            encryptedAddress = deterministicEncryptionManager.encrypt(address),
            algoAmount = BigInteger.ZERO,
            optedInAppsCount = 0,
            appsTotalExtraPages = 0,
            authAddress = null,
            createdAtRound = null,
            lastFetchedRound = 0,
            totalCreatedAppsCount = 0,
            totalCreatedAssetsCount = 0,
            appStateNumByteSlice = null,
            appStateSchemaUint = null
        )
    }
}
