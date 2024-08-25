package com.algorand.android.foundation.locale

import java.util.Locale
import javax.inject.Inject

internal class LocaleProviderImpl @Inject constructor() : LocaleProvider {

    override fun getLocale(): Locale {
        return Locale.getDefault()
    }
}
