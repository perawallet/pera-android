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

import com.algorand.android.banner.domain.repository.BannerRepository
import com.algorand.android.deviceid.component.domain.usecase.GetSelectedNodeDeviceId
import javax.inject.Inject

internal class InitializeBannersUseCase @Inject constructor(
    private val getSelectedNodeDeviceId: GetSelectedNodeDeviceId,
    private val bannerRepository: BannerRepository
) : InitializeBanners {

    override suspend fun invoke() {
        val selectedNodeDeviceId = getSelectedNodeDeviceId() ?: return
        bannerRepository.initializeBanners(selectedNodeDeviceId)
    }
}