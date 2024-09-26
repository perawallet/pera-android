package com.algorand.android.module.deviceid.domain.usecase

import com.algorand.android.foundation.PeraResult

fun interface RegisterDeviceId {
    suspend operator fun invoke(token: String): PeraResult<String>
}
