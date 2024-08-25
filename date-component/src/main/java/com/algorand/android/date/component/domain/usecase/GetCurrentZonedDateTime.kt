package com.algorand.android.date.component.domain.usecase

import java.time.ZonedDateTime

interface GetCurrentZonedDateTime {
    operator fun invoke(): ZonedDateTime
}
