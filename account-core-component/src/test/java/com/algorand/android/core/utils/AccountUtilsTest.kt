package com.algorand.android.core.utils

import com.algorand.android.module.account.core.component.utils.toShortenedAddress
import org.junit.Assert.assertEquals
import org.junit.Test

internal class AccountUtilsTest {

    @Test
    fun `EXPECT address to be shortened`() {
        val address = "VERYLONGADDRESSVERYLONGADDRESS"

        val result = address.toShortenedAddress()

        val expected = "VERYLO...DDRESS"
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT empty string WHEN address is not valid`() {
        val address = ""

        val result = address.toShortenedAddress()

        val expected = ""
        assertEquals(expected, result)
    }
}
