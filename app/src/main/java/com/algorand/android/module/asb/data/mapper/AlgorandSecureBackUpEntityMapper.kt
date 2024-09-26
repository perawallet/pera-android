package com.algorand.android.module.asb.data.mapper

import com.algorand.android.shared_db.asb.model.AlgorandSecureBackUpEntity

internal interface AlgorandSecureBackUpEntityMapper {
    operator fun invoke(encryptedAddress: String, isBackedUp: Boolean): AlgorandSecureBackUpEntity
}
