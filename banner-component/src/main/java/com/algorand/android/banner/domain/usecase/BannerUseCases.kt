/*
 *   ~ Copyright 2022 Pera Wallet, LDA
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License
 *   -->
 */

package com.algorand.android.banner.domain.usecase

import com.algorand.android.banner.domain.model.Banner
import kotlinx.coroutines.flow.Flow

interface InitializeBanners {
    suspend operator fun invoke()
}

fun interface GetBannerFlow {
    suspend operator fun invoke(): Flow<Banner?>
}

fun interface DismissBanner {
    suspend operator fun invoke(bannerId: Long)
}

fun interface ClearBanners {
    suspend operator fun invoke()
}

fun interface ClearDismissedBannerIds {
    suspend operator fun invoke()
}
