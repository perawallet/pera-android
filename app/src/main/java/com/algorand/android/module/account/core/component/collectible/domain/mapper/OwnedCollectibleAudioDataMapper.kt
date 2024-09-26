package com.algorand.android.module.account.core.component.collectible.domain.mapper

import com.algorand.android.assetdetail.component.asset.domain.model.detail.AudioCollectibleDetail
import com.algorand.android.module.account.core.component.domain.model.BaseAccountAssetData.BaseOwnedAssetData.BaseOwnedCollectibleData.OwnedCollectibleAudioData
import com.algorand.android.parity.domain.model.ParityValue
import java.math.BigInteger

internal interface OwnedCollectibleAudioDataMapper {

    operator fun invoke(
        collectibleDetail: AudioCollectibleDetail,
        amount: BigInteger,
        formattedAmount: String,
        formattedCompactAmount: String,
        parityValueInSelectedCurrency: ParityValue,
        parityValueInSecondaryCurrency: ParityValue,
        optedInAtRound: Long?
    ): OwnedCollectibleAudioData
}
