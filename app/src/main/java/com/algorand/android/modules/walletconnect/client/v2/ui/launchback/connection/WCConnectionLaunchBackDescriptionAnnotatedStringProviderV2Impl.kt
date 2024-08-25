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

package com.algorand.android.modules.walletconnect.client.v2.ui.launchback.connection

import com.algorand.android.R
import com.algorand.android.models.AnnotatedString
import com.algorand.android.modules.walletconnect.client.v2.ui.launchback.usecase.GetFormattedWCSessionMaxExpirationDateUseCase
import com.algorand.android.modules.walletconnect.domain.model.WalletConnect
import com.algorand.android.modules.walletconnect.launchback.connection.ui.model.WCConnectionLaunchBackDescriptionAnnotatedStringProvider

class WCConnectionLaunchBackDescriptionAnnotatedStringProviderV2Impl(
    private val getFormattedWCSessionMaxExpirationDateUseCase: GetFormattedWCSessionMaxExpirationDateUseCase
) : WCConnectionLaunchBackDescriptionAnnotatedStringProvider {

    override suspend fun provideAnnotatedString(
        sessionDetail: WalletConnect.SessionDetail,
        launchBackBrowserItemCount: Int
    ): AnnotatedString {
        val formattedMaxExtendableExpiryDate = getFormattedWCSessionMaxExpirationDateUseCase(
            sessionDetail.sessionIdentifier
        )
        val peerMetaName = sessionDetail.peerMeta.name
        return when (launchBackBrowserItemCount) {
            0, 1 -> {
                AnnotatedString(
                    stringResId = R.string.its_validity_will_be_automatically,
                    replacementList = listOf(
                        "session_expiry_date" to formattedMaxExtendableExpiryDate,
                        "peer_name" to peerMetaName,
                    )
                )
            }
            else -> {
                AnnotatedString(
                    stringResId = R.string.its_validity_will_be_automatically_extended,
                    replacementList = listOf(
                        "session_expiry_date" to formattedMaxExtendableExpiryDate,
                        "peer_name" to peerMetaName,
                    )
                )
            }
        }
    }

    companion object {
        const val INJECTION_NAME = "wcConnectionLaunchBackBrowserDescriptionAnnotatedStringV2InjectionName"
    }
}
