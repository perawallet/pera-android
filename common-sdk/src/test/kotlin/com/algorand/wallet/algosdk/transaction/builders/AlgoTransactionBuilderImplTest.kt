package com.algorand.wallet.algosdk.transaction.builders

import com.algorand.test.peraFixture
import com.algorand.wallet.algosdk.transaction.model.Transaction
import com.algorand.wallet.algosdk.transaction.sdk.AlgoSdk
import com.algorand.wallet.algosdk.transaction.sdk.model.AlgoTransactionPayload
import com.algorand.wallet.algosdk.transaction.sdk.model.SuggestedTransactionParams
import io.mockk.every
import io.mockk.mockk
import java.math.BigInteger
import org.junit.Assert.assertEquals
import org.junit.Test

class AlgoTransactionBuilderImplTest {

    private val algoSdk: AlgoSdk = mockk()

    private val sut = AlgoTransactionBuilderImpl(algoSdk)

    @Test
    fun `EXPECT algo txn to be created`() {
        every {
            algoSdk.createAlgoTransferTxn(
                ADDRESS,
                RECEIVER_ADDRESS,
                AMOUNT,
                IS_MAX,
                NOTE,
                SUGGESTED_PARAMS
            )
        } returns TXN_BYTE_ARRAY

        val result = sut(ALGO_TXN_PAYLOAD, SUGGESTED_PARAMS)

        val expected = Transaction.AlgoTransaction(ADDRESS, TXN_BYTE_ARRAY)
        assertEquals(expected, result)
    }

    private companion object {
        val ADDRESS = peraFixture<String>()
        val RECEIVER_ADDRESS = peraFixture<String>()
        val AMOUNT = peraFixture<BigInteger>()
        val IS_MAX = peraFixture<Boolean>()
        val NOTE = peraFixture<ByteArray>()
        val SUGGESTED_PARAMS = peraFixture<SuggestedTransactionParams>()

        val ALGO_TXN_PAYLOAD = AlgoTransactionPayload(
            senderAddress = ADDRESS,
            receiverAddress = RECEIVER_ADDRESS,
            amount = AMOUNT,
            isMaxAmount = IS_MAX,
            noteInByteArray = NOTE
        )
        val TXN_BYTE_ARRAY = peraFixture<ByteArray>()
    }
}
