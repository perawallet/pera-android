/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.utils.date

import android.text.format.DateUtils
import com.algorand.wallet.utils.date.RelativeTimeDifference.RelativeTime
import com.algorand.wallet.utils.date.RelativeTimeDifference.RelativeTime.Days
import com.algorand.wallet.utils.date.RelativeTimeDifference.RelativeTime.Hours
import com.algorand.wallet.utils.date.RelativeTimeDifference.RelativeTime.Minutes
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

internal data class PeraRelativeTimeDifference @Inject constructor(
    private val timeProvider: TimeProvider
) : RelativeTimeDifference {

    override fun getRelativeTime(time: ZonedDateTime, timeDifference: Long): RelativeTime {
        return when {
            timeDifference < DateUtils.MINUTE_IN_MILLIS -> RelativeTime.Now
            timeDifference < DateUtils.HOUR_IN_MILLIS -> Minutes((timeDifference / DateUtils.MINUTE_IN_MILLIS).toInt())
            timeDifference < DateUtils.DAY_IN_MILLIS -> Hours((timeDifference / DateUtils.HOUR_IN_MILLIS).toInt())
            timeDifference < DateUtils.WEEK_IN_MILLIS -> Days((timeDifference / DateUtils.DAY_IN_MILLIS).toInt())
            else -> RelativeTime.Date(time.format(DateTimeFormatter.ofPattern(MONTH_DAY_YEAR_PATTERN)))
        }
    }

    override fun getCurrentRelativeTime(timestampMillis: Long): RelativeTime {
        val timeDifference = timeProvider.getCurrentTimeMillis() - timestampMillis
        val time = timeProvider.getZonedDateTimeNow().minusNanos(timeDifference * NANOS_TO_MILLIS_MULTIPLIER)
        return getRelativeTime(time, timeDifference)
    }

    private companion object {
        const val MONTH_DAY_YEAR_PATTERN = "MMMM dd, yyyy"
        const val NANOS_TO_MILLIS_MULTIPLIER = 1_000_000
    }
}
