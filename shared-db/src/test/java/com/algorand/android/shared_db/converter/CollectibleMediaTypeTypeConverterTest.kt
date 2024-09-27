package com.algorand.android.account.core.data.database.converter

import com.algorand.android.module.shareddb.assetdetail.model.CollectibleMediaTypeEntity
import com.algorand.android.module.shareddb.converters.CollectibleMediaTypeTypeConverter
import org.junit.Assert.assertEquals
import org.junit.Test

internal class CollectibleMediaTypeTypeConverterTest {

    @Test
    fun `EXPECT image to be converted successfully`() {
        val input = CollectibleMediaTypeEntity.IMAGE.toString()

        val result = CollectibleMediaTypeTypeConverter.stringToCollectibleMediaType(input)

        val expected = CollectibleMediaTypeEntity.IMAGE
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT video to be converted successfully`() {
        val input = CollectibleMediaTypeEntity.VIDEO.toString()

        val result = CollectibleMediaTypeTypeConverter.stringToCollectibleMediaType(input)

        val expected = CollectibleMediaTypeEntity.VIDEO
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT mixed to be converted successfully`() {
        val input = CollectibleMediaTypeEntity.MIXED.toString()

        val result = CollectibleMediaTypeTypeConverter.stringToCollectibleMediaType(input)

        val expected = CollectibleMediaTypeEntity.MIXED
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT audio to be converted successfully`() {
        val input = CollectibleMediaTypeEntity.AUDIO.toString()

        val result = CollectibleMediaTypeTypeConverter.stringToCollectibleMediaType(input)

        val expected = CollectibleMediaTypeEntity.AUDIO
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT unknown to be converted successfully`() {
        val input = CollectibleMediaTypeEntity.UNKNOWN.toString()

        val result = CollectibleMediaTypeTypeConverter.stringToCollectibleMediaType(input)

        val expected = CollectibleMediaTypeEntity.UNKNOWN
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT image to be converted to string successfully`() {
        val input = CollectibleMediaTypeEntity.IMAGE

        val result = CollectibleMediaTypeTypeConverter.collectibleMediaTypeToString(input)

        val expected = CollectibleMediaTypeEntity.IMAGE.toString()
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT video to be converted to string successfully`() {
        val input = CollectibleMediaTypeEntity.VIDEO

        val result = CollectibleMediaTypeTypeConverter.collectibleMediaTypeToString(input)

        val expected = CollectibleMediaTypeEntity.VIDEO.toString()
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT mixed to be converted to string successfully`() {
        val input = CollectibleMediaTypeEntity.MIXED

        val result = CollectibleMediaTypeTypeConverter.collectibleMediaTypeToString(input)

        val expected = CollectibleMediaTypeEntity.MIXED.toString()
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT audio to be converted to string successfully`() {
        val input = CollectibleMediaTypeEntity.AUDIO

        val result = CollectibleMediaTypeTypeConverter.collectibleMediaTypeToString(input)

        val expected = CollectibleMediaTypeEntity.AUDIO.toString()
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT unknown to be converted to string successfully`() {
        val input = CollectibleMediaTypeEntity.UNKNOWN

        val result = CollectibleMediaTypeTypeConverter.collectibleMediaTypeToString(input)

        val expected = CollectibleMediaTypeEntity.UNKNOWN.toString()
        assertEquals(expected, result)
    }
}
