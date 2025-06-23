/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.wallet.banner.domain.repository

import com.algorand.wallet.banner.domain.model.Banner
import com.algorand.wallet.foundation.PeraResult
import kotlinx.coroutines.flow.Flow

internal interface BannerRepository {

    fun getBannersFlow(): Flow<List<Banner>>

    suspend fun cacheBanners(banners: List<Banner>)

    suspend fun getBanners(deviceId: String): PeraResult<List<Banner>>

    suspend fun dismissBanner(bannerId: Long)

    suspend fun getDismissedBannerIdList(): List<Long>

    suspend fun clearBannerCache()

    suspend fun clearDismissedBannerIds()
}
