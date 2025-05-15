package com.algorand.android.usecase

import com.algorand.android.BuildConfig
import javax.inject.Inject

// TODO: Update this class after deciding on where is the correct place to get the active node
class GetIsProductionBuildUseCase @Inject constructor() {
    operator fun invoke(): Boolean {
        // return true if prod flavor
        return BuildConfig.FLAVOR == "prod"
    }
}
