package com.algorand.android.utils.app

import com.algorand.android.BuildConfig
import com.algorand.android.foundation.app.ProvideApplicationName
import javax.inject.Inject

internal class ProvideApplicationNameImpl @Inject constructor() : ProvideApplicationName {

    override fun invoke(): String {
        return BuildConfig.APPLICATION_NAME
    }
}
