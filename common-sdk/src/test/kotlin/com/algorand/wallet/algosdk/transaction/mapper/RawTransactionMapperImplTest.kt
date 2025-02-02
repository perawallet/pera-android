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

package com.algorand.wallet.algosdk.transaction.mapper

import com.algorand.test.peraFixture
import com.algorand.wallet.algosdk.transaction.model.AlgorandAddress
import com.algorand.wallet.algosdk.transaction.model.ApplicationCallStateSchema
import com.algorand.wallet.algosdk.transaction.model.AssetConfigParameters
import com.algorand.wallet.algosdk.transaction.model.RawTransaction
import com.algorand.wallet.algosdk.transaction.model.RawTransactionType
import com.algorand.wallet.algosdk.transaction.model.payload.RawTransactionApplicationCallStateSchemaPayload
import com.algorand.wallet.algosdk.transaction.model.payload.RawTransactionAssetConfigParametersPayload
import com.algorand.wallet.algosdk.transaction.model.payload.RawTransactionPayload
import com.algorand.wallet.algosdk.transaction.model.payload.RawTransactionTypePayload
import com.algorand.wallet.algosdk.transaction.sdk.AlgoSdkAddress
import io.mockk.every
import io.mockk.mockk
import java.math.BigInteger
import java.math.BigInteger.ONE
import java.math.BigInteger.TEN
import java.math.BigInteger.TWO
import java.math.BigInteger.ZERO
import org.junit.Assert.assertEquals
import org.junit.Test

class RawTransactionMapperImplTest {

    private val algoSdkAddress: AlgoSdkAddress = mockk()

    private val rawTransactionTypeMapper: RawTransactionTypeMapper = mockk {
        every { invoke(payload = RAW_TXN_TYPE_PAYLOAD) } returns RAW_TXN_TYPE
    }
    private val appCallStateSchemaMapper: ApplicationCallStateSchemaMapper = mockk {
        every { invoke(payload = APP_GLOBAL_SCHEMA_PAYLOAD) } returns APP_GLOBAL_SCHEMA
        every { invoke(payload = APP_LOCAL_SCHEMA_PAYLOAD) } returns APP_LOCAL_SCHEMA
    }
    private val assetConfigParametersMapper: AssetConfigParametersMapper = mockk {
        every { invoke(payload = ASSET_CONFIG_PARAMETERS_PAYLOAD) } returns ASSET_CONFIG_PARAMETERS
    }

    private val sut = RawTransactionMapperImpl(
        algoSdkAddress,
        rawTransactionTypeMapper,
        assetConfigParametersMapper,
        appCallStateSchemaMapper
    )

    @Test
    fun `EXPECT raw txn to be mapped`() {
        val result = sut(PAYLOAD)

        assertEquals(RAW_TXN, result)
    }

    @Test
    fun `EXPECT raw txn to be mapped with addresses WHEN addresses are not null`() {
        val payload = PAYLOAD.copy(
            receiverAddress = "receiverAddress",
            senderAddress = "senderAddress",
            closeToAddress = "closeToAddress",
            rekeyAddress = "rekeyAddress",
            assetCloseToAddress = "assetCloseToAddress",
            assetReceiverAddress = "assetReceiverAddress"
        )
        initAddressMocks()

        val result = sut(payload)

        val expected = RAW_TXN.copy(
            receiverAddress = RECEIVER_ADDRESS,
            senderAddress = SENDER_ADDRESS,
            closeToAddress = CLOSE_TO_ADDRESS,
            rekeyAddress = REKEY_ADDRESS,
            assetCloseToAddress = ASSET_CLOSE_TO_ADDRESS,
            assetReceiverAddress = ASSET_RECEIVER_ADDRESS
        )
        assertEquals(expected, result)
    }

    private fun initAddressMocks() {
        every { algoSdkAddress.generateAddressFromPublicKey("receiverAddress") } returns RECEIVER_ADDRESS
        every { algoSdkAddress.generateAddressFromPublicKey("senderAddress") } returns SENDER_ADDRESS
        every { algoSdkAddress.generateAddressFromPublicKey("closeToAddress") } returns CLOSE_TO_ADDRESS
        every { algoSdkAddress.generateAddressFromPublicKey("rekeyAddress") } returns REKEY_ADDRESS
        every {
            algoSdkAddress.generateAddressFromPublicKey("assetCloseToAddress")
        } returns ASSET_CLOSE_TO_ADDRESS
        every {
            algoSdkAddress.generateAddressFromPublicKey("assetReceiverAddress")
        } returns ASSET_RECEIVER_ADDRESS
    }

