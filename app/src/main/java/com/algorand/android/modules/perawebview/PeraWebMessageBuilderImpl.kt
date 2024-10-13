package com.algorand.android.modules.perawebview

import com.algorand.android.modules.perawebview.model.PeraWebMessage
import com.algorand.android.modules.peraserializer.PeraSerializer
import javax.inject.Inject

class PeraWebMessageBuilderImpl @Inject constructor(
    private val peraSerializer: PeraSerializer
) : PeraWebMessageBuilder {

    override fun buildMessage(action: PeraWebMessageAction, payload: String): String {
        val messagePayload = getMessagePayload(action, payload)
        val messageJson = peraSerializer.toJson(messagePayload)
        return "handleMessage('$messageJson')"
    }

    private fun getMessagePayload(action: PeraWebMessageAction, payload: String): PeraWebMessage {
        return PeraWebMessage(
            action = action.key,
            payload = payload
        )
    }
}
