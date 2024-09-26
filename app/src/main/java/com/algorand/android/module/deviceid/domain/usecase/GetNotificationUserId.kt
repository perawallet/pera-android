package com.algorand.android.module.deviceid.domain.usecase

fun interface GetNotificationUserId {
    operator fun invoke(): String?
}
