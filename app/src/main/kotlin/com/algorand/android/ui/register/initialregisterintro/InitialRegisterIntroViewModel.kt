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

package com.algorand.android.ui.register.initialregisterintro

import com.algorand.android.core.BaseViewModel
import com.algorand.android.models.AccountCreation
import com.algorand.android.utils.analytics.CreationType
import com.algorand.wallet.algosdk.transaction.sdk.AlgoAccountSdk
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InitialRegisterIntroViewModel @Inject constructor(
    private val algoAccountSdk: AlgoAccountSdk,
    private val aesPlatformManager: AESPlatformManager,
) : BaseViewModel() {

    fun createHdKeyAccount(): AccountCreation? {
        val account = algoAccountSdk.createHdAccount()
            ?: return null

        return AccountCreation(
            address = account.address,
            customName = null,
            isBackedUp = false,
            type = AccountCreation.Type.HdKey(
                account.publicKey,
                aesPlatformManager.encryptByteArray(account.privateKey),
                aesPlatformManager.encryptByteArray(account.entropy),
                account.account,
                account.change,
                account.keyIndex,
                account.derivationType,
            ),
            creationType = CreationType.CREATE
        )
    }
}
