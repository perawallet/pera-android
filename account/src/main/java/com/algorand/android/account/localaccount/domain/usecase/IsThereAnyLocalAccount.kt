package com.algorand.android.account.localaccount.domain.usecase

interface IsThereAnyLocalAccount {
    suspend operator fun invoke(): Boolean
}
