/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.common.account.info.data.mapper

import com.algorand.common.account.info.data.model.AppStateSchemaResponse
import com.algorand.common.account.info.domain.model.AppStateScheme

internal class AppStateSchemeMapperImpl : AppStateSchemeMapper {

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
