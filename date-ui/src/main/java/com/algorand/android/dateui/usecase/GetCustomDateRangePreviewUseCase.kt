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
import com.algorand.android.dateui.model.CustomDateRangePreview
import com.algorand.android.dateui.model.DatePickerDate
import com.algorand.android.dateui.model.DateRange
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

internal class GetCustomDateRangePreviewUseCase @Inject constructor() : GetCustomDateRangePreview {

    override fun invoke(latestDateRange: DateRange?, isFromFocused: Boolean): CustomDateRangePreview {
        val defaultToDate = getCurrentTimeAsZonedDateTime()
        val defaultFromDate = getPreviousDayZonedDateTime(DEFAULT_DAY_DIFFERENCE_BETWEEN_FROM_AND_TO)

        val fromDate = latestDateRange?.from ?: defaultFromDate
        val toDate = latestDateRange?.to ?: defaultToDate

        val formattedFromDate = fromDate.formatAsCustomDateString()
        val formattedToDate = toDate.formatAsCustomDateString()

        val minDate = createMinDate(fromDate, isFromFocused)
        val maxDate = createMaxDate(toDate, isFromFocused, System.currentTimeMillis())

        // Subtracting 1 from month as date picker in UI level accepts months from 0-11
        val focusedDatePickerDate = if (isFromFocused) {
            with(fromDate) { DatePickerDate(year, monthValue - 1, dayOfMonth) }
        } else {
            with(toDate) { DatePickerDate(year, monthValue - 1, dayOfMonth) }
        }

        return CustomDateRangePreview(
            formattedFromDate = formattedFromDate,
            formattedToDate = formattedToDate,
            focusedDateYear = focusedDatePickerDate.year,
            focusedDateMonth = focusedDatePickerDate.month,
            focusedDateDay = focusedDatePickerDate.day,
            minDateInMillis = minDate,
            maxDateInMillis = maxDate,
            isFromFocused = isFromFocused
        )
    }

    private fun ZonedDateTime.formatAsCustomDateString(): String {
        return format(DateTimeFormatter.ofPattern(MONTH_DAY_YEAR_WITH_DOT_PATTERN))
    }

    private fun createMinDate(fromZonedDateTime: ZonedDateTime?, isFromFocused: Boolean): Long {
        return if (isFromFocused) {
            DEFAULT_MIN_DATE_IN_MILLIS
        } else {
            fromZonedDateTime?.toInstant()?.toEpochMilli()?.plus(ONE_DAY_IN_MILLIS) ?: DEFAULT_MIN_DATE_IN_MILLIS
        }
    }

    private fun createMaxDate(toZonedDateTime: ZonedDateTime?, isFromFocused: Boolean, defaultMax: Long): Long {
        // We need to convert date to start of day because date picker rounds it up to next day after 11PM
        return if (isFromFocused) {
            toZonedDateTime?.toInstant()
                ?.toEpochMilli()
                ?.minus(ONE_DAY_IN_MILLIS)
                ?.let { convertDateInMillisToStartOfDay(it) }
                ?: convertDateInMillisToStartOfDay(defaultMax)
        } else {
            convertDateInMillisToStartOfDay(defaultMax) + 1
        }
    }

    private fun convertDateInMillisToStartOfDay(date: Long): Long {
        return date - (date % ONE_DAY_IN_MILLIS)
    }

    private companion object {

        // This date corresponds to 01.01.2019
        const val DEFAULT_MIN_DATE_IN_MILLIS = 1546290000000

        const val ONE_DAY_IN_MILLIS = (1000 * 60 * 60 * 24)
        const val MONTH_DAY_YEAR_WITH_DOT_PATTERN = "MM.dd.yyyy"
    }
}
