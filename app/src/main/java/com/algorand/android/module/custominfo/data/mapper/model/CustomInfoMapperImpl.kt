package com.algorand.android.module.custominfo.data.mapper.model

import com.algorand.android.module.custominfo.domain.model.CustomInfo
import com.algorand.android.module.encryption.EncryptionManager
import com.algorand.android.module.shareddb.assetdetail.model.CustomInfoEntity

internal class CustomInfoMapperImpl(
    private val encryptionManager: EncryptionManager
) : CustomInfoMapper {

    override fun invoke(address: String, customInfoEntity: CustomInfoEntity?): CustomInfo {
        return CustomInfo(
            address = address,
            customName = customInfoEntity?.customName
        )
    }
}
