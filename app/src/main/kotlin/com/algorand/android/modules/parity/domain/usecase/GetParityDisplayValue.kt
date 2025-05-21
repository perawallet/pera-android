package com.algorand.android.modules.parity.domain.usecase

import com.algorand.android.modules.parity.domain.model.ParityDisplayValue
import com.algorand.wallet.asset.domain.model.AssetLite

interface GetParityDisplayValue {
    operator fun invoke(assetLite: AssetLite): ParityDisplayValue
}
