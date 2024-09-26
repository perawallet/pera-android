package com.algorand.android.module.pushtoken

import com.algorand.android.foundation.PeraResult

interface MessagingTokenProvider {

    suspend fun getToken(): PeraResult<String>
}