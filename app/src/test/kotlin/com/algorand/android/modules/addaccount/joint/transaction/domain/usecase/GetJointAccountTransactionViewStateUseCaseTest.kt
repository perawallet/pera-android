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

package com.algorand.android.modules.addaccount.joint.transaction.domain.usecase

import android.content.res.Resources
import com.algorand.android.deviceregistration.domain.usecase.DeviceIdUseCase
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountSignatureStatus
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountSignerItem
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccounts
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccountsAddresses
import com.algorand.wallet.algosdk.transaction.usecase.ParseTransactionMessagePack
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.jointaccount.creation.domain.model.JointAccount
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestStatus
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestWithFullSignature
import com.algorand.wallet.jointaccount.transaction.domain.model.TransactionListWithFullSignature
import com.algorand.wallet.jointaccount.transaction.domain.usecase.GetSignRequestWithSignatures
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

internal class GetJointAccountTransactionViewStateUseCaseTest {

    private val getSignRequestWithSignatures: GetSignRequestWithSignatures = mockk()
    private val parseTransactionMessagePack: ParseTransactionMessagePack = mockk()
    private val getAccountDisplayName: GetAccountDisplayName = mockk()
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview = mockk()
    private val deviceIdUseCase: DeviceIdUseCase = mockk()
    private val getLocalAccountsAddresses: GetLocalAccountsAddresses = mockk()
    private val getLocalAccounts: GetLocalAccounts = mockk()
    private val getJointAccountSignerItems: GetJointAccountSignerItems = mockk()
    private val formatAlgoAsDisplayCurrency: FormatAlgoAsDisplayCurrency = mockk()
    private val resources: Resources = mockk()

    private val dependencies = GetJointAccountTransactionViewStateDependencies(
        getSignRequestWithSignatures = getSignRequestWithSignatures,
        parseTransactionMessagePack = parseTransactionMessagePack,
        getAccountDisplayName = getAccountDisplayName,
        getAccountIconDrawablePreview = getAccountIconDrawablePreview,
        deviceIdUseCase = deviceIdUseCase,
        getLocalAccountsAddresses = getLocalAccountsAddresses,
        getLocalAccounts = getLocalAccounts,
        getJointAccountSignerItems = getJointAccountSignerItems,
        formatAlgoAsDisplayCurrency = formatAlgoAsDisplayCurrency
    )

    private val sut = GetJointAccountTransactionViewStateUseCase(
        dependencies = dependencies,
        resources = resources
    )

