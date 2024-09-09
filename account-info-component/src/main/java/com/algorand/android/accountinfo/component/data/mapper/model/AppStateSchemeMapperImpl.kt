package com.algorand.android.accountinfo.component.data.mapper.model

import com.algorand.android.accountinfo.component.data.model.AppStateSchemaResponse
import com.algorand.android.accountinfo.component.domain.model.AppStateScheme
import javax.inject.Inject

internal class AppStateSchemeMapperImpl @Inject constructor() : AppStateSchemeMapper {

    override fun invoke(response: AppStateSchemaResponse?): AppStateScheme {
        return AppStateScheme(
            numByteSlice = response?.numByteSlice ?: DEFAULT_NUM_BYTE_SLICE,
            numUint = response?.numUint ?: DEFAULT_NUM_UINT
        )
    }

    override fun invoke(numByteSlice: Long?, numUint: Long?): AppStateScheme {
        return AppStateScheme(
            numByteSlice = numByteSlice ?: DEFAULT_NUM_BYTE_SLICE,
            numUint = numUint ?: DEFAULT_NUM_UINT
        )
    }

    companion object {
        private const val DEFAULT_NUM_BYTE_SLICE = 0L
        private const val DEFAULT_NUM_UINT = 0L
    }
}
