package com.algorand.android.deviceid.component.domain.usecase

fun interface GetNotificationUserId {
    operator fun invoke(): String?
}
