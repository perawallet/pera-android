package com.algorand.wallet.analytics.domain.model

data class ReferrerData(
    val utmSource: String?,
    val utmMedium: String?,
    val utmCampaign: String?,
    val utmTerm: String?,
    val utmContent: String?
)
