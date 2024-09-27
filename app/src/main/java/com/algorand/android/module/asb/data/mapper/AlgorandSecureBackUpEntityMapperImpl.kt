package com.algorand.android.module.asb.data.mapper

import com.algorand.android.module.shareddb.asb.model.AlgorandSecureBackUpEntity
import javax.inject.Inject

internal class AlgorandSecureBackUpEntityMapperImpl @Inject constructor() : AlgorandSecureBackUpEntityMapper {

    override fun invoke(encryptedAddress: String, isBackedUp: Boolean): AlgorandSecureBackUpEntity {
        return AlgorandSecureBackUpEntity(encryptedAddress = encryptedAddress, isBackedUp = isBackedUp)
    }
}
