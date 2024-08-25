package com.algorand.android.assetdetail.component.asset.data.utils

/**
 * Takes number list and returns Retrofit Query compatible string for array queries
 * @param [1, 12, 123]
 * @return 1,12,123
 */
internal fun Collection<Number>.toQueryString(): String {
    return toString().replace(Regex("([^0-9,])"), "")
}
