package com.algorand.android.transaction_history_component.data.mapper

import com.algorand.android.transaction_history_component.data.model.OnCompletionResponse
import com.algorand.android.transaction_history_component.domain.model.OnCompletion

internal interface OnCompletionMapper {
    operator fun invoke(response: OnCompletionResponse?): OnCompletion?
}
