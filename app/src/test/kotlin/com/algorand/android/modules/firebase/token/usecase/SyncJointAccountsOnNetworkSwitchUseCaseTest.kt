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

package com.algorand.android.modules.firebase.token.usecase

import com.algorand.android.deviceregistration.domain.usecase.DeviceIdUseCase
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccounts
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.jointaccount.creation.domain.model.JointAccount
import com.algorand.wallet.jointaccount.creation.domain.usecase.CreateJointAccount
import com.algorand.wallet.logger.PeraErrorLogger
import com.algorand.wallet.remoteconfig.domain.model.FeatureToggle
import com.algorand.wallet.remoteconfig.domain.usecase.IsFeatureToggleEnabled
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class SyncJointAccountsOnNetworkSwitchUseCaseTest {

    private val getLocalAccounts: GetLocalAccounts = mockk()
    private val createJointAccount: CreateJointAccount = mockk(relaxed = true)
    private val deviceIdUseCase: DeviceIdUseCase = mockk()
    private val isFeatureToggleEnabled: IsFeatureToggleEnabled = mockk()
    private val errorLogger: PeraErrorLogger = mockk(relaxed = true)

    private val useCase = SyncJointAccountsOnNetworkSwitchUseCase(
        getLocalAccounts = getLocalAccounts,
        createJointAccount = createJointAccount,
        deviceIdUseCase = deviceIdUseCase,
        isFeatureToggleEnabled = isFeatureToggleEnabled,
        errorLogger = errorLogger
    )

    @Test
    fun `EXPECT no calls WHEN feature toggle is disabled`() = runTest {
        every { isFeatureToggleEnabled(FeatureToggle.JOINT_ACCOUNT.key) } returns false

        useCase(this)
        advanceUntilIdle()

        coVerify(exactly = 0) { createJointAccount(any(), any(), any(), any()) }
    }

    @Test
    fun `EXPECT no calls WHEN device id is blank`() = runTest {
        every { isFeatureToggleEnabled(FeatureToggle.JOINT_ACCOUNT.key) } returns true
        every { deviceIdUseCase.getSelectedNodeDeviceId() } returns ""

        useCase(this)
        advanceUntilIdle()

        coVerify(exactly = 0) { createJointAccount(any(), any(), any(), any()) }
    }

    @Test
    fun `EXPECT no calls WHEN device id is null`() = runTest {
        every { isFeatureToggleEnabled(FeatureToggle.JOINT_ACCOUNT.key) } returns true
        every { deviceIdUseCase.getSelectedNodeDeviceId() } returns null

        useCase(this)
        advanceUntilIdle()

        coVerify(exactly = 0) { createJointAccount(any(), any(), any(), any()) }
    }

    @Test
    fun `EXPECT no calls WHEN no joint accounts exist`() = runTest {
        every { isFeatureToggleEnabled(FeatureToggle.JOINT_ACCOUNT.key) } returns true
        every { deviceIdUseCase.getSelectedNodeDeviceId() } returns TEST_DEVICE_ID
        coEvery { getLocalAccounts() } returns listOf(
            LocalAccount.NoAuth(algoAddress = "ADDR1")
        )

        useCase(this)
        advanceUntilIdle()

        coVerify(exactly = 0) { createJointAccount(any(), any(), any(), any()) }
    }

    @Test
    fun `EXPECT createJointAccount called for each joint account`() = runTest {
        every { isFeatureToggleEnabled(FeatureToggle.JOINT_ACCOUNT.key) } returns true
        every { deviceIdUseCase.getSelectedNodeDeviceId() } returns TEST_DEVICE_ID
        coEvery { getLocalAccounts() } returns listOf(
            createTestJointAccount("JOINT1"),
            createTestJointAccount("JOINT2"),
            LocalAccount.NoAuth(algoAddress = "STANDARD1")
        )
        coEvery { createJointAccount(any(), any(), any(), any()) } returns PeraResult.Success(mockk<JointAccount>())

        useCase(this)
        advanceUntilIdle()

        coVerify(exactly = 2) { createJointAccount(any(), any(), any(), deviceId = TEST_DEVICE_ID) }
    }

    @Test
    fun `EXPECT error logged WHEN createJointAccount throws`() = runTest {
        every { isFeatureToggleEnabled(FeatureToggle.JOINT_ACCOUNT.key) } returns true
        every { deviceIdUseCase.getSelectedNodeDeviceId() } returns TEST_DEVICE_ID
        val exception = RuntimeException("Network error")
        coEvery { getLocalAccounts() } returns listOf(createTestJointAccount("JOINT1"))
        coEvery { createJointAccount(any(), any(), any(), any()) } throws exception

        useCase(this)
        advanceUntilIdle()

        verify { errorLogger.logError(exception) }
    }

    @Test
    fun `EXPECT other accounts synced WHEN one throws`() = runTest {
        every { isFeatureToggleEnabled(FeatureToggle.JOINT_ACCOUNT.key) } returns true
        every { deviceIdUseCase.getSelectedNodeDeviceId() } returns TEST_DEVICE_ID
        coEvery { getLocalAccounts() } returns listOf(
            createTestJointAccount("JOINT1"),
            createTestJointAccount("JOINT2")
        )
        coEvery {
            createJointAccount(listOf("JOINT1", "P1"), any(), any(), any())
        } throws RuntimeException("fail")
        coEvery {
            createJointAccount(listOf("JOINT2", "P1"), any(), any(), any())
        } returns PeraResult.Success(mockk<JointAccount>())

        useCase(this)
        advanceUntilIdle()

        coVerify(exactly = 1) { createJointAccount(listOf("JOINT2", "P1"), any(), any(), any()) }
        verify(exactly = 1) { errorLogger.logError(any<Exception>()) }
    }

    private fun createTestJointAccount(address: String) = LocalAccount.Joint(
        algoAddress = address,
        participantAddresses = listOf(address, "P1"),
        threshold = TEST_THRESHOLD,
        version = TEST_VERSION
    )

    private companion object {
        const val TEST_DEVICE_ID = "device123"
        const val TEST_THRESHOLD = 2
        const val TEST_VERSION = 1
    }
}
