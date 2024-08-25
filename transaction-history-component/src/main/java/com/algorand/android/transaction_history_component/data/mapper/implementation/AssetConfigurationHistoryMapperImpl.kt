package com.algorand.android.transaction_history_component.data.mapper.implementation

import com.algorand.android.transaction_history_component.data.mapper.AssetConfigurationHistoryMapper
import com.algorand.android.transaction_history_component.data.model.AssetConfigurationHistoryResponse
import com.algorand.android.transaction_history_component.domain.model.AssetConfigurationHistory
import javax.inject.Inject

internal class AssetConfigurationHistoryMapperImpl @Inject constructor() : AssetConfigurationHistoryMapper {

    override fun invoke(response: AssetConfigurationHistoryResponse?): AssetConfigurationHistory? {
        if (response == null) return null
        return AssetConfigurationHistory(
            assetId = response.assetId,
            creator = response.params?.creator,
            decimals = response.params?.decimals,
            defaultFrozen = response.params?.defaultFrozen,
            metadataHash = response.params?.metadataHash,
            name = response.params?.name,
            nameB64 = response.params?.nameB64,
            maxSupply = response.params?.maxSupply,
            unitName = response.params?.unitName,
            unitNameB64 = response.params?.unitNameB64,
            url = response.params?.url,
            urlB64 = response.params?.urlB64
        )
    }
}
