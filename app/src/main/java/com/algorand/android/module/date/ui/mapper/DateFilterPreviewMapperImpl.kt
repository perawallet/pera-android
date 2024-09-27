/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.module.date.ui.mapper

import com.algorand.android.module.date.ui.model.DateFilter
import com.algorand.android.module.date.ui.model.DateFilterPreview
import com.algorand.android.R
import javax.inject.Inject

internal class DateFilterPreviewMapperImpl @Inject constructor() : DateFilterPreviewMapper {

    override fun invoke(dateFilter: DateFilter): DateFilterPreview {
        return DateFilterPreview(
            filterButtonIconResId = getFilterButtonIconResId(dateFilter),
            title = getDateFilterTitle(dateFilter),
            titleResId = getDateFilterTitleResId(dateFilter),
            useFilterIconsOwnTint = dateFilter != DateFilter.AllTime
        )
    }

    private fun getFilterButtonIconResId(dateFilter: DateFilter): Int {
        return if (dateFilter == DateFilter.AllTime) {
            R.drawable.ic_customize
        } else {
            R.drawable.ic_customized_filled
        }
    }

    private fun getDateFilterTitle(dateFilter: DateFilter): String? {
        return when (dateFilter) {
            DateFilter.AllTime -> null
            is DateFilter.CustomRange -> dateFilter.getDateRange()?.getRangeAsText(dateFilter).orEmpty()
            else -> null
        }
    }

    private fun getDateFilterTitleResId(dateFilter: DateFilter): Int? {
        return when (dateFilter) {
            DateFilter.AllTime -> R.string.transactions
            is DateFilter.CustomRange -> null
            else -> dateFilter.titleResId
        }
    }
}
