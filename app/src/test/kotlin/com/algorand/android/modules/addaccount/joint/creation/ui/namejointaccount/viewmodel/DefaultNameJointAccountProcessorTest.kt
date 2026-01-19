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

package com.algorand.android.modules.addaccount.joint.creation.ui.namejointaccount.viewmodel

import com.algorand.android.R
import com.algorand.android.deviceregistration.domain.usecase.DeviceIdUseCase
import com.algorand.android.models.Result
import com.algorand.android.modules.addaccount.joint.creation.domain.exception.JointAccountValidationException
import com.algorand.android.modules.addaccount.joint.creation.domain.usecase.DeleteInboxJointInvitationNotification
import com.algorand.wallet.account.core.domain.usecase.AddJointAccount
import com.algorand.wallet.account.custom.domain.model.AccountOrderIndex
import com.algorand.wallet.account.custom.domain.usecase.GetAllAccountOrderIndexes
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.jointaccount.domain.usecase.GetJointAccount
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

internal class DefaultNameJointAccountProcessorTest {

    private val getAllAccountOrderIndexes: GetAllAccountOrderIndexes = mockk()
    private val getJointAccount: GetJointAccount = mockk()
    private val addJointAccount: AddJointAccount = mockk()
    private val deviceIdUseCase: DeviceIdUseCase = mockk()
    private val deleteInboxJointInvitationNotification: DeleteInboxJointInvitationNotification = mockk()

    private val sut = DefaultNameJointAccountProcessor(
        getAllAccountOrderIndexes = getAllAccountOrderIndexes,
        getJointAccount = getJointAccount,
        addJointAccount = addJointAccount,
        deviceIdUseCase = deviceIdUseCase,
        deleteInboxJointInvitationNotification = deleteInboxJointInvitationNotification
    )

