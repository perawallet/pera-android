package com.algorand.android.module.shareddb.converter

import com.algorand.android.module.shareddb.assetdetail.model.VerificationTierEntity
import com.algorand.android.module.shareddb.converters.VerificationTierTypeConverter
import org.junit.Assert.assertEquals
import org.junit.Test

internal class VerificationTierTypeConverterTest {

    @Test
    fun `EXPECT verified string to be converted successfully`() {
        val input = VerificationTierEntity.VERIFIED.toString()

        val result = VerificationTierTypeConverter.stringToVerificationTier(input)

        val expected = VerificationTierEntity.VERIFIED
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT unverified string to be converted successfully`() {
        val input = VerificationTierEntity.UNVERIFIED.toString()

        val result = VerificationTierTypeConverter.stringToVerificationTier(input)

        val expected = VerificationTierEntity.UNVERIFIED
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT trusted string to be converted successfully`() {
        val input = VerificationTierEntity.TRUSTED.toString()

        val result = VerificationTierTypeConverter.stringToVerificationTier(input)

        val expected = VerificationTierEntity.TRUSTED
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT suspicious string to be converted successfully `() {
        val input = VerificationTierEntity.SUSPICIOUS.toString()

        val result = VerificationTierTypeConverter.stringToVerificationTier(input)

        val expected = VerificationTierEntity.SUSPICIOUS
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT unknown string to be converted successfully`() {
        val input = VerificationTierEntity.UNKNOWN.toString()

        val result = VerificationTierTypeConverter.stringToVerificationTier(input)

        val expected = VerificationTierEntity.UNKNOWN
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT verified to be converted to string successfully`() {
        val input = VerificationTierEntity.VERIFIED

        val result = VerificationTierTypeConverter.verificationTierToString(input)

        val expected = VerificationTierEntity.VERIFIED.toString()
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT unverified to be converted to string successfully`() {
        val input = VerificationTierEntity.UNVERIFIED

        val result = VerificationTierTypeConverter.verificationTierToString(input)

        val expected = VerificationTierEntity.UNVERIFIED.toString()
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT trusted to be converted to string successfully`() {
        val input = VerificationTierEntity.TRUSTED

        val result = VerificationTierTypeConverter.verificationTierToString(input)

        val expected = VerificationTierEntity.TRUSTED.toString()
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT suspicious to be converted to string successfully`() {
        val input = VerificationTierEntity.TRUSTED

        val result = VerificationTierTypeConverter.verificationTierToString(input)

        val expected = VerificationTierEntity.TRUSTED.toString()
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT unknown to be converted to string successfully`() {
        val input = VerificationTierEntity.UNKNOWN

        val result = VerificationTierTypeConverter.verificationTierToString(input)

        val expected = VerificationTierEntity.UNKNOWN.toString()
        assertEquals(expected, result)
    }
}
