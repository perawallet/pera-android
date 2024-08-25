package com.algorand.android.deviceid.component.domain.usecase

fun interface SetNotificationUserId {
    operator fun invoke(userId: String?)
}
