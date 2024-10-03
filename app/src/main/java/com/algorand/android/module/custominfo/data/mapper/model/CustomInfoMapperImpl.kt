package com.algorand.android.module.custominfo.data.mapper.model

import com.algorand.android.module.custominfo.domain.model.CustomInfo
import com.algorand.android.module.shareddb.assetdetail.model.CustomInfoEntity
import javax.inject.Inject

internal class CustomInfoMapperImpl @Inject constructor() : CustomInfoMapper {

    override fun invoke(address: String, customInfoEntity: CustomInfoEntity?): CustomInfo {
        return CustomInfo(
            address = address,
            customName = customInfoEntity?.customName
        )
    }
}
