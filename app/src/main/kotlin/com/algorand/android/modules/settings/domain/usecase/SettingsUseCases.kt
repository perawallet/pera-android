package com.algorand.android.modules.settings.domain.usecase

fun interface GetMigratedTo6xCheck {
    suspend operator fun invoke(): Boolean
}

fun interface SaveMigratedTo6xCheck {
    suspend operator fun invoke(check: Boolean)
}
