package com.algorand.android.custominfo.component.domain.usecase

import com.algorand.android.custominfo.component.domain.model.CustomInfo

fun interface GetCustomInfoOrNull {
    suspend operator fun invoke(address: String): CustomInfo?
}
