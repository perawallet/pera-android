package com.algorand.wallet.algosdk.transaction.builders

import com.algorand.test.peraFixture
import com.algorand.wallet.algosdk.transaction.model.Transaction
import com.algorand.wallet.algosdk.transaction.sdk.AlgoSdk
import com.algorand.wallet.algosdk.transaction.sdk.model.SendAndRemoveAssetTransactionPayload
import com.algorand.wallet.algosdk.transaction.sdk.model.SuggestedTransactionParams
import io.mockk.every
import io.mockk.mockk
import java.math.BigInteger
import org.junit.Assert.assertEquals
import org.junit.Test

class SendAndRemoveAssetTransactionBuilderImplTest {

    private val algoSdk: AlgoSdk = mockk()

    private val sut = SendAndRemoveAssetTransactionBuilderImpl(algoSdk)

    @Test
    fun `EXPECT send and remove asset txn to be created`() {
        every {
            algoSdk.createSendAndRemoveAssetTxn(
                ADDRESS,
                RECEIVER_ADDRESS,
                ASSET_ID,
                AMOUNT,
                NOTE,
                SUGGESTED_PARAMS
            )
        } returns TXN_BYTE_ARRAY

        val result = sut(SEND_AND_REMOVE_ASSET_PAYLOAD, SUGGESTED_PARAMS)

        val expected = Transaction.SendAndRemoveAssetTransaction(ADDRESS, TXN_BYTE_ARRAY)
        assertEquals(expected, result)
    }

    private companion object {
        val ADDRESS = peraFixture<String>()
        val ASSET_ID = peraFixture<Long>()
        val RECEIVER_ADDRESS = peraFixture<String>()
        val AMOUNT = peraFixture<BigInteger>()
        val NOTE = peraFixture<ByteArray>()
        val SUGGESTED_PARAMS = peraFixture<SuggestedTransactionParams>()
        val TXN_BYTE_ARRAY = peraFixture<ByteArray>()
        val SEND_AND_REMOVE_ASSET_PAYLOAD = SendAndRemoveAssetTransactionPayload(
            senderAddress = ADDRESS,
            assetId = ASSET_ID,
            receiverAddress = RECEIVER_ADDRESS,
            amount = AMOUNT,
            noteInByteArray = NOTE
        )
    }
}
