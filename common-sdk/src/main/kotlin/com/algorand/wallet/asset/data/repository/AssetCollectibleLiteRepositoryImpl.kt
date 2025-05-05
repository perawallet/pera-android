package com.algorand.wallet.asset.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.map
import com.algorand.wallet.asset.data.database.dao.PaginatedAssetCollectibleDao
import com.algorand.wallet.asset.data.database.model.PaginatedAssetCollectibleItemDto
import com.algorand.wallet.asset.data.mapper.model.AssetCollectibleLiteSortTypeQueryMapper
import com.algorand.wallet.asset.data.mapper.model.AssetLiteMapper
import com.algorand.wallet.asset.domain.model.AssetCollectibleLiteQuery
import com.algorand.wallet.asset.domain.model.AssetLite
import com.algorand.wallet.asset.domain.repository.AssetCollectibleLiteRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class AssetCollectibleLiteRepositoryImpl @Inject constructor(
    private val assetCollectibleLiteSortTypeQueryMapper: AssetCollectibleLiteSortTypeQueryMapper,
    private val assetLiteMapper: AssetLiteMapper,
    private val paginatedAssetCollectibleDao: PaginatedAssetCollectibleDao
) : AssetCollectibleLiteRepository {

    override fun getPaginatedAssetCollectibleLiteItems(query: AssetCollectibleLiteQuery): Flow<PagingData<AssetLite>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE),
            pagingSourceFactory = { getPagingSource(query) }
        ).flow.map { pagingData ->
            pagingData.map { pagedItem ->
                assetLiteMapper(pagedItem)
            }
        }
    }

    private fun getPagingSource(query: AssetCollectibleLiteQuery): PagingSource<Int, PaginatedAssetCollectibleItemDto> {
        return paginatedAssetCollectibleDao.getPaginatedAssetCollectibleItems(
            addressList = query.addresses,
            searchKeyword = query.searchKeyword,
            filterOutZeroAmount = query.filterOutZeroAmount,
            filterOutCollectibles = query.filterOutCollectibles,
            filterOutCollectiblesWithZeroAmount = query.filterOutCollectiblesWithZeroAmount,
            sortType = assetCollectibleLiteSortTypeQueryMapper(query.sortType)
        )
    }

    private companion object {
        const val PAGE_SIZE = 50
    }
}