    @Test
    fun `EXPECT error WHEN device id is null`() = runTest {
        every { deviceIdUseCase.getSelectedNodeDeviceId() } returns null

        val result = sut(TEST_SIGN_REQUEST_ID)

        assertTrue(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT error WHEN device id is not a valid number`() = runTest {
        every { deviceIdUseCase.getSelectedNodeDeviceId() } returns "invalid"

        val result = sut(TEST_SIGN_REQUEST_ID)

        assertTrue(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT error WHEN getSignRequestWithSignatures fails`() = runTest {
        every { deviceIdUseCase.getSelectedNodeDeviceId() } returns "123"
        coEvery {
            getSignRequestWithSignatures(
                123L,
                TEST_SIGN_REQUEST_ID
            )
        } returns PeraResult.Error(Exception("Error"))

        val result = sut(TEST_SIGN_REQUEST_ID)

        assertTrue(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT error WHEN joint account is null`() = runTest {
        every { deviceIdUseCase.getSelectedNodeDeviceId() } returns "123"
        coEvery { getSignRequestWithSignatures(123L, TEST_SIGN_REQUEST_ID) } returns PeraResult.Success(
            createSignRequest(jointAccount = null)
        )

        val result = sut(TEST_SIGN_REQUEST_ID)

        assertTrue(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT error WHEN joint account address is null`() = runTest {
        every { deviceIdUseCase.getSelectedNodeDeviceId() } returns "123"
        coEvery { getSignRequestWithSignatures(123L, TEST_SIGN_REQUEST_ID) } returns PeraResult.Success(
            createSignRequest(
                jointAccount = JointAccount(
                    creationDatetime = null,
                    address = null,
                    version = 1,
                    threshold = 2,
                    participantAddresses = emptyList()
                )
            )
        )

        val result = sut(TEST_SIGN_REQUEST_ID)

        assertTrue(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT error WHEN transaction lists is null`() = runTest {
        every { deviceIdUseCase.getSelectedNodeDeviceId() } returns "123"
        coEvery { getSignRequestWithSignatures(123L, TEST_SIGN_REQUEST_ID) } returns PeraResult.Success(
            createSignRequest(transactionLists = null)
        )

        val result = sut(TEST_SIGN_REQUEST_ID)

        assertTrue(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT success WHEN valid sign request returned`() = runTest {
        every { deviceIdUseCase.getSelectedNodeDeviceId() } returns "123"
        coEvery { getSignRequestWithSignatures(123L, TEST_SIGN_REQUEST_ID) } returns PeraResult.Success(
            createValidSignRequest()
        )
        coEvery { getLocalAccountsAddresses() } returns listOf(TEST_PARTICIPANT_ADDRESS)
        coEvery { getLocalAccounts() } returns listOf(createLocalAccount())
        coEvery { getJointAccountSignerItems(listOf(TEST_PARTICIPANT_ADDRESS), emptyList()) } returns listOf(
            createSignerItem()
        )
        coEvery { getJointAccountSignerItems.hasSigningCapableLocalAccount(TEST_PARTICIPANT_ADDRESS) } returns true
        coEvery { getAccountDisplayName(TEST_JOINT_ADDRESS) } returns createAccountDisplayName()
        coEvery { getAccountIconDrawablePreview(TEST_JOINT_ADDRESS) } returns mockk<AccountIconDrawablePreview>()
        every { parseTransactionMessagePack(any()) } returns null
        every { formatAlgoAsDisplayCurrency(any()) } returns "$0.00"
        every { resources.getString(any()) } returns "0m"
        every { resources.getString(any(), any()) } returns "0m"

        val result = sut(TEST_SIGN_REQUEST_ID)

        assertTrue(result is PeraResult.Success)
        val viewState = (result as PeraResult.Success).data
        assertEquals(TEST_JOINT_ADDRESS, viewState.jointAccountDisplayName.accountAddress)
    }

    @Test
    fun `EXPECT correct threshold WHEN valid sign request returned`() = runTest {
        every { deviceIdUseCase.getSelectedNodeDeviceId() } returns "123"
        coEvery { getSignRequestWithSignatures(123L, TEST_SIGN_REQUEST_ID) } returns PeraResult.Success(
            createValidSignRequest(threshold = 3)
        )
        coEvery { getLocalAccountsAddresses() } returns listOf(TEST_PARTICIPANT_ADDRESS)
        coEvery { getLocalAccounts() } returns listOf(createLocalAccount())
        coEvery { getJointAccountSignerItems(listOf(TEST_PARTICIPANT_ADDRESS), emptyList()) } returns emptyList()
        coEvery { getJointAccountSignerItems.hasSigningCapableLocalAccount(TEST_PARTICIPANT_ADDRESS) } returns false
        coEvery { getAccountDisplayName(TEST_JOINT_ADDRESS) } returns createAccountDisplayName()
        coEvery { getAccountIconDrawablePreview(TEST_JOINT_ADDRESS) } returns mockk<AccountIconDrawablePreview>()
        every { parseTransactionMessagePack(any()) } returns null
        every { formatAlgoAsDisplayCurrency(any()) } returns "$0.00"
        every { resources.getString(any()) } returns "0m"
        every { resources.getString(any(), any()) } returns "0m"

        val result = sut(TEST_SIGN_REQUEST_ID)

        assertTrue(result is PeraResult.Success)
        val viewState = (result as PeraResult.Success).data
        assertEquals(3, viewState.requiredSignatureCount)
    }

    private fun createSignRequest(
        jointAccount: JointAccount? = JointAccount(
            creationDatetime = null,
            address = TEST_JOINT_ADDRESS,
            version = 1,
            threshold = 2,
            participantAddresses = listOf(TEST_PARTICIPANT_ADDRESS)
        ),
        transactionLists: List<TransactionListWithFullSignature>? = listOf(
            TransactionListWithFullSignature(
                rawTransactions = emptyList(),
                firstValidBlock = null,
                lastValidBlock = null,
                responses = emptyList(),
                lastValidExpectedDatetime = null
            )
        )
    ): SignRequestWithFullSignature {
        return SignRequestWithFullSignature(
            id = 1L,
            type = "payment",
            jointAccount = jointAccount,
            proposerAddress = TEST_PARTICIPANT_ADDRESS,
            lastValidExpectedDatetime = null,
            transactionLists = transactionLists,
            status = SignRequestStatus.PENDING
        )
    }

    private fun createValidSignRequest(threshold: Int = 2): SignRequestWithFullSignature {
        return SignRequestWithFullSignature(
            id = 1L,
            type = "payment",
            jointAccount = JointAccount(
                creationDatetime = null,
                address = TEST_JOINT_ADDRESS,
                version = 1,
                threshold = threshold,
                participantAddresses = listOf(TEST_PARTICIPANT_ADDRESS)
            ),
            proposerAddress = TEST_PARTICIPANT_ADDRESS,
            lastValidExpectedDatetime = null,
            transactionLists = listOf(
                TransactionListWithFullSignature(
                    rawTransactions = emptyList(),
                    firstValidBlock = null,
                    lastValidBlock = null,
                    responses = emptyList(),
                    lastValidExpectedDatetime = null
                )
            ),
            status = SignRequestStatus.PENDING
        )
    }

    private fun createLocalAccount(): LocalAccount.Algo25 {
        return mockk {
            every { algoAddress } returns TEST_PARTICIPANT_ADDRESS
        }
    }

    private fun createSignerItem(): JointAccountSignerItem {
        return JointAccountSignerItem(
            accountAddress = TEST_PARTICIPANT_ADDRESS,
            accountDisplayName = AccountDisplayName(
                accountAddress = TEST_PARTICIPANT_ADDRESS,
                primaryDisplayName = "Participant",
                secondaryDisplayName = "PART...ADDR"
            ),
            accountIconDrawablePreview = mockk(),
            imageUri = null,
            signatureStatus = JointAccountSignatureStatus.Pending,
            showProgress = true
        )
    }

    private fun createAccountDisplayName(): AccountDisplayName {
        return AccountDisplayName(
            accountAddress = TEST_JOINT_ADDRESS,
            primaryDisplayName = "Joint Account",
            secondaryDisplayName = null
        )
    }

    private companion object {
        const val TEST_SIGN_REQUEST_ID = "sign_request_123"
        const val TEST_JOINT_ADDRESS = "JOINT_ADDRESS_123"
        const val TEST_PARTICIPANT_ADDRESS = "PARTICIPANT_ADDRESS"
    }
}
