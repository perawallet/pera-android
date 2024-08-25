package com.algorand.android.date.component

import com.algorand.android.date.component.DateConstants.TXN_DATE_PATTERN
import com.algorand.android.date.component.DateConstants.UNIX_TIME_STAMP_MULTIPLIER
import com.algorand.android.date.component.DateConstants.WEEK_IN_DAYS
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.TemporalAdjusters

const val UTC_ZONE_ID = "UTC"
const val DEFAULT_DAY_DIFFERENCE_BETWEEN_FROM_AND_TO = 1L

fun getBeginningOfDay(dayBefore: Long = 0): ZonedDateTime {
    return ZonedDateTime.now().minusDays(dayBefore).with(LocalTime.MIN)
}

fun getBeginningOfWeek(): ZonedDateTime {
    return ZonedDateTime.now().minusWeeks(1).with(DayOfWeek.MONDAY).with(LocalTime.MIN)
}

fun getEndOfWeek(beginningOfWeek: ZonedDateTime): ZonedDateTime {
    return beginningOfWeek.plusDays(WEEK_IN_DAYS - 1).with(LocalTime.MAX)
}

fun getBeginningOfMonth(): ZonedDateTime {
    return ZonedDateTime.now().minusMonths(1).withDayOfMonth(1).with(LocalTime.MIN)
}

fun getEndOfMonth(beginningOfMonth: ZonedDateTime): ZonedDateTime {
    return beginningOfMonth.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX)
}

fun timeStampToZonedDateTime(timeStamp: Long): ZonedDateTime {
    val zone = ZoneId.systemDefault()
    return Instant.ofEpochMilli(timeStamp * UNIX_TIME_STAMP_MULTIPLIER).atZone(zone)
}

fun ZonedDateTime?.formatAsRFC3339Version(): String? {
    return this?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
}

fun ZonedDateTime.formatAsDate(): String {
    return format(DateTimeFormatter.ofPattern(TXN_DATE_PATTERN))
}

fun getUTCZonedDateTime(): ZonedDateTime {
    return ZonedDateTime.ofInstant(Instant.now(), ZoneId.of(UTC_ZONE_ID))
}

fun getCurrentTimeAsZonedDateTime(): ZonedDateTime {
    return ZonedDateTime.now()
}

fun getPreviousDayZonedDateTime(differenceAsDay: Long): ZonedDateTime {
    return ZonedDateTime.now().minusDays(differenceAsDay)
}

fun ZonedDateTime?.orNow(): ZonedDateTime {
    return this ?: ZonedDateTime.now()
}

fun String?.parseFormattedDate(dateTimeFormatter: DateTimeFormatter): ZonedDateTime? {
    return try {
        if (this == null) {
            null
        } else {
            OffsetDateTime.parse(this, dateTimeFormatter).toZonedDateTime()
        }
    } catch (exception: Exception) {
        null
    }
}

fun getAlgorandMobileDateFormatter(): DateTimeFormatter {
    return DateTimeFormatterBuilder()
        .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME).appendOffset("+HHMM", "0000")
        .toFormatter()
}
