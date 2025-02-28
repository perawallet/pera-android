package com.algorand.wallet.analytics.domain.service

interface PeraReferrerInstallClient {
    suspend fun getReferrerUrl(): String?
}
