package com.algorand.android.account.accountinformation.domain.mapper

import com.algorand.android.account.accountinformation.domain.mapper.AccountAssetsMapperImpl
import com.algorand.android.account.accountinformation.domain.model.AssetHolding
import com.algorand.android.testutil.fixtureOf
import org.junit.Assert.assertEquals
import org.junit.Test

class AccountAssetsMapperImplTest {

    private val sut = AccountAssetsMapperImpl()

    @Test
    fun `EXPECT account assets to be mapped correctly`() {
        val address = "address"
        val assetHoldings = fixtureOf<List<AssetHolding>>()

        val result = sut(address, assetHoldings)

        assertEquals(address, result.address)
        assertEquals(assetHoldings, result.assetHoldings)
    }
}
