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

package com.algorand.common.deeplink.builder

import com.algorand.common.deeplink.model.DeepLink
import com.algorand.common.deeplink.model.DeepLinkPayload

internal class KeyRegTransactionDeepLinkBuilder : DeepLinkBuilder {

    override fun doesDeeplinkMeetTheRequirements(payload: DeepLinkPayload): Boolean {
        return payload.type == "keyreg"
    }

    override fun createDeepLink(payload: DeepLinkPayload): DeepLink {
        return with(payload) {
            DeepLink.KeyReg(
                senderAddress = accountAddress.orEmpty(),
                fee = fee,
                note = note,
                xnote = xnote,
                voteKey = votekey,
                selkey = selkey,
                sprfkey = sprfkey,
                votefst = votefst,
                votelst = votelst,
                votekd = votekd,
                type = type.orEmpty()
            )
        }
    }
}
