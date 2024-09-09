package com.algorand.android.custominfo.component.data.mapper.entity

import com.algorand.android.custominfo.component.domain.model.CustomInfo
import com.algorand.android.shared_db.assetdetail.model.CustomInfoEntity

internal interface CustomInfoEntityMapper {
    operator fun invoke(customInfo: CustomInfo): CustomInfoEntity
}
