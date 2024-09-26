package com.algorand.android.module.custominfo.domain.usecase

fun interface SetCustomName {
    suspend operator fun invoke(address: String, name: String)
}
