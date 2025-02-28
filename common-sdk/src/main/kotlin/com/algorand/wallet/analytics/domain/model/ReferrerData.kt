package com.algorand.wallet.analytics.domain.usecases.model

data class ReferrerData(
    val utmSource: String?,
    val utmMedium: String?,
    val utmCampaign: String?,
    val utmTerm: String?,
    val utmContent: String?
)
