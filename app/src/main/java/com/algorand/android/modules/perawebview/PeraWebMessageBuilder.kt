package com.algorand.android.modules.perawebview

interface PeraWebMessageBuilder {
    fun buildMessage(action: PeraWebMessageAction, payload: String): String
}
