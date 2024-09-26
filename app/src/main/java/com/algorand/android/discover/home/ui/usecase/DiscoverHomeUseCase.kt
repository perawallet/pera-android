/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android.discover.home.ui.usecase

import com.algorand.android.currency.domain.usecase.GetPrimaryCurrencyId
import com.algorand.android.module.deviceid.domain.usecase.GetSelectedNodeDeviceId
import com.algorand.android.discover.common.ui.model.DappFavoriteElement
import com.algorand.android.discover.home.ui.mapper.DiscoverDappFavoritesMapper
import com.algorand.android.discover.home.ui.mapper.TokenDetailInfoMapper
import com.algorand.android.discover.utils.getAddToFavoriteFunction
import com.algorand.android.discover.utils.getSendDeviceId
import com.algorand.android.discover.utils.isValidDiscoverURL
import com.google.gson.Gson
import javax.inject.Inject

class DiscoverHomeUseCase @Inject constructor(
    private val discoverDappFavoritesMapper: DiscoverDappFavoritesMapper,
    private val tokenDetailInfoMapper: TokenDetailInfoMapper,
    private val getPrimaryCurrency: GetPrimaryCurrencyId,
    private val gson: Gson,
    private val getSelectedNodeDeviceId: GetSelectedNodeDeviceId
) {
    fun getPrimaryCurrencyId(): String {
        return getPrimaryCurrency()
    }

    fun getAddToFavoriteJSFunction(favorite: DappFavoriteElement): String {
        return getAddToFavoriteFunction(discoverDappFavoritesMapper.mapFromDappFavoriteElement(favorite), gson)
    }

    fun tokenDetailIdToJson(assetId: Long): String {
        return gson.toJson(tokenDetailInfoMapper.mapToTokenDetailInfo(assetId.toString(), null))
    }

    suspend fun getSendDeviceIdJSFunctionOrNull(callingUrl: String): String? {
        val deviceId = getSelectedNodeDeviceId()
        return if (deviceId != null && isValidDiscoverURL(callingUrl)) {
            getSendDeviceId(deviceId, gson)
        } else {
            null
        }
    }
}
