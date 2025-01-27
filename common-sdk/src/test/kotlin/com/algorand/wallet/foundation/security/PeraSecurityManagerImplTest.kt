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

package com.algorand.wallet.foundation.security

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.security.Provider
import org.junit.Test

class PeraSecurityManagerImplTest {

    private val securityManager: SecurityManager = mockk(relaxed = true)
    private val securityProvidersFactory: SecurityProvidersFactory = mockk()

    private val sut = PeraSecurityManagerImpl(securityManager, securityProvidersFactory)

    @Test
    fun `EXPECT security providers to be registered`() {
        every { securityProvidersFactory.getProviders() } returns listOf(SECURITY_PROVIDER_1, SECURITY_PROVIDER_2)

        sut.initializeSecurityManager()

        verify { securityManager.registerProvider(SECURITY_PROVIDER_1) }
        verify { securityManager.registerProvider(SECURITY_PROVIDER_2) }
    }

    private companion object {
        val SECURITY_PROVIDER_1 = SecurityProvider(
            provider = object : Provider("1", 1.0, "1") {},
            priority = 1
        )
        val SECURITY_PROVIDER_2 = SecurityProvider(
            provider = object : Provider("2", 1.0, "2") {},
            priority = 2
        )
    }
}
