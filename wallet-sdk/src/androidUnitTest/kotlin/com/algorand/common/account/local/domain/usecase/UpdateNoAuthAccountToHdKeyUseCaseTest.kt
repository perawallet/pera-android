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

package com.algorand.common.account.local.domain.usecase

import com.algorand.common.algosdk.Bip32DerivationType
import com.algorand.common.encryption.SecretKeyEncryptionManager
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UpdateNoAuthAccountToHdKeyUseCaseTest {

    private val deleteLocalAccount: DeleteLocalAccount = mockk(relaxed = true)
    private val createHdKeyAccount: CreateHdKeyAccount = mockk(relaxed = true)
    private val secretKeyEncryptionManager: SecretKeyEncryptionManager = mockk(relaxed = true)

    private val sut = UpdateNoAuthAccountToHdKeyUseCase(
        deleteLocalAccount,
        createHdKeyAccount,
        secretKeyEncryptionManager
    )

    @Test
    fun `EXPECT noAuthAccount to be deleted and new Algo25Account to be created`() = runTest {
        sut(
            ADDRESS,
            PUBLIC_KEY,
            PRIVATE_KEY,
            SEED_ID,
            ACCOUNT,
            CHANGE,
            KEY_INDEX,
            Bip32DerivationType.Peikert
        )

        coVerify { deleteLocalAccount(ADDRESS) }
        coVerify { createHdKeyAccount(
            ADDRESS,
            PUBLIC_KEY,
            PRIVATE_KEY,
            SEED_ID,
            ACCOUNT,
            CHANGE,
            KEY_INDEX,
            Bip32DerivationType.Peikert
        ) }
    }

    companion object {
        private const val ADDRESS = "ADDRESS"
        private val PUBLIC_KEY = byteArrayOf()
        private val PRIVATE_KEY = byteArrayOf()
        private val SEED_ID = 0
        private val ACCOUNT = 0
        private val CHANGE = 0
        private val KEY_INDEX = 0
    }
}
