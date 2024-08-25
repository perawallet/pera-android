package com.algorand.android.custominfo.component.data.mapper.model

import com.algorand.android.custominfo.component.domain.model.CustomInfo
import com.algorand.android.encryption.EncryptionManager
import com.algorand.android.shared_db.assetdetail.model.CustomInfoEntity

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
