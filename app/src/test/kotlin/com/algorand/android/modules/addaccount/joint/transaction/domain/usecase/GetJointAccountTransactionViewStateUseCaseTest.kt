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
import com.algorand.android.assetsearch.ui.model.VerificationTierConfiguration
import com.algorand.android.modules.accountcore.ui.model.AccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountDisplayName
import com.algorand.android.modules.accountcore.ui.usecase.GetAccountIconDrawablePreview
import com.algorand.android.modules.accounticon.ui.model.AccountIconDrawablePreview
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountSignatureStatus
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountSignerItem
import com.algorand.android.modules.addaccount.joint.transaction.model.JointAccountSignRequestCenterPreview
import com.algorand.android.modules.verificationtier.ui.decider.VerificationTierConfigurationDecider
import com.algorand.android.ui.device.model.DeviceConfig
import com.algorand.android.ui.device.usecase.GetDeviceConfig
import com.algorand.wallet.account.info.domain.usecase.GetAccountRekeyAdminAddress
import com.algorand.wallet.account.local.domain.model.LocalAccount
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccounts
import com.algorand.wallet.account.local.domain.usecase.GetLocalAccountsAddresses
import com.algorand.wallet.algosdk.transaction.model.AlgorandAddress
import com.algorand.wallet.algosdk.transaction.model.RawTransaction
import com.algorand.wallet.algosdk.transaction.model.RawTransactionType
import com.algorand.wallet.algosdk.transaction.usecase.ParseTransactionMessagePack
import com.algorand.wallet.asset.domain.model.Asset
import com.algorand.wallet.asset.domain.model.VerificationTier
import com.algorand.wallet.asset.domain.usecase.FetchAsset
import com.algorand.wallet.foundation.PeraResult
import com.algorand.wallet.jointaccount.creation.domain.model.JointAccount
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestStatus
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestType
import com.algorand.wallet.jointaccount.transaction.domain.model.SignRequestWithFullSignature
import com.algorand.wallet.jointaccount.transaction.domain.model.TransactionListWithFullSignature
import com.algorand.wallet.jointaccount.transaction.domain.usecase.GetSignRequestWithSignatures
import com.algorand.wallet.utils.date.TimeProvider
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigInteger
import java.time.ZonedDateTime

internal class GetJointAccountTransactionViewStateUseCaseTest {

    @Before
    fun setup() {
        mockkStatic(android.util.Base64::class)
        every { android.util.Base64.decode(any<String>(), any()) } answers {
            java.util.Base64.getDecoder().decode(firstArg<String>())
        }
        every {
            verificationTierConfigurationDecider.decideVerificationTierConfiguration(any())
        } returns VerificationTierConfiguration.UNVERIFIED
    }

    @After
    fun tearDown() {
        unmockkStatic(android.util.Base64::class)
    }

    private val getSignRequestWithSignatures: GetSignRequestWithSignatures = mockk()
    private val parseTransactionMessagePack: ParseTransactionMessagePack = mockk()
    private val getAccountDisplayName: GetAccountDisplayName = mockk()
    private val getAccountIconDrawablePreview: GetAccountIconDrawablePreview = mockk()
    private val getDeviceConfig: GetDeviceConfig = mockk()
    private val getLocalAccountsAddresses: GetLocalAccountsAddresses = mockk()
    private val getLocalAccounts: GetLocalAccounts = mockk()
    private val getJointAccountSignerItems: GetJointAccountSignerItems = mockk()
    private val getAccountRekeyAdminAddress: GetAccountRekeyAdminAddress = mockk()
    private val formatAlgoAsDisplayCurrency: FormatAlgoAsDisplayCurrency = mockk()
    private val timeProvider: TimeProvider = mockk()
    private val resources: Resources = mockk()
    private val fetchAsset: FetchAsset = mockk()
    private val verificationTierConfigurationDecider: VerificationTierConfigurationDecider = mockk()

    private val dependencies = GetJointAccountTransactionViewStateDependencies(
        getSignRequestWithSignatures = getSignRequestWithSignatures,
        parseTransactionMessagePack = parseTransactionMessagePack,
        getAccountDisplayName = getAccountDisplayName,
        getAccountIconDrawablePreview = getAccountIconDrawablePreview,
        getDeviceConfig = getDeviceConfig,
        getLocalAccountsAddresses = getLocalAccountsAddresses,
        getLocalAccounts = getLocalAccounts,
        getJointAccountSignerItems = getJointAccountSignerItems,
        getAccountRekeyAdminAddress = getAccountRekeyAdminAddress,
        formatAlgoAsDisplayCurrency = formatAlgoAsDisplayCurrency,
        timeProvider = timeProvider,
        fetchAsset = fetchAsset,
        verificationTierConfigurationDecider = verificationTierConfigurationDecider
    )

