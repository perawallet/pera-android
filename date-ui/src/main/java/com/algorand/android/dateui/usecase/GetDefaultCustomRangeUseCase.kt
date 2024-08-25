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

package com.algorand.android.dateui.usecase

import com.algorand.android.date.component.DEFAULT_DAY_DIFFERENCE_BETWEEN_FROM_AND_TO
import com.algorand.android.date.component.getCurrentTimeAsZonedDateTime
import com.algorand.android.date.component.getPreviousDayZonedDateTime
import com.algorand.android.dateui.model.DateFilter
import com.algorand.android.dateui.model.DateRange
import javax.inject.Inject

internal class GetDefaultCustomRangeUseCase @Inject constructor() : GetDefaultCustomRange {

    override fun invoke(): DateFilter.CustomRange {
        return DateFilter.CustomRange(
            DateRange(
                getPreviousDayZonedDateTime(DEFAULT_DAY_DIFFERENCE_BETWEEN_FROM_AND_TO),
                getCurrentTimeAsZonedDateTime()
            )
        )
    }
}
