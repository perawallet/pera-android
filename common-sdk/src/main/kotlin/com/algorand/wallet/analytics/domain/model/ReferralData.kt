package com.algorand.wallet.analytics.domain.usecases.model

data class ReferralData(
    val utmSource: String?,
    val utmMedium: String?,
    val utmCampaign: String?,
    val utmTerm: String?,
    val utmContent: String?
)
