package com.algorand.android.module.transaction.history.component.data.mapper.implementation

import com.algorand.android.module.transaction.history.component.data.mapper.SignatureMapper
import com.algorand.android.module.transaction.history.component.data.model.SignatureResponse
import com.algorand.android.module.transaction.history.component.domain.model.Signature
import javax.inject.Inject

internal class SignatureMapperImpl @Inject constructor() : SignatureMapper {

    override fun invoke(response: SignatureResponse?): Signature? {
        if (response == null) return null
        return Signature(response.signatureKey)
    }
}
