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

package com.algorand.android.ui.asset.detail.mapper

import com.algorand.android.R
import com.algorand.android.ui.asset.detail.model.AssetMarketsDetail
import com.algorand.wallet.asset.domain.model.VerificationTier
import com.algorand.wallet.asset.domain.model.VerificationTier.SUSPICIOUS
import com.algorand.wallet.asset.domain.model.VerificationTier.TRUSTED
import com.algorand.wallet.asset.domain.model.VerificationTier.UNKNOWN
import com.algorand.wallet.asset.domain.model.VerificationTier.UNVERIFIED
import com.algorand.wallet.asset.domain.model.VerificationTier.VERIFIED
import javax.inject.Inject

class DefaultAssetMarketsDetailBadgeDescriptionMapper @Inject constructor() : AssetMarketsDetailBadgeDescriptionMapper {

    override fun invoke(verificationTier: VerificationTier): AssetMarketsDetail.BadgeDescription? {
        return when (verificationTier) {
            VERIFIED -> getVerifiedBadgeDescription()
            TRUSTED -> getTrustedBadgeDescription()
            SUSPICIOUS -> getSuspiciousBadgeDescription()
            UNVERIFIED, UNKNOWN -> null
        }
    }

    private fun getVerifiedBadgeDescription(): AssetMarketsDetail.BadgeDescription {
        return AssetMarketsDetail.BadgeDescription(
            backgroundColorResId = R.color.trusted_icon_bg_opacity_16,
            textColorResId = R.color.positive,
            drawableResId = R.drawable.ic_asa_trusted,
            titleTextResId = R.string.trusted_asa,
            descriptionTextResId = R.string.this_is_a_well_known
        )
    }

    private fun getTrustedBadgeDescription(): AssetMarketsDetail.BadgeDescription {
        return AssetMarketsDetail.BadgeDescription(
            backgroundColorResId = R.color.trusted_icon_bg_opacity_16,
            textColorResId = R.color.positive,
            drawableResId = R.drawable.ic_asa_trusted,
            titleTextResId = R.string.trusted_asa,
            descriptionTextResId = R.string.this_is_a_well_known
        )
    }

    private fun getSuspiciousBadgeDescription(): AssetMarketsDetail.BadgeDescription {
        return AssetMarketsDetail.BadgeDescription(
            backgroundColorResId = R.color.suspicious_icon_bg_opacity_16,
            textColorResId = R.color.negative,
            drawableResId = R.drawable.ic_asa_danger,
            titleTextResId = R.string.suspicious,
            descriptionTextResId = R.string.we_ve_received_reports_that,
        )
    }
}
