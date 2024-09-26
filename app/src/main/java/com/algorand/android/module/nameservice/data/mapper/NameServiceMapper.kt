package com.algorand.android.module.nameservice.data.mapper

import com.algorand.android.module.nameservice.data.model.NameServicePayload
import com.algorand.android.module.nameservice.domain.model.NameService

internal interface NameServiceMapper {
    operator fun invoke(responses: List<NameServicePayload>): List<NameService>
}
