package com.algorand.android.module.pushtoken

import com.algorand.android.foundation.PeraResult
import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class FirebaseMessagingTokenProvider @Inject constructor() : MessagingTokenProvider {

    override suspend fun getToken(): PeraResult<String> = suspendCoroutine { continuation ->
        try {
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token ->
                    continuation.resume(PeraResult.Success(token))
                }
                .addOnFailureListener {
                    continuation.resume(PeraResult.Error(it))
                }
        } catch (exception: Exception) {
            continuation.resume(PeraResult.Error(exception))
        }
    }
}
