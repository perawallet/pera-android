package com.algorand.wallet.analytics.domain.service

interface PeraReferralInstallClient {
    suspend fun getReferrerUrl(): String?
}