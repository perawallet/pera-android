package com.algorand.android.assetdetail.component.data.mapper.entity

import com.algorand.android.assetdetail.component.data.model.AssetDetailResponse
import com.algorand.android.assetdetail.component.data.model.collectible.*
import com.algorand.android.shared_db.assetdetail.model.CollectibleTraitEntity
import com.algorand.android.testutil.fixtureOf
import org.junit.*

internal class CollectibleTraitEntityMapperImplTest {

    private val sut = CollectibleTraitEntityMapperImpl()

    @Test
    fun `EXPECT null WHEN asset id is null`() {
        val assetDetailResponse = fixtureOf<AssetDetailResponse>().copy(assetId = null)

        val result = sut(assetDetailResponse)

        Assert.assertTrue(result.isEmpty())
    }

    @Test
    fun `EXPECT entity WHEN traits exists in response`() {
        val assetDetailResponse = fixtureOf<AssetDetailResponse>().copy(
            assetId = 1,
            collectible = fixtureOf<CollectibleResponse>().copy(
                traits = listOf(
                    CollectibleTraitResponse(name = "name1", value = "value1"),
                    CollectibleTraitResponse(name = "name2", value = "value2")
                )
            )
        )

        val result = sut(assetDetailResponse)

        val expected = listOf(
            CollectibleTraitEntity(collectibleAssetId = 1, displayName = "name1", displayValue = "value1"),
            CollectibleTraitEntity(collectibleAssetId = 1, displayName = "name2", displayValue = "value2")
        )
        Assert.assertEquals(expected, result)
    }
}
