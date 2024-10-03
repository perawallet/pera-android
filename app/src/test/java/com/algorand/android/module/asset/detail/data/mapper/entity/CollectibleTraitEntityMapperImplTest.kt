package com.algorand.android.module.asset.detail.data.mapper.entity

import com.algorand.android.module.asset.detail.component.asset.data.mapper.entity.CollectibleTraitEntityMapperImpl
import com.algorand.android.module.asset.detail.component.asset.data.model.AssetResponse
import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleResponse
import com.algorand.android.module.asset.detail.component.asset.data.model.collectible.CollectibleTraitResponse
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleTraitEntity
import com.algorand.android.testutil.fixtureOf
import org.junit.Assert
import org.junit.Test

internal class CollectibleTraitEntityMapperImplTest {

    private val sut = CollectibleTraitEntityMapperImpl()

    @Test
    fun `EXPECT null WHEN asset id is null`() {
        val assetDetailResponse = fixtureOf<AssetResponse>().copy(assetId = null)

        val result = sut(assetDetailResponse)

        Assert.assertTrue(result.isEmpty())
    }

    @Test
    fun `EXPECT entity WHEN traits exists in response`() {
        val assetDetailResponse = fixtureOf<AssetResponse>().copy(
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
