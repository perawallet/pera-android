/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

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
