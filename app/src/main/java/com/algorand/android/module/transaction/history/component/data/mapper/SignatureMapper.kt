package com.algorand.android.module.transaction.history.component.data.mapper

import com.algorand.android.module.transaction.history.component.data.model.SignatureResponse
import com.algorand.android.module.transaction.history.component.domain.model.Signature

internal interface SignatureMapper {
    operator fun invoke(response: SignatureResponse?): Signature?
}
