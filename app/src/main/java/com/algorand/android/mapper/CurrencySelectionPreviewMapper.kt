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

package com.algorand.android.mapper

import com.algorand.android.models.ScreenState
import com.algorand.android.models.ui.CurrencySelectionPreview
import com.algorand.android.ui.settings.selection.CurrencyListItem
import com.algorand.android.utils.DataResource
import javax.inject.Inject

class CurrencySelectionPreviewMapper @Inject constructor() {

    fun mapToCurrencySelectionPreview(
        dataResource: DataResource<List<CurrencyListItem>>,
        isLoading: Boolean,
        isError: Boolean
    ): CurrencySelectionPreview {
        return CurrencySelectionPreview(
            isLoading = isLoading,
            isScreenStateViewVisible = isError,
            screenStateViewType = getScreenStateViewType(dataResource),
            isCurrencyListVisible = isError.not() && isLoading.not(),
            currencyList = (dataResource as? DataResource.Success)?.data
        )
    }

    private fun getScreenStateViewType(dataResource: DataResource<List<CurrencyListItem>>): ScreenState? {
        return when (dataResource) {
            is DataResource.Error.Api -> ScreenState.ConnectionError()
            is DataResource.Error -> ScreenState.DefaultError()
            else -> null
        }
    }
}
