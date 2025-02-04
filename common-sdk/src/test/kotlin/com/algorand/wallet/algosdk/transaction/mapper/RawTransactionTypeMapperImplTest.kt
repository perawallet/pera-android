package com.algorand.wallet.algosdk.transaction.mapper

import com.algorand.wallet.algosdk.transaction.model.RawTransactionType
import com.algorand.wallet.algosdk.transaction.model.payload.RawTransactionTypePayload
import org.junit.Assert.assertEquals
import org.junit.Test

class RawTransactionTypeMapperImplTest {

    private val sut = RawTransactionTypeMapperImpl()

    @Test
    fun `EXPECT payload to be mapped`() {
        val payTxnResult = sut(RawTransactionTypePayload.PAY_TRANSACTION)
        val assetTxnResult = sut(RawTransactionTypePayload.ASSET_TRANSACTION)
        val appTxnResult = sut(RawTransactionTypePayload.APP_TRANSACTION)
        val assetConfigResult = sut(RawTransactionTypePayload.ASSET_CONFIGURATION)
        val undefinedResult = sut(RawTransactionTypePayload.UNDEFINED)

        assertEquals(RawTransactionType.PAY_TRANSACTION, payTxnResult)
        assertEquals(RawTransactionType.ASSET_TRANSACTION, assetTxnResult)
        assertEquals(RawTransactionType.APP_TRANSACTION, appTxnResult)
        assertEquals(RawTransactionType.ASSET_CONFIGURATION, assetConfigResult)
        assertEquals(RawTransactionType.UNDEFINED, undefinedResult)
    }
}
