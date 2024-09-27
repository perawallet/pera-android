package com.algorand.android.module.asset.detail.component.data.mapper.entity

import com.algorand.android.module.asset.detail.component.data.model.AssetDetailResponse
import com.algorand.android.module.asset.detail.component.data.model.collectible.CollectibleResponse
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleEntity
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleMediaTypeEntity.MIXED
import com.algorand.android.module.shareddb.assetdetail.model.CollectibleStandardTypeEntity.ARC_3
import com.algorand.android.testutil.fixtureOf
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.*

internal class CollectibleEntityMapperImplTest {

    private val collectibleStandardEntityMapper: CollectibleStandardTypeEntityMapper = mock()
    private val collectibleMediaTypeEntityMapper: CollectibleMediaTypeEntityMapper = mock()

    private val sut = com.algorand.android.module.asset.detail.component.asset.data.mapper.entity.CollectibleEntityMapperImpl(
        collectibleStandardEntityMapper,
        collectibleMediaTypeEntityMapper
    )

    @Test
    fun `EXPECT response to be mapped to entity successfully`() {
        val collectibleResponse = fixtureOf<CollectibleResponse>()
        val assetDetailResponse = fixtureOf<AssetDetailResponse>().copy(
            assetId = 1L,
            collectible = collectibleResponse
        )
        whenever(collectibleStandardEntityMapper(collectibleResponse.standard)).thenReturn(ARC_3)
        whenever(collectibleMediaTypeEntityMapper(collectibleResponse.mediaType)).thenReturn(MIXED)

        val result = sut(assetDetailResponse)

        val expected = CollectibleEntity(
            collectibleAssetId = 1L,
            standardType = ARC_3,
            mediaType = MIXED,
            primaryImageUrl = collectibleResponse.primaryImageUrl,
            title = collectibleResponse.title,
            description = collectibleResponse.description,
            collectionId = collectibleResponse.collection?.collectionId,
            collectionName = collectibleResponse.collection?.collectionName,
            collectionDescription = collectibleResponse.collection?.collectionDescription
        )
        assertEquals(expected, result)
    }
}
