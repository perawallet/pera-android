package com.algorand.android.transaction_history_component.data.mapper

import com.algorand.android.transaction_history_component.data.model.SignatureResponse
import com.algorand.android.transaction_history_component.domain.model.Signature

internal interface SignatureMapper {
    operator fun invoke(response: SignatureResponse?): Signature?
}
