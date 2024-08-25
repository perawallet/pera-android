package com.algorand.android.nameservice.data.mapper

import com.algorand.android.nameservice.data.model.NameServicePayload
import com.algorand.android.nameservice.domain.model.NameService

internal interface NameServiceMapper {
    operator fun invoke(responses: List<NameServicePayload>): List<NameService>
}
