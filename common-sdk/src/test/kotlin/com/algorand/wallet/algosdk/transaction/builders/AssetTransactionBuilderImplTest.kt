package com.algorand.wallet.algosdk.transaction.builders

import com.algorand.test.peraFixture
import com.algorand.wallet.algosdk.transaction.model.Transaction
import com.algorand.wallet.algosdk.transaction.sdk.AlgoSdk
import com.algorand.wallet.algosdk.transaction.sdk.model.AssetTransactionPayload
import com.algorand.wallet.algosdk.transaction.sdk.model.SuggestedTransactionParams
import io.mockk.every
import io.mockk.mockk
import java.math.BigInteger
import org.junit.Assert.assertEquals
import org.junit.Test

class AssetTransactionBuilderImplTest {

    private val algoSdk: AlgoSdk = mockk()

    private val sut = AssetTransactionBuilderImpl(algoSdk)

    @Test
    fun `EXPECT asset txn to be created`() {
        every {
            algoSdk.createAssetTransferTxn(
                ADDRESS,
                RECEIVER_ADDRESS,
                AMOUNT,
                ASSET_ID,
                NOTE,
                SUGGESTED_PARAMS
            )
        } returns TXN_BYTE_ARRAY

        val result = sut(ASSET_TXN_PAYLOAD, SUGGESTED_PARAMS)

        val expected = Transaction.AssetTransaction(ADDRESS, TXN_BYTE_ARRAY)
        assertEquals(expected, result)
    }

    private companion object {
        val ADDRESS = peraFixture<String>()
        val RECEIVER_ADDRESS = peraFixture<String>()
        val AMOUNT = peraFixture<BigInteger>()
        val ASSET_ID = peraFixture<Long>()
        val NOTE = peraFixture<ByteArray>()
        val SUGGESTED_PARAMS = peraFixture<SuggestedTransactionParams>()

        val ASSET_TXN_PAYLOAD = AssetTransactionPayload(
            senderAddress = ADDRESS,
            receiverAddress = RECEIVER_ADDRESS,
            amount = AMOUNT,
            noteInByteArray = NOTE,
            assetId = ASSET_ID
        )
        val TXN_BYTE_ARRAY = peraFixture<ByteArray>()
    }
}
