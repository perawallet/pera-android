package com.algorand.android.module.account.info.data.mapper.model

import com.algorand.android.module.account.info.data.model.AppStateSchemaResponse
import com.algorand.android.module.account.info.domain.model.AppStateScheme

internal interface AppStateSchemeMapper {
    operator fun invoke(response: AppStateSchemaResponse?): AppStateScheme
    operator fun invoke(numByteSlice: Long?, numUint: Long?): AppStateScheme
}
