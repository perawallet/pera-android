package com.algorand.android.module.transaction.history.component.data.mapper.implementation

import com.algorand.android.module.transaction.history.component.data.mapper.OnCompletionMapper
import com.algorand.android.module.transaction.history.component.data.model.OnCompletionResponse
import com.algorand.android.module.transaction.history.component.data.model.OnCompletionResponse.CLEAR_STATE
import com.algorand.android.module.transaction.history.component.data.model.OnCompletionResponse.CLOSE_OUT
import com.algorand.android.module.transaction.history.component.data.model.OnCompletionResponse.DELETE_APPLICATION
import com.algorand.android.module.transaction.history.component.data.model.OnCompletionResponse.NO_OP
import com.algorand.android.module.transaction.history.component.data.model.OnCompletionResponse.OPT_IN
import com.algorand.android.module.transaction.history.component.data.model.OnCompletionResponse.UNKNOWN
import com.algorand.android.module.transaction.history.component.data.model.OnCompletionResponse.UPDATE_APPLICATION
import com.algorand.android.module.transaction.history.component.domain.model.OnCompletion
import javax.inject.Inject

internal class OnCompletionMapperImpl @Inject constructor() : OnCompletionMapper {

    override fun invoke(response: OnCompletionResponse?): OnCompletion? {
        if (response == null) return null
        return when (response) {
            OPT_IN -> OnCompletion.OPT_IN
            NO_OP -> OnCompletion.NO_OP
            CLOSE_OUT -> OnCompletion.CLOSE_OUT
            CLEAR_STATE -> OnCompletion.CLEAR_STATE
            UPDATE_APPLICATION -> OnCompletion.UPDATE_APPLICATION
            DELETE_APPLICATION -> OnCompletion.DELETE_APPLICATION
            UNKNOWN -> OnCompletion.UNKNOWN
        }
    }
}
