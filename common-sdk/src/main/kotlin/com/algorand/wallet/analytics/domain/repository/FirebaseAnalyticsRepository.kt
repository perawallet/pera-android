package com.algorand.wallet.analytics.domain.repository

interface FirebaseAnalyticsRepository {
    suspend fun getFirebaseInstanceId(): String
}
