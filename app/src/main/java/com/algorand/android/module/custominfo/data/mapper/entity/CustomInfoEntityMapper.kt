package com.algorand.android.module.custominfo.data.mapper.entity

import com.algorand.android.module.custominfo.domain.model.CustomInfo
import com.algorand.android.shared_db.assetdetail.model.CustomInfoEntity

internal interface CustomInfoEntityMapper {
    operator fun invoke(customInfo: CustomInfo): CustomInfoEntity
}