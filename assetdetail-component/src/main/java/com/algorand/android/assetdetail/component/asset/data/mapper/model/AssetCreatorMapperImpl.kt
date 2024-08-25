package com.algorand.android.assetdetail.component.asset.data.mapper.model

import com.algorand.android.assetdetail.component.asset.data.model.AssetCreatorResponse
import com.algorand.android.assetdetail.component.asset.domain.model.AssetCreator
import javax.inject.Inject

internal class AssetCreatorMapperImpl @Inject constructor() : AssetCreatorMapper {

    override fun invoke(response: AssetCreatorResponse): AssetCreator? {
        return with(response) {
            if (id == null && publicKey == null && isVerifiedAssetCreator == null) {
                return@with null
            }
            AssetCreator(
                id = id,
                publicKey = publicKey,
                isVerifiedAssetCreator = isVerifiedAssetCreator,
            )
        }
    }

    override fun invoke(id: Long?, address: String?, isVerified: Boolean?): AssetCreator? {
        if (id == null && address == null && isVerified == null) {
            return null
        }
        return AssetCreator(
            id = id,
            publicKey = address,
            isVerifiedAssetCreator = isVerified,
        )
    }
}
