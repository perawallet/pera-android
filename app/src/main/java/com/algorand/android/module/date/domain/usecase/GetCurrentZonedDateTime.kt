package com.algorand.android.module.date.domain.usecase

import java.time.ZonedDateTime

interface GetCurrentZonedDateTime {
    operator fun invoke(): ZonedDateTime
}
