package com.algorand.android.custominfo.component.data.mapper.entity

import com.algorand.android.custominfo.component.domain.model.CustomInfo
import com.algorand.android.encryption.EncryptionManager
import com.algorand.android.shared_db.assetdetail.model.CustomInfoEntity

internal class CustomInfoEntityMapperImpl(
    private val encryptionManager: EncryptionManager
) : CustomInfoEntityMapper {

    override fun invoke(customInfo: CustomInfo): CustomInfoEntity {
        return CustomInfoEntity(
            encryptedAddress = encryptionManager.encrypt(customInfo.address),
            customName = customInfo.customName
        )
    }
}