    private val sut = GetJointAccountTransactionViewStateUseCase(
        dependencies = dependencies,
        resources = resources
    )

    @Test
    fun `EXPECT error WHEN device id is blank`() = runTest {
        coEvery { getDeviceConfig() } returns createDeviceConfig("")

        val result = sut(TEST_SIGN_REQUEST_ID)

        assertTrue(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT error WHEN device id is not a valid number`() = runTest {
        coEvery { getDeviceConfig() } returns createDeviceConfig("invalid")

        val result = sut(TEST_SIGN_REQUEST_ID)

        assertTrue(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT error WHEN getSignRequestWithSignatures fails`() = runTest {
        coEvery { getDeviceConfig() } returns createDeviceConfig("123")
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
        coEvery { getDeviceConfig() } returns createDeviceConfig("123")
        coEvery { getSignRequestWithSignatures(123L, TEST_SIGN_REQUEST_ID) } returns PeraResult.Success(
            createSignRequest(jointAccount = null)
        )

        val result = sut(TEST_SIGN_REQUEST_ID)

        assertTrue(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT error WHEN joint account address is null`() = runTest {
        coEvery { getDeviceConfig() } returns createDeviceConfig("123")
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
        coEvery { getDeviceConfig() } returns createDeviceConfig("123")
        coEvery { getSignRequestWithSignatures(123L, TEST_SIGN_REQUEST_ID) } returns PeraResult.Success(
            createSignRequest(transactionLists = null)
        )

        val result = sut(TEST_SIGN_REQUEST_ID)

        assertTrue(result is PeraResult.Error)
    }

    @Test
    fun `EXPECT success WHEN valid sign request returned`() = runTest {
        coEvery { getDeviceConfig() } returns createDeviceConfig("123")
        coEvery { getSignRequestWithSignatures(123L, TEST_SIGN_REQUEST_ID) } returns PeraResult.Success(
            createValidSignRequest()
        )
        coEvery { getLocalAccountsAddresses() } returns listOf(TEST_PARTICIPANT_ADDRESS)
        coEvery { getLocalAccounts() } returns listOf(createLocalAccount())
        coEvery { getJointAccountSignerItems(listOf(TEST_PARTICIPANT_ADDRESS), emptyList()) } returns listOf(
            createSignerItem()
        )
        coEvery { getJointAccountSignerItems.hasSigningCapableLocalAccount(TEST_PARTICIPANT_ADDRESS) } returns true
        coEvery { getAccountRekeyAdminAddress(TEST_PARTICIPANT_ADDRESS) } returns null
        coEvery { getAccountDisplayName(TEST_JOINT_ADDRESS) } returns createAccountDisplayName()
        coEvery { getAccountIconDrawablePreview(TEST_JOINT_ADDRESS) } returns mockk<AccountIconDrawablePreview>()
        every { parseTransactionMessagePack(any()) } returns null
        every { formatAlgoAsDisplayCurrency(any()) } returns "$0.00"
        every { resources.getString(any()) } returns "0m"
        every { resources.getString(any(), any()) } returns "0m"
        every { timeProvider.getZonedDateTimeNow() } returns ZonedDateTime.parse("2025-01-01T00:00:00Z")

        val result = sut(TEST_SIGN_REQUEST_ID)

        assertTrue(result is PeraResult.Success)
        val viewState = (result as PeraResult.Success).data
        assertEquals(TEST_JOINT_ADDRESS, viewState.jointAccountDisplayName.accountAddress)
        assertTrue(viewState.centerPreview is JointAccountSignRequestCenterPreview.Transfer)
    }

    @Test
    fun `EXPECT asset opt-in center preview WHEN transaction is zero amount self asset transfer`() = runTest {
        coEvery { getDeviceConfig() } returns createDeviceConfig("123")
        coEvery { getSignRequestWithSignatures(123L, TEST_SIGN_REQUEST_ID) } returns PeraResult.Success(
            createSignRequest(
                transactionLists = listOf(
                    TransactionListWithFullSignature(
                        rawTransactions = listOf("dGVzdA=="),
                        firstValidBlock = null,
                        lastValidBlock = null,
                        responses = emptyList(),
                        lastValidExpectedDatetime = null
                    )
                )
            )
        )
        coEvery { getLocalAccountsAddresses() } returns listOf(TEST_PARTICIPANT_ADDRESS)
        coEvery { getLocalAccounts() } returns listOf(createLocalAccount())
        coEvery { getJointAccountSignerItems(listOf(TEST_PARTICIPANT_ADDRESS), emptyList()) } returns listOf(
            createSignerItem()
        )
        coEvery { getJointAccountSignerItems.hasSigningCapableLocalAccount(TEST_PARTICIPANT_ADDRESS) } returns true
        coEvery { getAccountRekeyAdminAddress(TEST_PARTICIPANT_ADDRESS) } returns null
        coEvery { getAccountDisplayName(TEST_JOINT_ADDRESS) } returns createAccountDisplayName()
        coEvery { getAccountIconDrawablePreview(TEST_JOINT_ADDRESS) } returns mockk<AccountIconDrawablePreview>()
        every { parseTransactionMessagePack(any()) } returns createAssetOptInRawTransaction()
        every { formatAlgoAsDisplayCurrency(any()) } returns "$0.00"
        every { resources.getString(any()) } returns "0m"
        every { resources.getString(any(), any()) } returns "0m"
        every { timeProvider.getZonedDateTimeNow() } returns ZonedDateTime.parse("2025-01-01T00:00:00Z")
        val mockAsset = mockk<Asset>()
        every { mockAsset.id } returns TEST_ASSET_ID
        every { mockAsset.isAlgo } returns false
        every { mockAsset.fullName } returns "LOUDeFi"
        every { mockAsset.shortName } returns "LOUD"
        every { mockAsset.logoUri } returns null
        every { mockAsset.verificationTier } returns VerificationTier.VERIFIED
        coEvery { fetchAsset(TEST_ASSET_ID) } returns PeraResult.Success(mockAsset)

        val result = sut(TEST_SIGN_REQUEST_ID)

        assertTrue(result is PeraResult.Success)
        val viewState = (result as PeraResult.Success).data
        val center = viewState.centerPreview as JointAccountSignRequestCenterPreview.AssetAction
        assertEquals(JointAccountSignRequestCenterPreview.AssetAction.Type.OPT_IN, center.type)
        assertEquals("LOUDeFi", center.assetName)
        assertEquals("LOUD", center.assetUnitName)
        assertEquals(TEST_ASSET_ID.toString(), center.assetIdText)
        assertEquals(TEST_JOINT_ADDRESS, viewState.addressForClipboard)
    }

    @Test
    fun `EXPECT asset opt-out center preview WHEN transaction has asset close-to`() = runTest {
        coEvery { getDeviceConfig() } returns createDeviceConfig("123")
        coEvery { getSignRequestWithSignatures(123L, TEST_SIGN_REQUEST_ID) } returns PeraResult.Success(
            createSignRequest(
                transactionLists = listOf(
                    TransactionListWithFullSignature(
                        rawTransactions = listOf("dGVzdA=="),
                        firstValidBlock = null,
                        lastValidBlock = null,
                        responses = emptyList(),
                        lastValidExpectedDatetime = null
                    )
                )
            )
        )
        coEvery { getLocalAccountsAddresses() } returns listOf(TEST_PARTICIPANT_ADDRESS)
        coEvery { getLocalAccounts() } returns listOf(createLocalAccount())
        coEvery { getJointAccountSignerItems(listOf(TEST_PARTICIPANT_ADDRESS), emptyList()) } returns listOf(
            createSignerItem()
        )
        coEvery { getJointAccountSignerItems.hasSigningCapableLocalAccount(TEST_PARTICIPANT_ADDRESS) } returns true
        coEvery { getAccountRekeyAdminAddress(TEST_PARTICIPANT_ADDRESS) } returns null
        coEvery { getAccountDisplayName(TEST_JOINT_ADDRESS) } returns createAccountDisplayName()
        coEvery { getAccountIconDrawablePreview(TEST_JOINT_ADDRESS) } returns mockk<AccountIconDrawablePreview>()
        every { parseTransactionMessagePack(any()) } returns createAssetOptOutRawTransaction()
        every { formatAlgoAsDisplayCurrency(any()) } returns "$0.00"
        every { resources.getString(any()) } returns "0m"
        every { resources.getString(any(), any()) } returns "0m"
        every { timeProvider.getZonedDateTimeNow() } returns ZonedDateTime.parse("2025-01-01T00:00:00Z")
        val mockAsset = mockk<Asset>()
        every { mockAsset.id } returns TEST_ASSET_ID
        every { mockAsset.isAlgo } returns false
        every { mockAsset.fullName } returns "USDC"
        every { mockAsset.shortName } returns "USDC"
        every { mockAsset.logoUri } returns null
        every { mockAsset.verificationTier } returns VerificationTier.VERIFIED
        coEvery { fetchAsset(TEST_ASSET_ID) } returns PeraResult.Success(mockAsset)

        val result = sut(TEST_SIGN_REQUEST_ID)

        assertTrue(result is PeraResult.Success)
        val viewState = (result as PeraResult.Success).data
        val center = viewState.centerPreview as JointAccountSignRequestCenterPreview.AssetAction
        assertEquals(JointAccountSignRequestCenterPreview.AssetAction.Type.OPT_OUT, center.type)
        assertEquals("USDC", center.assetName)
        assertEquals(TEST_ASSET_ID.toString(), center.assetIdText)
        assertEquals(TEST_JOINT_ADDRESS, viewState.addressForClipboard)
    }

    @Test
    fun `EXPECT correct threshold WHEN valid sign request returned`() = runTest {
        coEvery { getDeviceConfig() } returns createDeviceConfig("123")
        coEvery { getSignRequestWithSignatures(123L, TEST_SIGN_REQUEST_ID) } returns PeraResult.Success(
            createValidSignRequest(threshold = 3)
        )
        coEvery { getLocalAccountsAddresses() } returns listOf(TEST_PARTICIPANT_ADDRESS)
        coEvery { getLocalAccounts() } returns listOf(createLocalAccount())
        coEvery { getJointAccountSignerItems(listOf(TEST_PARTICIPANT_ADDRESS), emptyList()) } returns emptyList()
        coEvery { getJointAccountSignerItems.hasSigningCapableLocalAccount(TEST_PARTICIPANT_ADDRESS) } returns false
        coEvery { getAccountRekeyAdminAddress(TEST_PARTICIPANT_ADDRESS) } returns null
        coEvery { getAccountDisplayName(TEST_JOINT_ADDRESS) } returns createAccountDisplayName()
        coEvery { getAccountIconDrawablePreview(TEST_JOINT_ADDRESS) } returns mockk<AccountIconDrawablePreview>()
        every { parseTransactionMessagePack(any()) } returns null
        every { formatAlgoAsDisplayCurrency(any()) } returns "$0.00"
        every { resources.getString(any()) } returns "0m"
        every { resources.getString(any(), any()) } returns "0m"
        every { timeProvider.getZonedDateTimeNow() } returns ZonedDateTime.parse("2025-01-01T00:00:00Z")

        val result = sut(TEST_SIGN_REQUEST_ID)

        assertTrue(result is PeraResult.Success)
        val viewState = (result as PeraResult.Success).data
        assertEquals(3, viewState.requiredSignatureCount)
    }

    private fun createBaseAssetRawTransaction(): RawTransaction {
        val addr = AlgorandAddress(decodedAddress = TEST_JOINT_ADDRESS, addressPublicKey = null)
        return RawTransaction(
            amount = null,
            fee = 1000L,
            firstValidRound = null,
            genesisId = null,
            genesisHash = null,
            lastValidRound = null,
            note = null,
            receiverAddress = null,
            senderAddress = addr,
            transactionType = RawTransactionType.ASSET_TRANSACTION,
            closeToAddress = null,
            rekeyAddress = null,
            assetCloseToAddress = null,
            assetReceiverAddress = addr,
            assetAmount = BigInteger.ZERO,
            assetId = TEST_ASSET_ID,
            appArgs = null,
            appOnComplete = null,
            appId = null,
            appGlobalSchema = null,
            appLocalSchema = null,
            appExtraPages = null,
            approvalHash = null,
            stateHash = null,
            assetIdBeingConfigured = null,
            assetConfigParameters = null,
            groupId = null,
            rejectVersion = null,
            accessListSize = null,
            innerTransactions = null
        )
    }

    private fun createAssetOptInRawTransaction(): RawTransaction = createBaseAssetRawTransaction()

    private fun createAssetOptOutRawTransaction(): RawTransaction {
        val closeAddr = AlgorandAddress(decodedAddress = "CLOSE_TO_ADDRESS_456", addressPublicKey = null)
        return createBaseAssetRawTransaction().copy(assetCloseToAddress = closeAddr)
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
            id = "1",
            type = SignRequestType.ASYNC,
            jointAccount = jointAccount,
            proposerAddress = TEST_PARTICIPANT_ADDRESS,
            lastValidExpectedDatetime = null,
            transactionLists = transactionLists,
            status = SignRequestStatus.PENDING
        )
    }

    private fun createValidSignRequest(threshold: Int = 2): SignRequestWithFullSignature {
        return SignRequestWithFullSignature(
            id = "1",
            type = SignRequestType.ASYNC,
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

    private fun createDeviceConfig(deviceId: String): DeviceConfig {
        return mockk {
            every { this@mockk.deviceId } returns deviceId
        }
    }

    private companion object {
        const val TEST_SIGN_REQUEST_ID = "sign_request_123"
        const val TEST_JOINT_ADDRESS = "JOINT_ADDRESS_123"
        const val TEST_PARTICIPANT_ADDRESS = "PARTICIPANT_ADDRESS"
        const val TEST_ASSET_ID = 457819394L
    }
}
