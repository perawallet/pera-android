package com.algorand.android.date.component.domain.usecase.implementation

import com.algorand.android.date.component.domain.usecase.GetCurrentZonedDateTime
import java.time.ZonedDateTime
import javax.inject.Inject

internal class GetCurrentZonedDateTimeUseCase @Inject constructor() : GetCurrentZonedDateTime {

    override fun invoke(): ZonedDateTime {
        return ZonedDateTime.now()
    }
}
