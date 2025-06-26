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

package com.algorand.wallet.spotbanner.domain.usecase

import com.algorand.wallet.account.detail.domain.model.AccountType.Companion.canSignTransaction
import com.algorand.wallet.spotbanner.domain.model.SpotBanner
import com.algorand.wallet.spotbanner.domain.model.SpotBannerFlowData
import com.algorand.wallet.spotbanner.domain.repository.SpotBannerRepository
import java.math.BigDecimal
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GetSpotBannersFlowUseCase @Inject constructor(
    private val spotBannerRepository: SpotBannerRepository
) : GetSpotBannersFlow {

    override fun invoke(data: List<SpotBannerFlowData>): Flow<List<SpotBanner>> {
        return spotBannerRepository.getSpotBannerFlow().map { banners ->
            if (isThereAnyNotBackedUpAuthAddressWithBalance(data)) {
                listOf(SpotBanner.BackupPassphrase) + banners
            } else {
                banners
            }
        }
    }

    private fun isThereAnyNotBackedUpAuthAddressWithBalance(data: List<SpotBannerFlowData>): Boolean {
        return data.any {
            !it.isBackedUp &&
                it.type?.canSignTransaction() == true &&
                (it.primaryBalance ?: BigDecimal.ZERO).compareTo(BigDecimal.ZERO) == 1
        }
    }
}
