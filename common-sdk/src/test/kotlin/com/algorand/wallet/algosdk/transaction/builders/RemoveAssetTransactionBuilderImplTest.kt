package com.algorand.wallet.algosdk.transaction.builders

import com.algorand.test.peraFixture
import com.algorand.wallet.algosdk.transaction.model.Transaction
import com.algorand.wallet.algosdk.transaction.sdk.AlgoSdk
import com.algorand.wallet.algosdk.transaction.sdk.model.RemoveAssetTransactionPayload
import com.algorand.wallet.algosdk.transaction.sdk.model.SuggestedTransactionParams
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class RemoveAssetTransactionBuilderImplTest {

    private val algoSdk: AlgoSdk = mockk()

    private val sut = RemoveAssetTransactionBuilderImpl(algoSdk)

    @Test
    fun `EXPECT remove asset txn to be created`() {
        every {
            algoSdk.createRemoveAssetTxn(ADDRESS, CREATOR_ADDRESS, ASSET_ID, SUGGESTED_PARAMS)
        } returns TXN_BYTE_ARRAY

        val result = sut(REMOVE_ASSET_PAYLOAD, SUGGESTED_PARAMS)

        val expected = Transaction.RemoveAssetTransaction(ADDRESS, TXN_BYTE_ARRAY)
        assertEquals(expected, result)
    }

    private companion object {
        val ADDRESS = peraFixture<String>()
        val ASSET_ID = peraFixture<Long>()
        val CREATOR_ADDRESS = peraFixture<String>()
        val SUGGESTED_PARAMS = peraFixture<SuggestedTransactionParams>()
        val TXN_BYTE_ARRAY = peraFixture<ByteArray>()
        val REMOVE_ASSET_PAYLOAD = RemoveAssetTransactionPayload(
            senderAddress = ADDRESS,
            assetId = ASSET_ID,
            creatorAddress = CREATOR_ADDRESS
        )
    }
}