    @Before
    fun setup() {
        mockkStatic(android.util.Log::class)
        io.mockk.every { android.util.Log.e(any(), any(), any()) } returns 0
        io.mockk.every { android.util.Log.w(any(), any<String>(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkStatic(android.util.Log::class)
    }

    @Test
    fun `EXPECT validation error res id WHEN exception is JointAccountValidationException`() {
        val result = sut.mapExceptionToErrorResId(
            JointAccountValidationException.InsufficientParticipants()
        )

        assertEquals(R.string.joint_account_validation_insufficient_participants, result)
    }

    @Test
    fun `EXPECT internet connection error res id WHEN exception is IOException`() {
        val result = sut.mapExceptionToErrorResId(IOException())

        assertEquals(R.string.the_internet_connection, result)
    }

    @Test
    fun `EXPECT generic error res id WHEN exception is unknown`() {
        val result = sut.mapExceptionToErrorResId(RuntimeException())

        assertEquals(R.string.an_error_occurred, result)
    }

    @Test
    fun `EXPECT AlreadyExists WHEN account already exists`() = runTest {
        val existingAccount = mockk<LocalAccount.Joint>()
        coEvery { getJointAccount(TEST_ADDRESS) } returns existingAccount
        coEvery { deviceIdUseCase.getSelectedNodeDeviceId() } returns TEST_DEVICE_ID
        coEvery {
            deleteInboxJointInvitationNotification(TEST_DEVICE_ID_LONG, TEST_ADDRESS)
        } returns Result.Success(Unit)

        val result = sut.createLocalAccount(
            jointAccountAddress = TEST_ADDRESS,
            participantAddresses = TEST_PARTICIPANTS,
            threshold = TEST_THRESHOLD,
            version = TEST_VERSION,
            accountName = "Test Account"
        )

        assertTrue(result is NameJointAccountProcessor.CreateLocalAccountResult.AlreadyExists)
    }

    @Test
    fun `EXPECT Success WHEN account is created successfully`() = runTest {
        coEvery { getJointAccount(TEST_ADDRESS) } returns null
        coEvery { getAllAccountOrderIndexes() } returns emptyList()
        coEvery {
            addJointAccount(
                address = TEST_ADDRESS,
                participantAddresses = TEST_PARTICIPANTS,
                threshold = TEST_THRESHOLD,
                version = TEST_VERSION,
                customName = "Test Account",
                orderIndex = 0
            )
        } returns Unit
        coEvery { deviceIdUseCase.getSelectedNodeDeviceId() } returns TEST_DEVICE_ID
        coEvery {
            deleteInboxJointInvitationNotification(TEST_DEVICE_ID_LONG, TEST_ADDRESS)
        } returns Result.Success(Unit)

        val result = sut.createLocalAccount(
            jointAccountAddress = TEST_ADDRESS,
            participantAddresses = TEST_PARTICIPANTS,
            threshold = TEST_THRESHOLD,
            version = TEST_VERSION,
            accountName = "Test Account"
        )

        assertTrue(result is NameJointAccountProcessor.CreateLocalAccountResult.Success)
    }

    @Test
    fun `EXPECT Error WHEN addJointAccount throws exception`() = runTest {
        coEvery { getJointAccount(TEST_ADDRESS) } returns null
        coEvery { getAllAccountOrderIndexes() } returns emptyList()
        coEvery {
            addJointAccount(any(), any(), any(), any(), any(), any())
        } throws RuntimeException("Database error")

        val result = sut.createLocalAccount(
            jointAccountAddress = TEST_ADDRESS,
            participantAddresses = TEST_PARTICIPANTS,
            threshold = TEST_THRESHOLD,
            version = TEST_VERSION,
            accountName = "Test Account"
        )

        assertTrue(result is NameJointAccountProcessor.CreateLocalAccountResult.Error)
    }

    @Test
    fun `EXPECT next order index calculated correctly WHEN accounts exist`() = runTest {
        coEvery { getJointAccount(TEST_ADDRESS) } returns null
        coEvery { getAllAccountOrderIndexes() } returns listOf(
            AccountOrderIndex("ADDR1", 0),
            AccountOrderIndex("ADDR2", 5),
            AccountOrderIndex("ADDR3", 3)
        )
        coEvery {
            addJointAccount(
                address = TEST_ADDRESS,
                participantAddresses = TEST_PARTICIPANTS,
                threshold = TEST_THRESHOLD,
                version = TEST_VERSION,
                customName = "Test",
                orderIndex = 6
            )
        } returns Unit
        coEvery { deviceIdUseCase.getSelectedNodeDeviceId() } returns null

        sut.createLocalAccount(
            jointAccountAddress = TEST_ADDRESS,
            participantAddresses = TEST_PARTICIPANTS,
            threshold = TEST_THRESHOLD,
            version = TEST_VERSION,
            accountName = "Test"
        )

        coVerify {
            addJointAccount(
                address = TEST_ADDRESS,
                participantAddresses = TEST_PARTICIPANTS,
                threshold = TEST_THRESHOLD,
                version = TEST_VERSION,
                customName = "Test",
                orderIndex = 6
            )
        }
    }

    @Test
    fun `EXPECT customName null WHEN account name is blank`() = runTest {
        coEvery { getJointAccount(TEST_ADDRESS) } returns null
        coEvery { getAllAccountOrderIndexes() } returns emptyList()
        coEvery {
            addJointAccount(
                address = TEST_ADDRESS,
                participantAddresses = TEST_PARTICIPANTS,
                threshold = TEST_THRESHOLD,
                version = TEST_VERSION,
                customName = null,
                orderIndex = 0
            )
        } returns Unit
        coEvery { deviceIdUseCase.getSelectedNodeDeviceId() } returns null

        sut.createLocalAccount(
            jointAccountAddress = TEST_ADDRESS,
            participantAddresses = TEST_PARTICIPANTS,
            threshold = TEST_THRESHOLD,
            version = TEST_VERSION,
            accountName = "   "
        )

        coVerify {
            addJointAccount(
                address = TEST_ADDRESS,
                participantAddresses = TEST_PARTICIPANTS,
                threshold = TEST_THRESHOLD,
                version = TEST_VERSION,
                customName = null,
                orderIndex = 0
            )
        }
    }

    private companion object {
        const val TEST_ADDRESS = "JOINT_ADDRESS_123"
        const val TEST_DEVICE_ID = "12345"
        const val TEST_DEVICE_ID_LONG = 12345L
        val TEST_PARTICIPANTS = listOf("ADDR1", "ADDR2", "ADDR3")
        const val TEST_THRESHOLD = 2
        const val TEST_VERSION = 1
    }
}
