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

package com.algorand.android.ui.onboarding.creation.mapper

import com.algorand.android.models.AccountCreation
import com.algorand.wallet.algosdk.bip39.model.HdKeyAddress
import com.algorand.wallet.encryption.domain.manager.AESPlatformManager
import javax.inject.Inject

internal class DefaultAccountCreationHdKeyTypeMapper @Inject constructor(
    private val aesPlatformManager: AESPlatformManager
) : AccountCreationHdKeyTypeMapper {

    override fun invoke(entropy: ByteArray, hdKeyAddress: HdKeyAddress, seedId: Int?): AccountCreation.Type.HdKey {
        return with(hdKeyAddress) {
            AccountCreation.Type.HdKey(
                publicKey,
                aesPlatformManager.encryptByteArray(privateKey),
                aesPlatformManager.encryptByteArray(entropy),
                index.accountIndex,
                index.changeIndex,
                index.keyIndex,
                derivationType.value,
                seedId
            )
        }
    }
}
