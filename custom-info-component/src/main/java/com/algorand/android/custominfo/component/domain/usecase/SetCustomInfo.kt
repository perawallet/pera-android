package com.algorand.android.custominfo.component.domain.usecase

import com.algorand.android.custominfo.component.domain.model.CustomInfo

fun interface SetCustomInfo {
    suspend operator fun invoke(customInfo: CustomInfo)
}
