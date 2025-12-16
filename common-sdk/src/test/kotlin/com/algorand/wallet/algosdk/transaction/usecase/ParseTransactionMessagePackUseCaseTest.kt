package com.algorand.wallet.algosdk.transaction.usecase

import com.algorand.test.peraFixture
import com.algorand.wallet.algosdk.transaction.mapper.RawTransactionMapper
import com.algorand.wallet.algosdk.transaction.model.ApplicationCallStateSchema
import com.algorand.wallet.algosdk.transaction.model.AssetConfigParameters
import com.algorand.wallet.algosdk.transaction.model.RawTransaction
import com.algorand.wallet.algosdk.transaction.model.RawTransactionType
import com.algorand.wallet.algosdk.transaction.model.payload.RawAccessItemPayload
import com.algorand.wallet.algosdk.transaction.model.payload.RawTransactionApplicationCallStateSchemaPayload
import com.algorand.wallet.algosdk.transaction.model.payload.RawTransactionAssetConfigParametersPayload
import com.algorand.wallet.algosdk.transaction.model.payload.RawTransactionPayload
import com.algorand.wallet.algosdk.transaction.model.payload.RawTransactionTypePayload
import com.algorand.wallet.algosdk.transaction.sdk.AlgoSdk
import com.algorand.wallet.foundation.json.JsonSerializer
import io.mockk.every
import io.mockk.mockk
import java.math.BigInteger
import java.math.BigInteger.ONE
import java.math.BigInteger.TEN
import java.math.BigInteger.TWO
import java.math.BigInteger.ZERO
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ParseTransactionMessagePackUseCaseTest {

    private val jsonSerializer: JsonSerializer = mockk()
    private val rawTransactionMapper: RawTransactionMapper = mockk()
    private val algoSdk: AlgoSdk = mockk()

    private val sut = ParseTransactionMessagePackUseCase(
        jsonSerializer,
        rawTransactionMapper,
        algoSdk
    )

    @Test
    fun `EXPECT null WHEN algoSdk throws exception`() {
        every { algoSdk.transactionMsgpackToJson(TXN_BYTE_ARRAY) } throws Exception()

        val result = sut(TXN_BYTE_ARRAY)

        assertNull(result)
    }

    @Test
    fun `EXPECT null WHEN json serialization fails`() {
        every { algoSdk.transactionMsgpackToJson(TXN_BYTE_ARRAY) } returns TXN_JSON
        every { jsonSerializer.fromJson(TXN_JSON, RawTransactionPayload::class.java) } returns null

        val result = sut(TXN_BYTE_ARRAY)

        assertNull(result)
    }

    @Test
    fun `EXPECT mapped raw txn`() {
        every { algoSdk.transactionMsgpackToJson(TXN_BYTE_ARRAY) } returns TXN_JSON
        every {
            jsonSerializer.fromJson(TXN_JSON, RawTransactionPayload::class.java)
        } returns RAW_TXN_PAYLOAD
        every { rawTransactionMapper(RAW_TXN_PAYLOAD) } returns RAW_TXN

        val result = sut(TXN_BYTE_ARRAY)

        assertEquals(RAW_TXN, result)
    }

    private companion object {
        val TXN_BYTE_ARRAY = byteArrayOf(1, 2, 3)
        const val TXN_JSON = "{ \"txn\": \"json\" }"
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
        val REJECT_VERSION = peraFixture<Long?>()
        val ACCESS_LIST = peraFixture<List<RawAccessItemPayload>?>()
        val GROUP_ID = peraFixture<String?>()

        val RAW_TXN_PAYLOAD = RawTransactionPayload(
            amount = AMOUNT,
            fee = FEE,
            firstValidRound = FIRST_VALID_ROUND,
            lastValidRound = LAST_VALID_ROUND,
            genesisHash = GENESIS_HASH,
            genesisId = GENESIS_ID,
            note = NOTE,
            transactionType = RAW_TXN_TYPE_PAYLOAD,
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
            groupId = GROUP_ID,
            rejectVersion = REJECT_VERSION,
            accessList = ACCESS_LIST,
            innerTransactions = null
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
            groupId = GROUP_ID,
            rejectVersion = REJECT_VERSION,
            accessListSize = ACCESS_LIST?.size,
            innerTransactions = null
        )
    }
}
