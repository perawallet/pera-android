package com.algorand.android.module.transaction.history.component.data.mapper

import com.algorand.android.module.transaction.history.component.data.model.ApplicationCallResponse
import com.algorand.android.module.transaction.history.component.domain.model.ApplicationCallHistory

internal interface ApplicationCallHistoryMapper {
    operator fun invoke(response: ApplicationCallResponse?): ApplicationCallHistory?
}
