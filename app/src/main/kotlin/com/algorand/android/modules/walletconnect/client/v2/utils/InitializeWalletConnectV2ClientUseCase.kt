/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.modules.walletconnect.client.v2.utils

import android.app.Application
import app.perawallet.walletconnectv2.Core
import app.perawallet.walletconnectv2.CoreClient
import app.perawallet.walletconnectv2.relay.ConnectionType
import app.perawallet.walletconnectv2.sign.client.Sign
import app.perawallet.walletconnectv2.web3.wallet.client.Wallet
import app.perawallet.walletconnectv2.web3.wallet.client.Web3Wallet
import com.algorand.android.deviceregistration.domain.usecase.FirebasePushTokenUseCase
import com.algorand.android.modules.walletconnect.client.v2.domain.WalletConnectV2SignClient
import com.algorand.android.utils.walletconnect.peermeta.WalletConnectPeraPeerMeta
import com.algorand.wallet.logger.PeraErrorLogger
import javax.inject.Inject

class InitializeWalletConnectV2ClientUseCase @Inject constructor(
    private val signClient: WalletConnectV2SignClient,
    private val firebasePushTokenUseCase: FirebasePushTokenUseCase,
    private val errorLogger: PeraErrorLogger
) {

    operator fun invoke(application: Application) {
        initializeCoreClient(application)
        registerFirebasePushToken()
        initializeSignClient()
    }

    private fun initializeCoreClient(application: Application) {
        CoreClient.initialize(
            relayServerUrl = getRelayServerUrl(),
            connectionType = ConnectionType.MANUAL,
            application = application,
            metaData = getPeraWalletAppMetaData()
        ) { error ->
            errorLogger.logError(error.throwable)
        }
    }

    private fun registerFirebasePushToken() {
        val initParams = Wallet.Params.Init(core = CoreClient)

        Web3Wallet.initialize(initParams) { error ->
            errorLogger.logError(error.throwable)
        }

        val firebaseAccessToken = firebasePushTokenUseCase.getPushTokenOrNull()?.data.orEmpty()
        val enableEncrypted = false

        try {
            Web3Wallet.registerDeviceToken(
                firebaseAccessToken = firebaseAccessToken,
                enableEncrypted = enableEncrypted,
                onSuccess = {
                    // No need to do anything here
                },
                onError = { error: Wallet.Model.Error ->
                    errorLogger.logError(error.throwable)
                }
            )
        } catch (e: Exception) {
            errorLogger.logError(e)
        }
    }

    private fun initializeSignClient() {
        val initParams = Sign.Params.Init(core = CoreClient)
        signClient.initialize(initParams)
    }

    private fun getPeraWalletAppMetaData(): Core.Model.AppMetaData {
        return with(WalletConnectPeraPeerMeta) {
            Core.Model.AppMetaData(
                name = name,
                description = description.orEmpty(),
                url = url,
                icons = icons.orEmpty(),
                redirect = redirectUrl
            )
        }
    }

    private fun getRelayServerUrl(): String {
        return WalletConnectV2ServerUrlBuilder.create()
            .addProjectId(PROJECT_ID)
            .build()
    }

    companion object {
        // TODO Change project id when it is decided
        private const val PROJECT_ID = "d98a4285aff59c9cd463bdd8b7415465"
    }
}
