package com.algorand.android.custominfo.component.domain.usecase

fun interface SetCustomName {
    suspend operator fun invoke(address: String, name: String)
}
