package com.algorand.android.transaction_history_component.data.mapper

import com.algorand.android.transaction_history_component.data.model.ApplicationCallResponse
import com.algorand.android.transaction_history_component.domain.model.ApplicationCallHistory

internal interface ApplicationCallHistoryMapper {
    operator fun invoke(response: ApplicationCallResponse?): ApplicationCallHistory?
}
