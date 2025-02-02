package com.algorand.wallet.algosdk.transaction.mapper

import com.algorand.wallet.algosdk.transaction.model.ApplicationCallStateSchema
import com.algorand.wallet.algosdk.transaction.model.payload.RawTransactionApplicationCallStateSchemaPayload
import java.math.BigInteger.ONE
import java.math.BigInteger.TEN
import org.junit.Assert.assertEquals
import org.junit.Test

class ApplicationCallStateSchemaMapperImplTest {

    private val sut = ApplicationCallStateSchemaMapperImpl()

    @Test
    fun `EXPECT application call state schema to be mapped`() {
        val payload = RawTransactionApplicationCallStateSchemaPayload(
            numberOfInts = TEN,
            numberOfBytes = ONE
        )
        val result = sut(payload)

        val expected = ApplicationCallStateSchema(numberOfBytes = ONE, numberOfInts = TEN)
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT application call state schema values to be null WHEN payload is null`() {
        val result = sut(null)

        val expected = ApplicationCallStateSchema(numberOfBytes = null, numberOfInts = null)
        assertEquals(expected, result)
    }
}
