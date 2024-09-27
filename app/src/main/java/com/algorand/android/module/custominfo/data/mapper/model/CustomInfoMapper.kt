package com.algorand.android.module.custominfo.data.mapper.model

import com.algorand.android.module.custominfo.domain.model.CustomInfo
import com.algorand.android.module.shareddb.assetdetail.model.CustomInfoEntity

internal interface CustomInfoMapper {
    operator fun invoke(address: String, customInfoEntity: CustomInfoEntity?): CustomInfo
}
