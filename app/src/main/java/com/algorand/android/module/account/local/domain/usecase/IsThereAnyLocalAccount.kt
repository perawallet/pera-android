package com.algorand.android.module.account.local.domain.usecase

interface IsThereAnyLocalAccount {
    suspend operator fun invoke(): Boolean
}
