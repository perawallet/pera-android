package com.algorand.android.nameservice.data.mapper

import com.algorand.common.nameservice.data.model.NameServiceSourceResponse
import com.algorand.common.nameservice.domain.model.NameServiceSource

internal interface NameServiceSourceMapper {
    operator fun invoke(response: NameServiceSourceResponse?): NameServiceSource
}
