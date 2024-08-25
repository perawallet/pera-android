package com.algorand.android.asb.component.data.mapper

import com.algorand.android.shared_db.asb.model.AlgorandSecureBackUpEntity
import javax.inject.Inject

internal class AlgorandSecureBackUpEntityMapperImpl @Inject constructor() : AlgorandSecureBackUpEntityMapper {

    override fun invoke(encryptedAddress: String, isBackedUp: Boolean): AlgorandSecureBackUpEntity {
        return AlgorandSecureBackUpEntity(encryptedAddress = encryptedAddress, isBackedUp = isBackedUp)
    }
}
