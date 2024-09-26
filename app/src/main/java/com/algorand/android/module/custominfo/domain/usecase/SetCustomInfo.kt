package com.algorand.android.module.custominfo.domain.usecase

import com.algorand.android.module.custominfo.domain.model.CustomInfo

fun interface SetCustomInfo {
    suspend operator fun invoke(customInfo: CustomInfo)
}
