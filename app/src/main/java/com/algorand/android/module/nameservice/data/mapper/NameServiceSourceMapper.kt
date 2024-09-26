package com.algorand.android.module.nameservice.data.mapper

import com.algorand.android.module.nameservice.data.model.NameServiceSourceResponse
import com.algorand.android.module.nameservice.domain.model.NameServiceSource

internal interface NameServiceSourceMapper {
    operator fun invoke(response: NameServiceSourceResponse?): NameServiceSource
}
