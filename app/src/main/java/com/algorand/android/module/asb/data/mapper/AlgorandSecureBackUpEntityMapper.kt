package com.algorand.android.module.asb.data.mapper

import com.algorand.android.module.shareddb.asb.model.AlgorandSecureBackUpEntity

internal interface AlgorandSecureBackUpEntityMapper {
    operator fun invoke(encryptedAddress: String, isBackedUp: Boolean): AlgorandSecureBackUpEntity
}
