package com.algorand.android.accountinfo.component.data.mapper.model

import com.algorand.android.accountinfo.component.data.model.AppStateSchemaResponse
import com.algorand.android.accountinfo.component.domain.model.AppStateScheme

internal interface AppStateSchemeMapper {
    operator fun invoke(response: AppStateSchemaResponse?): AppStateScheme
    operator fun invoke(numByteSlice: Long?, numUint: Long?): AppStateScheme
}
