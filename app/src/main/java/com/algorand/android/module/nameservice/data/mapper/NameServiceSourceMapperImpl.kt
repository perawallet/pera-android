package com.algorand.android.module.nameservice.data.mapper

import com.algorand.android.module.nameservice.data.model.NameServiceSourceResponse
import com.algorand.android.module.nameservice.domain.model.NameServiceSource
import javax.inject.Inject

internal class NameServiceSourceMapperImpl @Inject constructor() : NameServiceSourceMapper {

    override fun invoke(response: NameServiceSourceResponse?): NameServiceSource {
        return when (response) {
            NameServiceSourceResponse.NFDOMAIN -> NameServiceSource.NFDOMAIN
            else -> NameServiceSource.UNKNOWN
        }
    }
}
