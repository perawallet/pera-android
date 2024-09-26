package com.algorand.android.module.deviceid.domain.usecase

fun interface SetNotificationUserId {
    operator fun invoke(userId: String?)
}
