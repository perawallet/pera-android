package com.algorand.android.module.transaction.history.component.data.mapper

import com.algorand.android.module.transaction.history.component.data.model.OnCompletionResponse
import com.algorand.android.module.transaction.history.component.domain.model.OnCompletion

internal interface OnCompletionMapper {
    operator fun invoke(response: OnCompletionResponse?): OnCompletion?
}
