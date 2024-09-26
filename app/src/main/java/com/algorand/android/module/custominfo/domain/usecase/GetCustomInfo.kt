package com.algorand.android.module.custominfo.domain.usecase

import com.algorand.android.module.custominfo.domain.model.CustomInfo

fun interface GetCustomInfo {
    suspend operator fun invoke(address: String): CustomInfo
}
