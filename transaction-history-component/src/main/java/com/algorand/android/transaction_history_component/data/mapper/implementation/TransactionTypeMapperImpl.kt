package com.algorand.android.transaction_history_component.data.mapper.implementation

import com.algorand.android.transaction_history_component.data.mapper.TransactionTypeMapper
import com.algorand.android.transaction_history_component.data.model.TransactionTypeResponse
import com.algorand.android.transaction_history_component.domain.model.TransactionType
import javax.inject.Inject

internal class TransactionTypeMapperImpl @Inject constructor() : TransactionTypeMapper {

    override fun invoke(response: TransactionTypeResponse?): TransactionType? {
        if (response == null) return null
        return when (response) {
            TransactionTypeResponse.PAY_TRANSACTION -> TransactionType.PAY_TRANSACTION
            TransactionTypeResponse.ASSET_TRANSACTION -> TransactionType.ASSET_TRANSACTION
            TransactionTypeResponse.APP_TRANSACTION -> TransactionType.APP_TRANSACTION
            TransactionTypeResponse.ASSET_CONFIGURATION -> TransactionType.ASSET_CONFIGURATION
            TransactionTypeResponse.KEYREG -> TransactionType.KEYREG_TRANSACTION
            TransactionTypeResponse.UNDEFINED -> TransactionType.UNDEFINED
        }
    }
}
