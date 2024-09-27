package com.algorand.android.account.core.data.database.converter

import com.algorand.android.module.shareddb.assetdetail.model.CollectibleMediaTypeExtensionEntity
import com.algorand.android.module.shareddb.converters.CollectibleMediaTypeExtensionTypeConverter
import org.junit.Assert.assertEquals
import org.junit.Test

internal class CollectibleMediaTypeExtensionTypeConverterTest {

    @Test
    fun `EXPECT gif to be converted to string successfully`() {
        val input = CollectibleMediaTypeExtensionEntity.GIF

        val result = CollectibleMediaTypeExtensionTypeConverter.collectibleMediaTypeExtensionToString(input)

        val expected = CollectibleMediaTypeExtensionEntity.GIF.toString()
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT webp to be converted to string successfully`() {
        val input = CollectibleMediaTypeExtensionEntity.WEBP

        val result = CollectibleMediaTypeExtensionTypeConverter.collectibleMediaTypeExtensionToString(input)

        val expected = CollectibleMediaTypeExtensionEntity.WEBP.toString()
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT unknown to be converted to string successfully`() {
        val input = CollectibleMediaTypeExtensionEntity.UNKNOWN

        val result = CollectibleMediaTypeExtensionTypeConverter.collectibleMediaTypeExtensionToString(input)

        val expected = CollectibleMediaTypeExtensionEntity.UNKNOWN.toString()
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT string to be converted to gif successfully`() {
        val input = CollectibleMediaTypeExtensionEntity.GIF.toString()

        val result = CollectibleMediaTypeExtensionTypeConverter.stringToCollectibleMediaTypeExtension(input)

        val expected = CollectibleMediaTypeExtensionEntity.GIF
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT string to be converted to webp successfully`() {
        val input = CollectibleMediaTypeExtensionEntity.WEBP.toString()

        val result = CollectibleMediaTypeExtensionTypeConverter.stringToCollectibleMediaTypeExtension(input)

        val expected = CollectibleMediaTypeExtensionEntity.WEBP
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT string to be converted to unknown successfully`() {
        val input = CollectibleMediaTypeExtensionEntity.UNKNOWN.toString()

        val result = CollectibleMediaTypeExtensionTypeConverter.stringToCollectibleMediaTypeExtension(input)

        val expected = CollectibleMediaTypeExtensionEntity.UNKNOWN
        assertEquals(expected, result)
    }
}