    private companion object {
        val RAW_TXN_TYPE_PAYLOAD = peraFixture<RawTransactionTypePayload>()
        val RAW_TXN_TYPE = peraFixture<RawTransactionType>()

        val AMOUNT = peraFixture<String>()
        val FEE = peraFixture<Long?>()
        val FIRST_VALID_ROUND = peraFixture<Long?>()
        val LAST_VALID_ROUND = peraFixture<Long?>()
        val GENESIS_HASH = peraFixture<String?>()
        val GENESIS_ID = peraFixture<String?>()
        val NOTE = peraFixture<String?>()
        val ASSET_AMOUNT = peraFixture<BigInteger?>()
        val ASSET_ID = peraFixture<Long?>()
        val APP_ARGS = peraFixture<List<String>?>()
        val APP_ON_COMPLETE = peraFixture<Int?>()
        val APP_ID = peraFixture<Long?>()
        val APP_GLOBAL_SCHEMA_PAYLOAD = RawTransactionApplicationCallStateSchemaPayload(ZERO, TEN)
        val APP_GLOBAL_SCHEMA = ApplicationCallStateSchema(ZERO, TEN)
        val APP_LOCAL_SCHEMA_PAYLOAD = RawTransactionApplicationCallStateSchemaPayload(ONE, TWO)
        val APP_LOCAL_SCHEMA = ApplicationCallStateSchema(ONE, TWO)
        val APP_EXTRA_PAGES = peraFixture<Int?>()
        val APPROVAL_HASH = peraFixture<String?>()
        val STATE_HASH = peraFixture<String?>()
        val ASSET_ID_BEING_CONFIGURED = peraFixture<Long?>()
        val ASSET_CONFIG_PARAMETERS_PAYLOAD = peraFixture<RawTransactionAssetConfigParametersPayload>()
        val ASSET_CONFIG_PARAMETERS = peraFixture<AssetConfigParameters>()

        val RECEIVER_ADDRESS = AlgorandAddress(decodedAddress = "receiverAddress", addressPublicKey = null)
        val SENDER_ADDRESS = AlgorandAddress(decodedAddress = "senderAddress", addressPublicKey = null)
        val CLOSE_TO_ADDRESS = AlgorandAddress(decodedAddress = "closeToAddress", addressPublicKey = null)
        val REKEY_ADDRESS = AlgorandAddress(decodedAddress = "rekeyAddress", addressPublicKey = null)
        val ASSET_CLOSE_TO_ADDRESS = AlgorandAddress(decodedAddress = "assetCloseToAddress", addressPublicKey = null)
        val ASSET_RECEIVER_ADDRESS = AlgorandAddress(decodedAddress = "assetReceiverAddress", addressPublicKey = null)

        val GROUP_ID = peraFixture<String?>()

        val PAYLOAD = RawTransactionPayload(
            amount = AMOUNT,
            fee = FEE,
            firstValidRound = FIRST_VALID_ROUND,
            lastValidRound = LAST_VALID_ROUND,
            genesisHash = GENESIS_HASH,
            genesisId = GENESIS_ID,
            note = NOTE,
            receiverAddress = null,
            senderAddress = null,
            transactionType = RAW_TXN_TYPE_PAYLOAD,
            closeToAddress = null,
            rekeyAddress = null,
            assetCloseToAddress = null,
            assetReceiverAddress = null,
            assetAmount = ASSET_AMOUNT,
            assetId = ASSET_ID,
            appArgs = APP_ARGS,
            appOnComplete = APP_ON_COMPLETE,
            appId = APP_ID,
            appGlobalSchema = APP_GLOBAL_SCHEMA_PAYLOAD,
            appLocalSchema = APP_LOCAL_SCHEMA_PAYLOAD,
            appExtraPages = APP_EXTRA_PAGES,
            approvalHash = APPROVAL_HASH,
            stateHash = STATE_HASH,
            assetIdBeingConfigured = ASSET_ID_BEING_CONFIGURED,
            decodedAssetConfigParameters = ASSET_CONFIG_PARAMETERS_PAYLOAD,
            groupId = GROUP_ID
        )

        val RAW_TXN = RawTransaction(
            amount = AMOUNT,
            fee = FEE,
            firstValidRound = FIRST_VALID_ROUND,
            lastValidRound = LAST_VALID_ROUND,
            genesisHash = GENESIS_HASH,
            genesisId = GENESIS_ID,
            note = NOTE,
            receiverAddress = null,
            senderAddress = null,
            transactionType = RAW_TXN_TYPE,
            closeToAddress = null,
            rekeyAddress = null,
            assetCloseToAddress = null,
            assetReceiverAddress = null,
            assetAmount = ASSET_AMOUNT,
            assetId = ASSET_ID,
            appArgs = APP_ARGS,
            appOnComplete = APP_ON_COMPLETE,
            appId = APP_ID,
            appGlobalSchema = APP_GLOBAL_SCHEMA,
            appLocalSchema = APP_LOCAL_SCHEMA,
            appExtraPages = APP_EXTRA_PAGES,
            approvalHash = APPROVAL_HASH,
            stateHash = STATE_HASH,
            assetIdBeingConfigured = ASSET_ID_BEING_CONFIGURED,
            assetConfigParameters = ASSET_CONFIG_PARAMETERS,
            groupId = GROUP_ID
        )
    }
}
