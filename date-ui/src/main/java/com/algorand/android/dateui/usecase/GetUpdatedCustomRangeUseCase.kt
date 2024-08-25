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

import com.algorand.android.date.component.UTC_ZONE_ID
import com.algorand.android.dateui.model.DateFilter
import com.algorand.android.dateui.model.DateRange
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

internal class GetUpdatedCustomRangeUseCase @Inject constructor() : GetUpdatedCustomRange {

    override fun invoke(
        customRange: DateFilter.CustomRange,
        isFromFocused: Boolean,
        datePickerYear: Int,
        datePickerMonth: Int,
        datePickerDay: Int
    ): DateFilter.CustomRange {
        // Adding 1 to month because date picker in UI level return months from 0-11
        val dateRange = if (isFromFocused) {
            DateRange(
                createBeginningOfDayZonedDateTime(datePickerYear, datePickerMonth + 1, datePickerDay),
                customRange.customDateRange?.to
            )
        } else {
            DateRange(
                customRange.customDateRange?.from,
                createEndOfDayZonedDateTime(datePickerYear, datePickerMonth + 1, datePickerDay)
            )
        }
        return DateFilter.CustomRange(dateRange)
    }

    private fun createBeginningOfDayZonedDateTime(year: Int, month: Int, day: Int): ZonedDateTime {
        return ZonedDateTime.of(
            year,
            month,
            day,
            LocalTime.MIN.hour,
            LocalTime.MIN.minute,
            LocalTime.MIN.second,
            LocalTime.MIN.nano,
            ZoneId.of(UTC_ZONE_ID)
        )
    }

    private fun createEndOfDayZonedDateTime(year: Int, month: Int, day: Int): ZonedDateTime {
        return ZonedDateTime.of(
            year,
            month,
            day,
            LocalTime.MAX.hour,
            LocalTime.MAX.minute,
            LocalTime.MAX.second,
            LocalTime.MAX.nano,
            ZoneId.of(UTC_ZONE_ID)
        )
    }
}
