package com.algorand.wallet.algosdk.transaction.usecase

import com.algorand.test.peraFixture
import com.algorand.wallet.algosdk.transaction.mapper.RawTransactionMapper
import com.algorand.wallet.algosdk.transaction.model.RawTransaction
import com.algorand.wallet.algosdk.transaction.model.payload.RawTransactionPayload
import com.algorand.wallet.algosdk.transaction.sdk.AlgoSdk
import com.algorand.wallet.foundation.json.JsonSerializer
import io.mockk.every
import io.mockk.mockk
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
        val RAW_TXN_PAYLOAD = peraFixture<RawTransactionPayload>()
        val RAW_TXN = peraFixture<RawTransaction>()
    }
}
