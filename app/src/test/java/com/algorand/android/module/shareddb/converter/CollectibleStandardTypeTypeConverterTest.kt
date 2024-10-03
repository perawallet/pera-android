package com.algorand.android.module.shareddb.converter

import com.algorand.android.module.shareddb.assetdetail.model.CollectibleStandardTypeEntity
import com.algorand.android.module.shareddb.converters.CollectibleStandardTypeTypeConverter
import org.junit.Assert.assertEquals
import org.junit.Test

internal class CollectibleStandardTypeTypeConverterTest {

    @Test
    fun `EXPECT arc3 to be converted to string successfully`() {
        val input = CollectibleStandardTypeEntity.ARC_3

        val result = CollectibleStandardTypeTypeConverter.collectibleStandardTypeToString(input)

        val expected = CollectibleStandardTypeEntity.ARC_3.toString()
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT arc69 to be converted to string successfully`() {
        val input = CollectibleStandardTypeEntity.ARC_69

        val result = CollectibleStandardTypeTypeConverter.collectibleStandardTypeToString(input)

        val expected = CollectibleStandardTypeEntity.ARC_69.toString()
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT unknown to be converted to string successfully`() {
        val input = CollectibleStandardTypeEntity.UNKNOWN

        val result = CollectibleStandardTypeTypeConverter.collectibleStandardTypeToString(input)

        val expected = CollectibleStandardTypeEntity.UNKNOWN.toString()
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT string to be converted to arc3 successfully`() {
        val input = CollectibleStandardTypeEntity.ARC_3.toString()

        val result = CollectibleStandardTypeTypeConverter.stringToCollectibleStandardType(input)

        val expected = CollectibleStandardTypeEntity.ARC_3
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT string to be converted to arc69 successfully`() {
        val input = CollectibleStandardTypeEntity.ARC_69.toString()

        val result = CollectibleStandardTypeTypeConverter.stringToCollectibleStandardType(input)

        val expected = CollectibleStandardTypeEntity.ARC_69
        assertEquals(expected, result)
    }

    @Test
    fun `EXPECT string to be converted to unknown successfully`() {
        val input = CollectibleStandardTypeEntity.UNKNOWN.toString()

        val result = CollectibleStandardTypeTypeConverter.stringToCollectibleStandardType(input)

        val expected = CollectibleStandardTypeEntity.UNKNOWN
        assertEquals(expected, result)
    }
}
