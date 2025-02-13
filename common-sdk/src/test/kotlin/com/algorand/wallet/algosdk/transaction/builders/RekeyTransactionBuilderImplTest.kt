package com.algorand.wallet.algosdk.transaction.builders

import com.algorand.test.peraFixture
import com.algorand.wallet.algosdk.transaction.model.Transaction
import com.algorand.wallet.algosdk.transaction.sdk.AlgoSdk
import com.algorand.wallet.algosdk.transaction.sdk.model.RekeyTransactionPayload
import com.algorand.wallet.algosdk.transaction.sdk.model.SuggestedTransactionParams
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class RekeyTransactionBuilderImplTest {

    private val algoSdk: AlgoSdk = mockk()

    private val sut = RekeyTransactionBuilderImpl(algoSdk)

    @Test
    fun `EXPECT rekey transaction to be created`() {
        every { algoSdk.createRekeyTxn(REKEY_ADDRESS, ADDRESS, SUGGESTED_PARAMS) } returns TXN_BYTE_ARRAY

        val result = sut(REKEY_TXN_PAYLOAD, SUGGESTED_PARAMS)

        val expected = Transaction.RekeyTransaction(REKEY_ADDRESS, TXN_BYTE_ARRAY)
        assertEquals(expected, result)
    }

    private companion object {
        val ADDRESS = peraFixture<String>()
        val REKEY_ADDRESS = peraFixture<String>()
        val SUGGESTED_PARAMS = peraFixture<SuggestedTransactionParams>()
        val TXN_BYTE_ARRAY = peraFixture<ByteArray>()

        val REKEY_TXN_PAYLOAD = RekeyTransactionPayload(
            address = REKEY_ADDRESS,
            rekeyAdminAddress = ADDRESS
        )
    }
}
