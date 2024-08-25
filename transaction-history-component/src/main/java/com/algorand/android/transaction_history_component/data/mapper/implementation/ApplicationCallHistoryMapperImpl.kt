package com.algorand.android.transaction_history_component.data.mapper.implementation

import com.algorand.android.transaction_history_component.data.mapper.ApplicationCallHistoryMapper
import com.algorand.android.transaction_history_component.data.mapper.OnCompletionMapper
import com.algorand.android.transaction_history_component.data.model.ApplicationCallResponse
import com.algorand.android.transaction_history_component.domain.model.ApplicationCallHistory
import javax.inject.Inject

internal class ApplicationCallHistoryMapperImpl @Inject constructor(
    private val onCompletionMapper: OnCompletionMapper
) : ApplicationCallHistoryMapper {

    override fun invoke(response: ApplicationCallResponse?): ApplicationCallHistory? {
        if (response == null) return null
        return ApplicationCallHistory(
            applicationId = response.applicationId,
            accounts = response.accounts,
            foreignAssets = response.foreignAssets,
            onCompletion = onCompletionMapper(response.onCompletion),
            foreignApps = response.foreignApps
        )
    }
}
