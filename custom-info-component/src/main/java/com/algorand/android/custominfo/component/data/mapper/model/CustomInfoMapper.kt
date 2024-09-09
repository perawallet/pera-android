package com.algorand.android.custominfo.component.data.mapper.model

import com.algorand.android.custominfo.component.domain.model.CustomInfo
import com.algorand.android.shared_db.assetdetail.model.CustomInfoEntity

internal interface CustomInfoMapper {
    operator fun invoke(address: String, customInfoEntity: CustomInfoEntity?): CustomInfo
}
